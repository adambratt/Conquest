package com.blockempires.conquest;

public class Conquest implements Runnable {

	private ConquestPlugin plugin;
	
	public Conquest(ConquestPlugin plugin){
		this.plugin=plugin;
	}
	
	public void init(){
		loadConfig();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	
	public void loadConfig(){
		
	}
	
	public void installConfig(){
		
	}
	
}
