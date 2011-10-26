package com.blockempires.conquest.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

public class Area {
	
	private String name;
	private HashSet<Player> areaPlayers;
	private HashMap<Race, Integer> raceCount;
	private int currentHP;
	private int maxHP;
	private int captureSpeed = 1;	// Default for now
	private int captureTime;		// Counter until capture
	private int maxTime = 90;		// Default time to capture
	private int captureMomentum;	// 0 if not being captured, -1 if being recaptured by owner, 1 if being captured by another race
	private Race advantageRace;		// The race that currently has the advantage
	private Race ownerRace; 		// The race that currently controls the position
	private Race capturingRace;		// The race whose ticks are on the clock (only matters for neutral -> capture, not owner -> neutral)
	
	public Area(){
		resetCapture();
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
		
	}
	
	public void statusUpdate(){
		
	}
	
	public HashSet<Player> getPlayers(){
		return areaPlayers;
	}
	
	public void playerEnter(Player player){
		areaPlayers.add(player);
		
	}
	
	public void playerExit(Player player){
		areaPlayers.remove(player);
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
