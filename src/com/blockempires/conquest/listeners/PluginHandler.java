package com.blockempires.conquest.listeners;

import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.blockempires.conquest.ConquestPlugin;
import com.iConomy.iConomy;

public class PluginHandler extends ServerListener {
	private ConquestPlugin plugin;

	public PluginHandler(ConquestPlugin cPlugin) {
		this.plugin=cPlugin;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin eventp = event.getPlugin();
		try {
			if(eventp instanceof PermissionsEx){
				ConquestPlugin.permissions = (PermissionsEx) eventp;
				ConquestPlugin.info("Pex loaded late");
			}
			if(eventp instanceof iConomy){ 
				ConquestPlugin.iConomy = (iConomy) eventp;
				ConquestPlugin.info("iConomy loaded late");
			}
		 } catch (NoClassDefFoundError ex) {
			 return;
		 }
	}
}