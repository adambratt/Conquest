package com.blockempires.conquest.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.blockempires.conquest.ConquestPlugin;

public class PluginHandler implements Listener {
	private ConquestPlugin plugin;

	public PluginHandler(ConquestPlugin cPlugin) {
		this.plugin=cPlugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin eventp = event.getPlugin();
		try {
			if(eventp instanceof PermissionsEx){
				ConquestPlugin.permissions = (PermissionsEx) eventp;
				ConquestPlugin.info("Pex loaded late");
			}
		 } catch (NoClassDefFoundError ex) {
			 return;
		 }
	}
}