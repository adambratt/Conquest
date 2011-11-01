package com.blockempires.conquest.system;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blockempires.conquest.Conquest;
import com.blockempires.conquest.objects.Area;
import com.blockempires.conquest.objects.Race;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CommandHandler implements CommandExecutor {
	private Conquest conquest;
	
	public CommandHandler(Conquest conquest){
		this.conquest=conquest;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = null;
		if(sender instanceof Player){
			player = (Player) sender;
		}
		
		if(args.length < 1){
			player.sendMessage(ChatColor.GREEN+"-------- Conquest List --------");
			for (Area a : conquest.getAreas()){
				int playerCount = a.getPlayers().size();
				player.sendMessage(ChatColor.AQUA+a.getName()+"- "+ChatColor.DARK_PURPLE+"players:"+playerCount+" time:"+a.getTime()+"s race:"+a.getRace());
			}
			return true;
		}
		
		
		// Check permissions from here on out	
		if (player != null && !sender.isOp()) {
			if(!sender.hasPermission("conquest.edit")) return false;
		}
		
		if (args[0].equalsIgnoreCase("create")){
			// Used to create the regions
			if (args.length != 2 || player == null)
				return usageMsg(player);
			String regionName = args[1];
			if (args[1] == "create"){
				msgError(player, "You can not use that name for a Conquest area");
				return true;
			}
			ProtectedRegion region = conquest.getRegion(regionName, player.getWorld());
			if (region == null){
				msgError(player, "Invalid WorldGuard region");
				return true;
			}
			conquest.createArea(region, player.getWorld());
			msgSuccess(player, "Area '"+regionName+"' created!");
			return true;
			
		} else {
			// Args 0 will be the region name of the area we are modifying
			Area area = conquest.getArea(args[0]);
			if (area == null){
				msgError(player, "Invalid Conquest area");
				return true;
			}
			
			// If there's no arguments, return the info for the area
			if(args.length == 1){
				int playerCount = area.getPlayers().size();
				player.sendMessage(ChatColor.GREEN+"-------- "+ChatColor.BLUE+args[0]+ChatColor.GREEN+" --------");
				player.sendMessage(ChatColor.YELLOW+"players: "+ChatColor.WHITE+playerCount);
				player.sendMessage(ChatColor.YELLOW+"time: "+ChatColor.WHITE+area.getTime()+"s");
				player.sendMessage(ChatColor.YELLOW+"race: "+ChatColor.WHITE+area.getRace());
				for (Map.Entry<Race, Integer> entry : area.getRaceCount().entrySet()) {
					player.sendMessage(ChatColor.YELLOW+entry.getKey().getName()+": "+ChatColor.WHITE+entry.getValue());
				}
				// More info to go here eventually
				return true;
			}
			
		}
		
		return usageMsg(player);
	}
	
	public void msgSuccess(Player player, String msg){
		if (player == null){
			
		} else {
			player.sendMessage(ChatColor.GREEN+"[Conquest] "+msg);
		}
	}
	
	public void msgError(Player player, String msg){
		if (player == null){
			
		} else {
			player.sendMessage(ChatColor.RED+"[Conquest] "+msg);
		}
	}
	
	public boolean usageMsg(Player player){
		msgError(player, "Improper command!");
		return true;
	}

}
