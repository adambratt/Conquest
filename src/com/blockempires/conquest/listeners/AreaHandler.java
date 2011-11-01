package com.blockempires.conquest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.blockempires.conquest.objects.Area;

public class AreaHandler {
	
	private Area area;
	
	public AreaHandler(Area area){
		this.area = area;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if (area.inArea(p)){
				
			}
		}
	}
}
