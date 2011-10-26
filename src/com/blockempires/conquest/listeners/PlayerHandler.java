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

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerJoin(event);
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerMove(event);
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerQuit(event);
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerRespawn(event);
		}
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerKick(event);
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onPlayerTeleport(event);
		}
	}

}
