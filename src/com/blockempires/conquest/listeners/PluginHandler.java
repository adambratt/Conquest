package com.blockempires.conquest.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.ConquestPlugin;
import com.iConomy.iConomy;

public class PluginHandler extends ServerListener {
	private Conquest conquest;

	public PluginHandler(Conquest conquest) {
		this.conquest=conquest;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin plugin = event.getPlugin();
		if (plugin instanceof iConomy){
			ConquestPlugin.iConomy = (iConomy) plugin;
		}
	}
	
	

}
