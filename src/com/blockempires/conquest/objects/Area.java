package com.blockempires.conquest.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.ConquestPlugin;
import com.blockempires.conquest.listeners.AreaHandler;
import com.iConomy.iConomy;
import com.iConomy.system.Account;
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
	private int maxTime = 180;		// Default time to capture
	private int captureMomentum;	// 0 if not being captured, -1 if being recaptured by owner, 1 if being captured by another race
	private int maxHourly = 0;		// 0 for infinite number, otherwise a number for max number of captures per hour
	private int advantage = 2;		// The necessary advantage by the leading race
	
	private Race advantageRace;		// The race that currently has the advantage
	private Race ownerRace; 		// The race that currently controls the position
	private Race capturingRace;		// The race whose ticks are on the clock (only matters for neutral -> capture, not owner -> neutral)
	
	public Area(ProtectedRegion region, World world){
		this.name = region.getId();
		this.region = region;
		this.world = world;
		areaPlayers = new HashSet<Player>();
		raceCount = new HashMap<Race, Integer>();
		advantageRace = null;
		ownerRace = null;
		capturingRace = null;
		
		resetCapture();
		init();
	}
	
	public Area(ProtectedRegion region, World world, Race owner, String label){
		this(region, world);
		this.ownerRace = owner;
		this.name = label;
	}
	
	private void init(){
		aHandler = new AreaHandler(this);
		raceCount = getRaces();
	}
	
	private HashMap<Race, Integer> getRaces(){
		Set<Race> racelist = Race.getRaceList();
		HashMap<Race, Integer> races = new HashMap<Race, Integer>();
		for (Race r : racelist){
			races.put(r, 0);
		}
		return races;
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
	
	public void save(){
		String raceName = "";
		if (ownerRace != null && ownerRace.getName() != null)
			raceName = ownerRace.getName();
		Conquest.getDB().query("insert into conquest_areas(name,region,world,`time`,`timemodifier`,`maxhourly`,`owner`,`advantage`) values('"+name+"','"+region.getId()+"','"+world.getName()+"','"+maxTime+"','"+captureSpeed+"','"+maxHourly+"','"+raceName+"','"+advantage+"')");
		ConquestPlugin.info("Area for '"+name+"' was saved!");
	}
	
	public void runCapture(){
		// Lets just check the advantage quick
		updateAdvantage();
		
		if (captureMomentum == 0){
			// Nothing happening, reset the clock and get out of here
			resetCapture();
			return;
			
		} else if (captureMomentum == -1){
			// Being returned to owner
			captureTime -= captureSpeed;
			if (captureTime <= 0){
				resetCapture();
				if (ownerRace != null)
					rewardRace(ownerRace, Conquest.defendMoney);
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
					statusCaptured(ownerRace.getName());
				} else {
					// Neutral capture
					ownerChange(null);
					resetCapture();
					statusNeutralized();
				}
			}
			
		}
		if (captureTime > 0 && (captureTime % 10) == 0)
			statusUpdate();
	}
	
	private void updateAdvantage(){
		// Initialize for calculating the new advantage
		int leadCount = 0;
		int secondCount = 0;
		Race newAdvantage = null;
		HashSet<Player> newPlayers = new HashSet<Player>();
		HashMap<Race, Integer> newCount = getRaces();
		
		// Build Race Counts
		for (Player p : world.getPlayers()){
			if (!p.isDead() && inRegion(p.getLocation())){
				Race playerRace = Race.getRace(p);
				if (playerRace != null){
					newPlayers.add(p);
					newCount.put(playerRace, newCount.get(playerRace)+1);
				}
			}
		}
		
		// Calculate Advantage
		for (Map.Entry<Race, Integer> entry : newCount.entrySet()) {
			if (entry.getValue() > leadCount){
				secondCount = leadCount;
				leadCount = entry.getValue();
				newAdvantage = entry.getKey();
			} else if (entry.getValue() == leadCount && leadCount > 0){
				// If there are two races tied for first, there is no advantage
				newAdvantage = null;
			} else if (entry.getValue() > secondCount){
				secondCount = entry.getValue();
			}
		}
		
		// If less than required advantage, set advantage to null
		if (leadCount < secondCount+advantage)
			newAdvantage = null;
		
		
		// If Advantage has changed, take action
		if (newAdvantage != advantageRace){
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
					statusCapturing(capturingRace.getName());
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
		
		// Assign new player/race count
		raceCount = newCount;
		areaPlayers = newPlayers;
	}
	
	public void ownerChange(Race owner){
		//  Update object owner
		ownerRace = owner;
		
		// Get Name for MySQL
		String raceName = "";
		if (ownerRace != null && ownerRace.getName() != null){
			raceName = ownerRace.getName();
			rewardRace(ownerRace, Conquest.captureMoney);
		}
		
		// Update config/SQL
		Conquest.getDB().query("update conquest_areas set `owner`='"+raceName+"' where `region`='"+region.getId()+"'");
	}
	
	public void rewardRace(Race race, int money){
		for (Player p : getRacePlayers(race)){
			Account account = iConomy.getAccount(p.getName());
			if(account!=null) account.getHoldings().add(money);
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
		if (region != null && region.contains(v)) {
		     return true;
		}
		return false;
	}
	
	public boolean inArea(Player p){
		return areaPlayers.contains(p);
	}
	
	public void sendStatus(String status){
		
	}
	
	public void sendStatusGlobal(String status){
		Bukkit.getServer().broadcastMessage(ChatColor.AQUA+"[Conquest] "+ChatColor.GREEN+status);
	}
	
	public void statusUpdate(){		
		String status = ChatColor.AQUA+"Start ["+ChatColor.RED;
		for (int i = 0; i < maxTime; i++){
			if ((i % 10) == 0){
				if (captureTime >= i){
					status += "x";
				} else {
					status += "-";
				}
			}
		}
		status += "] Captured";
		for (Player p : areaPlayers ){
			p.sendMessage(ChatColor.GREEN+"-------- Conquest of "+name+" --------");
			p.sendMessage(status);
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
	
	public void statusCapturing(String raceName){
		String message = "Attempts are being made to capture  "+name+" "+ChatColor.AQUA+"("+raceName+")";
		sendStatusGlobal(message);
	}
	
	public void statusCaptured(String raceName){
		String message = name+" has been successfully captured "+ChatColor.AQUA+"("+raceName+")";
		sendStatusGlobal(message);
	}
	
	public void statusRestored(){
		String message = "Order in "+name+" has been restored!";
		sendStatusGlobal(message);
	}

	public String getRace() {
		if (ownerRace == null)
			return "Neutral";
		return ownerRace.getName();
	}

	public int getTime() {
		return captureTime;
	}

	public HashMap<Race, Integer> getRaceCount() {
		return raceCount;
	}
	
}
