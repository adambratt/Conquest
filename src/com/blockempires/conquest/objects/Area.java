package com.blockempires.conquest.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.blockempires.conquest.listeners.AreaHandler;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Area {
	
	private String name;
	private World world;
	private ProtectedRegion region;
	private AreaHandler aHandler;
	private HashSet<Player> areaPlayers;
	private HashMap<Race, Integer> raceCount;
	
	private int captureSpeed = 1;	// Default for now
	private int captureTime;		// Counter until capture
	private int maxTime = 90;		// Default time to capture
	private int captureMomentum;	// 0 if not being captured, -1 if being recaptured by owner, 1 if being captured by another race
	
	private Race advantageRace;		// The race that currently has the advantage
	private Race ownerRace; 		// The race that currently controls the position
	private Race capturingRace;		// The race whose ticks are on the clock (only matters for neutral -> capture, not owner -> neutral)
	
	public Area(ProtectedRegion region, World world){
		this.name = region.getId();
		this.region = region;
		this.world = world;
		aHandler = new AreaHandler(this);
		areaPlayers = new HashSet<Player>();
		raceCount = new HashMap<Race, Integer>();
		advantageRace = null;
		ownerRace = null;
		capturingRace = null;
		
		loadRaces();
		resetCapture();
	}
	
	public Area(ProtectedRegion region, World world, Race owner){
		this(region, world);
		this.ownerRace = owner;
	}
	
	private void loadRaces(){
		Set<Race> racelist = Race.getRaceList();
		for (Race r : racelist){
			raceCount.put(r, 0);
		}
	}

	public void resetCapture(){
		captureTime = 0;
		captureMomentum = 0;
		capturingRace = null;
	}
	
	public boolean isRunning(){
		if (captureMomentum == 0 && captureTime == 0)
			return false;
		return true;
	}
	
	public void runCapture(){
		// Lets just double check the advantage quick
		/// updateAdvantage();
		
		if (captureMomentum == 0){
			// Nothing happening, reset the clock and get out of here
			resetCapture();
			return;
			
		} else if (captureMomentum == -1){
			// Being returned to owner
			captureTime -= captureSpeed;
			if (captureTime <= 0){
				resetCapture();
				statusRestored();
			}
			
		} else if (captureMomentum == 1){
			// Being captured
			captureTime += captureSpeed;
			if (captureTime >= maxTime){
				if (ownerRace == null){
					// Player capture
					ownerChange(capturingRace);
					resetCapture();
					statusCaptured();
				} else {
					// Neutral capture
					ownerChange(capturingRace);
					resetCapture();
					statusNeutralized();
				}
			}
			
		}
	}
	
	public Race getAdvantage(){
		int maxCount = 0;
		int secondCount = 0;
		Race maxRace = null;
		for (Map.Entry<Race, Integer> entry : raceCount.entrySet()) {
			if (entry.getValue() > maxCount){
				secondCount = maxCount;
				maxCount = entry.getValue();
				maxRace = entry.getKey();
			} else if (entry.getValue() == maxCount && maxCount > 0){
				// If there are two races tied for first, there is no advantage
				maxRace = null;
			} else if (entry.getValue() > secondCount){
				secondCount = entry.getValue();
			}
		}
		// If less than +2 advantage, remove advantage
		if (maxCount < secondCount+2)
			maxRace = null;
		return maxRace;
	}
	
	public void updateAdvantage(){
		Race newAdvantage = getAdvantage();
		if (newAdvantage != advantageRace){
			// Advantage has changed, take action
			
			if (ownerRace == null){
				// Phase is neutral -> captured
				if (newAdvantage == null){
					// Capture clock goes down now
					captureMomentum = -1;
				} else if (newAdvantage != capturingRace) {
					// A new race is capturing, reset clock
					resetCapture();
					captureMomentum = 1;
					capturingRace = newAdvantage;
					statusCapturing();
				} else {
					// Capturing race has gained the edge again
					captureMomentum = 1;
				}
			} else {
				// Phase is owner -> neutral
				if (newAdvantage == ownerRace && captureMomentum == 0)
					// Owner is just strolling on their property
					return;
				if (newAdvantage == ownerRace){
					// Owner is reclaiming
					captureMomentum = -1;
				} else if (newAdvantage == null){
					// Attacker has died, gotten bored, or been disadvantaged
					captureMomentum = -1;
				} else {
					// Someone is capturing it
					captureMomentum = 1;
					statusNeutralizing();
				}
			}
			// And assign the new advantage!
			advantageRace = newAdvantage;
		}
	}
	
	public void ownerChange(Race owner){
		ownerRace = owner;
		// Update config/SQL
	}
	
	public void sendStatus(String status){
		
	}
	
	public void sendStatusGlobal(String status){
		Bukkit.getServer().broadcastMessage("[Conquest] "+status);
	}
	
	public void statusUpdate(){
		String message = "------ Conquest of "+name+" ------ \n";
		for (Player p : areaPlayers ){
			p.sendMessage(message);
		}
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String string){
		this.name = string;
	}
	
	public HashSet<Player> getPlayers(){
		return areaPlayers;
	}
	
	public HashSet<Player> getRacePlayers(Race race){
		HashSet<Player> racePlayers = new HashSet<Player>();
		for (Player p : racePlayers){
			if (Race.getRace(p) == race){
				racePlayers.add(p);
			}
		}
		return racePlayers;
	}
	
	public ProtectedRegion getRegion(){
		return region;
	}
	
	public World getWorld(){
		return world;
	}
	
	public AreaHandler getHandler(){
		return aHandler;
	}
	
	public boolean inRegion(Location loc) {
		com.sk89q.worldedit.Vector v = new com.sk89q.worldedit.Vector(loc.getX(), loc.getY(), loc.getZ());
		if (region.contains(v)) {
		     return true;
		}
		return false;
	}
	
	public boolean inArea(Player p){
		return areaPlayers.contains(p);
	}
	
	public void playerEnter(Player player){
		Race playerRace = Race.getRace(player);
		if (playerRace != null){
			areaPlayers.add(player);
			raceCount.put(playerRace, raceCount.get(playerRace)+1);
			updateAdvantage();
		}
	}
	
	public void playerExit(Player player){
		Race playerRace = Race.getRace(player);
		if (playerRace != null){
			areaPlayers.remove(player);
			raceCount.put(playerRace, raceCount.get(playerRace)-1);
			updateAdvantage();
		}
	}
	
	public void statusNeutralizing(){
		String message = "An uprising has started in "+name+"!";
		sendStatusGlobal(message);
	}
	
	public void statusNeutralized(){
		String message = "Civil rebellion has caused "+name+" to fall into a neutral state!";
		sendStatusGlobal(message);
	}
	
	public void statusCapturing(){
		String message = "Attempts are being made to capture  "+name+"!";
		sendStatusGlobal(message);
	}
	
	public void statusCaptured(){
		String message = name+" has been successfully captured!";
		sendStatusGlobal(message);
	}
	
	public void statusRestored(){
		String message = "Order in "+name+" has been restored!";
		sendStatusGlobal(message);
	}
	
}
