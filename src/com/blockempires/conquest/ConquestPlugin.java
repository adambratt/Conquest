package com.blockempires.conquest;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ConquestPlugin extends JavaPlugin{
	private static WorldGuardPlugin wgPlugin;

	@Override
	public void onDisable() {
		
	}

	@Override
	public void onEnable() {
		
	}
	
	//Console loggers
	public static void info(String msg)    { Bukkit.getServer().getLogger().info("[Conquest] " + msg); }
    public static void warning(String msg) { Bukkit.getServer().getLogger().warning("[Conquest] " + msg); }    
    public static void error(String msg)   { Bukkit.getServer().getLogger().severe("[Conquest] " + msg); }
	
	public static WorldGuardPlugin getWorldGuard(){
		return ConquestPlugin.wgPlugin;
	}

}
