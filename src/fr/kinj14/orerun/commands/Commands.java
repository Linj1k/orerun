package fr.kinj14.orerun.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.Report;
import fr.kinj14.orerun.listeners.ChatListeners;
import fr.kinj14.orerun.utils.GameState;

public class Commands implements CommandExecutor {
	private Main main = Main.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		//orerun.canchangeteam
		if(cmd.getName().equalsIgnoreCase("canchangeteam")  && sender.hasPermission("orerun.canchangeteam")) {
			CanChangeTeam(sender, args);
			return true;
		}
		//orerun.restartgame
		if(cmd.getName().equalsIgnoreCase("restartgame") && sender.hasPermission("orerun.restartgame")) {
			RestartGame(sender);
			return true;
		}
		//orerun.healall
		if(cmd.getName().equalsIgnoreCase("healall") && sender.hasPermission("orerun.healall")) {
			for(Player player : main.getPlayers()) {
				player.setHealth(player.getHealthScale());
			}
			return true;
		}
		
		
		if(sender instanceof Player) {		
			//orerun.heal
			if(cmd.getName().equalsIgnoreCase("heal") && sender.hasPermission("orerun.heal")) {
				Player player = (Player) sender;
				player.setHealth(player.getHealthScale());
				return true;
			}
			//orerun.gm
			if(cmd.getName().equalsIgnoreCase("gm") && sender.hasPermission("orerun.gamemode")) {
				Player player = (Player) sender;
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode "+Integer.valueOf(args[0])+" "+player.getName());
				return true;
			}
			
			//lobby
			if(cmd.getName().equalsIgnoreCase("lobby")) {
				Player player = (Player) sender;
				if(main.getState() == GameState.WAITING) {
					player.teleport(main.lobby);
				}
				return true;
			}
			//Global Chat
			if(cmd.getName().equalsIgnoreCase("!")) {
				Player player = (Player) sender;
				sender.getServer().broadcastMessage(ChatListeners.getInstance().FormatChat(player, msg));
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("report")) {	
				Player owner = (Player) sender;
				Player player = getPlayerFromString(args[0]);
				if(player != null && player != owner) {				
					if(!argsToText(args).replace(args[0], "").isEmpty()) {						
						new Report().reportPlayer((Player)sender, getPlayerFromString(args[0]), argsToText(args).replace(args[0], ""));
						return true;
					}
					sender.sendMessage("§7[§eOreRun§7]§4 Please enter a reason!");
					return false;
				} else {
					sender.sendMessage("§7[§eOreRun§7]§4 The player doesn't exist!");
					return false;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("bug")) {
				new Report().reportBug(Bukkit.getServer().getServerName(), (Player)sender, argsToText(args));
				return true;
			}
		}
		
		
		return false;
	}
	
	public String argsToText(String[] args) {
		String value = args[0];
		for(String arg : args) {
			if(args[0] != arg) {				
				value = value + " " + arg;
			}
		}
		return value;
	}
	
	public Player getPlayerFromString(String playerName) {
		return Bukkit.getPlayer(playerName);
	}
	
	public boolean CommandContains(String[] args, String ContainsString) {
		for(String arg : args) {
			if(arg.equals(ContainsString)) {
				return true;
			}
		}
		return false;
	}
	
	public void CanChangeTeam(CommandSender sender, String[] args) {
		if(CommandContains(args, "true") || CommandContains(args, "false")) {
			main.setCanChangeTeam(Boolean.valueOf(args[0]));			
		} else {
			main.setCanChangeTeam(!main.GetCanChangeTeam());
		}
		
		if(main.GetCanChangeTeam()) {
			sender.sendMessage("§7[§eOreRun§7]§6 Team changes have been activated !");
		} else {
			sender.sendMessage("§7[§eOreRun§7]§6 Team changes have been deactivated !");
		}
	}
	
	public void RestartGame(CommandSender sender) {
		main.Reload();
		Bukkit.broadcastMessage("§7[§eOreRun§7]§6 The game is restarting !");
	}

}
