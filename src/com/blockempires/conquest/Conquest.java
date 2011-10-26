package com.blockempires.conquest;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;

import com.blockempires.conquest.objects.Area;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lib.PatPeter.SQLibrary.*;

public class Conquest implements Runnable {

	private ConquestPlugin plugin;
	private HashSet<Area> areaList;
	private int thread;
	private Configuration config;
	public MySQL db;
	
	public Conquest(ConquestPlugin plugin){
		this.plugin=plugin;
	}
	
	public void init(){
		loadConfig();
		loadDatabase();
		loadAreas();
	}
	

	@Override
	public void run() {
		for (Area a : areaList){
			if(a.isRunning()){
				a.runCapture();
			}
		}
		
	}
	
	private void loadAreas(){
		if(thread > 0){ 
			Bukkit.getServer().getScheduler().cancelTask(thread);
			for (Area a : areaList){
				a.resetCapture();
			}
		}
		thread = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 20, 20);
	}
	
	public Set<Area> getAreas(){
		return areaList;
	}
	
	
	private void loadConfig(){
		config = plugin.getConfig();
		String hostname = config.getString("database.host","localhost");
		String database = config.getString("database.database", "minecraft");
		String username = config.getString("database.username", "root");
		String port = config.getString("database.port", "3306");
		String password = config.getString("database.password", "password");
		this.db = new MySQL(plugin.getServer().getLogger(), "[Conquest] ", hostname, port, database, username, password);
	}
	
	private void loadDatabase(){
		if (db.getConnection() != null){
			installCheck();
			
		} else {
			ConquestPlugin.error("Could not connect to database, check configuration. Conquest is disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	
	private void installCheck(){
		if (!db.checkTable("mobster_dungeons")){
			String query = "create table mobster_dungeons(id int not null auto_increment, primary key(id), name varchar(80) not null)";
			db.createTable(query);
		}
		if (!db.checkTable("mobster_rooms")){
			String query = "create table mobster_rooms(id int not null auto_increment, primary key(id), name varchar(80) not null, world varchar(80) not null, dungeon varchar(80) not null)";
			db.createTable(query);
		}
		if (!db.checkTable("mobster_spawners")){
			String query = "create table mobster_spawners(id int not null auto_increment, primary key(id), name varchar(80) not null, creature varchar(80) not null, room varchar(80) not null, health int not null, speed int not null, size int not null, `limit` int not null, x double not null, y double not null, z double not null)";
			db.createTable(query);
		}
	}

	public void createArea(ProtectedRegion region, World world) {
		Area area = new Area(region, world);
		areaList.add(area);
	}

	public Area getArea(String string) {
		for (Area a : areaList){
			if (a.getRegion().getId().equalsIgnoreCase(string)){
				return a;
			}
		}
		return null;
	}
	
	public ProtectedRegion getRegion(String regionName, World world){
		ProtectedRegion region=ConquestPlugin.getWorldGuard().getRegionManager(world).getRegion(regionName);
		if (region==null) return null;
		return region;
	}
	
}
