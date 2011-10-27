package com.blockempires.conquest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

import com.blockempires.conquest.objects.Area;
import com.blockempires.conquest.objects.Race;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lib.PatPeter.SQLibrary.*;

public class Conquest implements Runnable {

	private ConquestPlugin plugin;
	private HashSet<Area> areaList;
	private int thread;
	private Configuration config;
	private MySQL db;
	private static MySQL dbstatic;
	
	public Conquest(ConquestPlugin plugin){
		this.plugin=plugin;
	}
	
	public void init(){
		areaList = new HashSet<Area>();
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

	public void createArea(ProtectedRegion region, World world) {
		Area area = new Area(region, world);
		area.save();
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
	
	
	private void loadConfig(){
		config = plugin.getConfiguration();
		String hostname = config.getString("database.host","localhost");
		String database = config.getString("database.database", "minecraft");
		String username = config.getString("database.username", "root");
		String port = config.getString("database.port", "3306");
		String password = config.getString("database.password", "password");
		this.db = new MySQL(plugin.getServer().getLogger(), "[Conquest] ", hostname, port, database, username, password);
		config.save();
	}
	
	private void loadDatabase(){
		if (db.getConnection() != null){
			
			// Check if tables are created, if not install/create them
			installCheck();
			Conquest.dbstatic = db;
			
			// Load Areas
			ResultSet areaResult = db.query("select * from conquest_areas");
			try {
				while ( areaResult.next() ){
					World world = plugin.getServer().getWorld(areaResult.getString("world"));
					if (world == null)
						continue;
					ProtectedRegion region = getRegion(areaResult.getString("region"), world);
					if (region == null)
						continue;
					Area a = new Area(region, world, Race.getRace(areaResult.getString("owner")), areaResult.getString("name"));
					areaList.add(a);
				}
			} catch (SQLException e) {
				ConquestPlugin.error("Disabling. SQL failed with: "+e.getMessage());
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return;
			}
		} else {
			ConquestPlugin.error("Could not connect to database, check configuration. Conquest is disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
	
	private void installCheck(){
		if (!db.checkTable("conquest_areas")){
			String query = "create table conquest_areas(id int not null auto_increment, primary key(id), name varchar(40) not null, region varchar(40) not null, world varchar(40) not null, `time` int not null, timemodifier int not null, maxhourly int not null, owner varchar(40) not null, advantage int not null)";
			db.createTable(query);
		}
		if (!db.checkTable("conquest_captures")){
			String query = "create table conquest_captures(id int not null auto_increment, primary key(id), race varchar(40) not null, area_id int not null, `timestamp` datetime)";
			db.createTable(query);
		}
		if (!db.checkTable("conquest_captures_player")){
			String query = "create table conquest_captures_player(id int not null auto_increment, primary key(id), player varchar(40) not null, capture_id int not null, `timestamp` datetime)";
			db.createTable(query);
		}
	}
	
	public ProtectedRegion getRegion(String regionName, World world){
		ProtectedRegion region=ConquestPlugin.getWorldGuard().getRegionManager(world).getRegion(regionName);
		if (region==null) return null;
		return region;
	}
	
	public static MySQL getDB(){
		return Conquest.dbstatic;
	}

	public void shutdown() {
		// Nothing needed at the moment
		return;
	}
	
}
