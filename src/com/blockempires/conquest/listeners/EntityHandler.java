package com.blockempires.conquest.listeners;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.objects.Area;

public class EntityHandler extends EntityListener {

	private Conquest conquest;
	
	public EntityHandler(Conquest conquest){
		this.conquest = conquest;
	}
	
	public void onEntityDeath(EntityDeathEvent event) {
		for (Area a : conquest.getAreas()){
			a.getHandler().onEntityDeath(event);
		}
	}

}
