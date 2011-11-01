package com.blockempires.conquest.listeners;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.objects.Area;

public class PlayerHandler extends PlayerListener {
	
	private Conquest conquest;
	
	public PlayerHandler(Conquest conquest){
		this.conquest = conquest;
	}

}
