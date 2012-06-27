package com.blockempires.conquest.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;


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
