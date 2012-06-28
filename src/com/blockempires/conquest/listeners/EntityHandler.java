package com.blockempires.conquest.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.objects.Area;

public class EntityHandler implements Listener {

	private Conquest conquest;
	
	public EntityHandler(Conquest conquest){
		this.conquest = conquest;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDeath(EntityDeathEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onEntityDeath(event);
		}
	}

}
