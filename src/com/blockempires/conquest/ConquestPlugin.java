package com.blockempires.conquest;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.blockempires.conquest.listeners.EntityHandler;
import com.blockempires.conquest.listeners.PlayerHandler;
import com.blockempires.conquest.listeners.PluginHandler;
import com.blockempires.conquest.system.CommandHandler;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.iConomy.iConomy;

public class ConquestPlugin extends JavaPlugin{
	private static WorldGuardPlugin wgPlugin;
	public static iConomy iConomy;
	private File dir;
	private Conquest conquest;
	private PluginManager pManage;
	public static PermissionsEx permissions;
	
	public void onDisable() {
		if (conquest != null){
			conquest.shutdown();
		}
		info("Conquest disabled");
	}

	public void onEnable() {
		//Get Folder setup
		dir=getDataFolder();
		if(!dir.exists()) dir.mkdir();
				
		// Setup plugin dependencies
		pManage=getServer().getPluginManager();
		loadDependencies();
		
		// Setup Conquest Main
		conquest = new Conquest(this);
		conquest.init();
		
		// Setup Events + Commands
		loadEvents();
		loadCommands();
		
		info("Conquest v"+getDescription().getVersion()+" has been enabled.");
	}
	
	private void loadDependencies() {
		// World Guard
    	if (pManage.isPluginEnabled("WorldGuard")){
			Plugin wg = pManage.getPlugin("WorldGuard");
			if (wg instanceof WorldGuardPlugin)
				wgPlugin = (WorldGuardPlugin) wg;
		} else {
			error("WorldGuard does not appear to be installed and is REQUIRED by Conquest");
		}
    	if (pManage.isPluginEnabled("PermissionsEx")){
    		Plugin pex = pManage.getPlugin("PermissionsEx");
    		if (pex instanceof PermissionsEx){
    			permissions = (PermissionsEx) pex;
    			info("PermissionsEx has been enabled!");
    		}
    	}else {
    		error("PermissionsEx Broke!");
    	}
    	// iConomy
    	if (pManage.isPluginEnabled("iConomy")){
			Plugin econ = pManage.getPlugin("iConomy");
			if (econ instanceof iConomy)
				iConomy = (iConomy) econ;
		} else {
			error("iConomy does not appear to be installed and is REQUIRED by Conquest");
		}
	}

	private void loadCommands() {
		try {
			getCommand("conquest").setExecutor(new CommandHandler(this.conquest));
		} catch (Exception e) {
			error("Error: Commands not defined in 'plugin.yml'");
		}
	}

	private void loadEvents() {
		EntityListener entityListener = new EntityHandler(this.conquest);
		PlayerListener playerListener = new PlayerHandler(this.conquest);
		ServerListener pluginListener = new PluginHandler(this);
		pManage.registerEvent(Event.Type.ENTITY_DEATH, entityListener, Priority.Monitor, this);
		pManage.registerEvent(Event.Type.PLUGIN_ENABLE, pluginListener, Priority.Monitor, this);
	}
	

	//Console loggers
	public static void info(String msg)    { Bukkit.getServer().getLogger().info("[Conquest] " + msg); }
    public static void warning(String msg) { Bukkit.getServer().getLogger().warning("[Conquest] " + msg); }    
    public static void error(String msg)   { Bukkit.getServer().getLogger().severe("[Conquest] " + msg); }
	
	public static WorldGuardPlugin getWorldGuard(){
		return ConquestPlugin.wgPlugin;
	}
	
	public static PermissionsEx getpermissions(){
		return ConquestPlugin.permissions;
	}
	
	public static iConomy getiConomy(){
		return ConquestPlugin.iConomy;
	}

}
