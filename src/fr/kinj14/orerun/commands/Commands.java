package fr.kinj14.orerun.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.listeners.ChatListeners;

public class Commands implements CommandExecutor {
	private Main main = Main.getInstance();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		//orerun.canchangeteam
		if(cmd.getName().equalsIgnoreCase("!")) {
			Player player = (Player) sender;
			sender.getServer().broadcastMessage(ChatListeners.getInstance().FormatChat(player, msg));
			return true;
		}
		//orerun.canchangeteam
		if(cmd.getName().equalsIgnoreCase("orerun.canchangeteam")) {
			CanChangeTeam(sender, args);
			return true;
		}
		//orerun.restartgame
		if(cmd.getName().equalsIgnoreCase("orerun.restartgame")) {
			RestartGame(sender);
			return true;
		}
		
		return false;
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
