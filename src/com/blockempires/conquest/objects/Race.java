package com.blockempires.conquest.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class Race {
	private String name;
	private static Set<Race> racelist;
	
	public Race(String name) {
		this.name=name;
	}

	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}

	public static Race getRace(Player player) {
		for (Race r : getRaceList()){
			if(r.getName().equalsIgnoreCase("Dwarf") && player.hasPermission("lineage.race.Dwarf"))
				return r;
			if(r.getName().equalsIgnoreCase("Elf") && player.hasPermission("lineage.race.Elf"))
				return r;
			if(r.getName().equalsIgnoreCase("Human") && player.hasPermission("lineage.race.Human"))
				return r;
			if(r.getName().equalsIgnoreCase("Orc") && player.hasPermission("lineage.race.Orc"))
				return r;
		}
		return null;
	}
	
	public static Set<Race> getRaceList() {
		if(racelist == null || racelist.isEmpty()){
			racelist = new HashSet<Race>();
			//This is very bad and manual for now
			Race dwarf = new Race("Dwarf");
			racelist.add(dwarf);
			Race elf = new Race("Elf");
			racelist.add(elf);
			Race human = new Race("Human");
			racelist.add(human);
			Race orc = new Race("Orc");
			racelist.add(orc);
			return racelist;			
		}
		return racelist;
	}

	public static Race getRace(String string) {
		for (Race r : getRaceList()){
			if(r.getName().equalsIgnoreCase(string))
				return r;
		}
		return null;
	}
}
