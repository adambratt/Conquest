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
		area=this.area;
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		if (area.inRegion(p.getLocation()) && !area.inArea(p)){
			area.playerEnter(p);
		}
	}
	
	public void onPlayerKick(PlayerKickEvent event){
		Player p = event.getPlayer();
		if (area.inArea(p)){
			area.playerExit(p);
		}
	}

	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (area.inRegion(event.getTo())){
			if (!area.inArea(p)){
				area.playerEnter(p);
			}
		} else if (area.inRegion(event.getFrom())){
			if (area.inArea(p)){
				area.playerExit(p);
			}
		}
		
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (area.inArea(p))
			area.playerExit(p);
	}

	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player p = event.getPlayer();
		if (area.inRegion(event.getRespawnLocation()) && !area.inArea(p)){
			area.playerEnter(p);
		}
	}

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player p = event.getPlayer();
		
		if (area.inRegion(event.getTo())){
			if (!area.inArea(p)){
				area.playerEnter(p);
			}
		} else if (area.inRegion(event.getFrom())){
			if (area.inArea(p)){
				area.playerExit(p);
			}
		}
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
			if (area.inArea(p)){
				area.playerExit(p);
			}
		}
	}
}
