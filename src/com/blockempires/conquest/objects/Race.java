package com.blockempires.conquest.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class Race {
	private String name;
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}

	public static Race getRace(Player player) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Set<Race> getRaceList() {
		Set<Race> racelist = new HashSet<Race>();
		//This is very bad and manual for now
		return racelist;
	}
}
