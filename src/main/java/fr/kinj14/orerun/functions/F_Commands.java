package fr.kinj14.orerun.functions;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.listeners.ChatListeners;

public class F_Commands implements CommandExecutor {
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
		
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			//orerun.feed
			if(cmd.getName().equalsIgnoreCase("feed") && sender.hasPermission("orerun.feed")) {
				if(args.length > 0 && args[0].equalsIgnoreCase("@a")) {					
					for(Player play : main.getPlayers()) {
						play.setFoodLevel(20);
					}
				} else {
					player.setFoodLevel(20);
				}
				return true;
			}
			//orerun.heal
			if(cmd.getName().equalsIgnoreCase("heal") && sender.hasPermission("orerun.heal")) {
				if(args.length > 0 && args[0].equalsIgnoreCase("@a")) {					
					for(Player play : main.getPlayers()) {
						play.setHealth(play.getHealthScale());
					}
				} else {
					player.setHealth(player.getHealthScale());
				}
				return true;
			}
			//orerun.gm
			if(cmd.getName().equalsIgnoreCase("gm") && sender.hasPermission("orerun.gamemode")) {
				if(args.length > 0) {					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode "+Integer.valueOf(args[0])+" "+player.getName());
				}
				return true;
			}

			//orerun.pvp
			if(cmd.getName().equalsIgnoreCase("pvp") && sender.hasPermission("orerun.pvp")) {
				switch(args[0]){
					default:
						main.SetPVPState(!main.GetPVPState());
						if(main.GetPVPState()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_PVPACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_PVPDESACTIVATE.get());
						}
					case "false":
						main.SetPVPState(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_PVPDESACTIVATE.get());
					case "true":
						main.SetPVPState(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_PVPACTIVATE.get());
				}
				return true;
			}

			//orerun.nofall
			if(cmd.getName().equalsIgnoreCase("nofall") && sender.hasPermission("orerun.nofall")) {
				switch(args[0]){
					default:
						main.setNoFallDamage(!main.getNoFallDamage());
						if(main.getNoFallDamage()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_NOFALLACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_NOFALLDESACTIVATE.get());
						}
					case "false":
						main.setNoFallDamage(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_NOFALLACTIVATE.get());
					case "true":
						main.setNoFallDamage(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_NOFALLDESACTIVATE.get());
				}
				return true;
			}

			//orerun.instantingot
			if(cmd.getName().equalsIgnoreCase("instantingot") && sender.hasPermission("orerun.instantingot")) {
				switch(args[0]){
					default:
						main.setInstantIngot(!main.getInstantIngot());
						if(main.getInstantIngot()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTDESACTIVATE.get());
						}
					case "false":
						main.setInstantIngot(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTACTIVATE.get());
					case "true":
						main.setInstantIngot(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTDESACTIVATE.get());
				}
				return true;
			}

			//orerun.instantingotfortune
			if(cmd.getName().equalsIgnoreCase("instantingotfortune") && sender.hasPermission("orerun.instantingotfortune")) {
				switch(args[0]){
					default:
						main.setInstantIngotFortune(!main.getInstantIngotFortune());
						if(main.getInstantIngotFortune()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEDESACTIVATE.get());
						}
					case "false":
						main.setInstantIngotFortune(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEACTIVATE.get());
					case "true":
						main.setInstantIngotFortune(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEDESACTIVATE.get());
				}
				return true;
			}

			//orerun.friendlyfire
			if(cmd.getName().equalsIgnoreCase("friendlyfire") && sender.hasPermission("orerun.friendlyfire")) {
				switch(args[0]){
					default:
						main.setFriendlyFire(!main.getFriendlyFire());
						if(main.getFriendlyFire()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_FRIENDLYFIREACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_FRIENDLYFIREDESACTIVATE.get());
						}
					case "false":
						main.setFriendlyFire(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_FRIENDLYFIREACTIVATE.get());
					case "true":
						main.setFriendlyFire(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_FRIENDLYFIREDESACTIVATE.get());
				}
				return true;
			}

			//orerun.healthregen
			if(cmd.getName().equalsIgnoreCase("healthregen") && sender.hasPermission("orerun.healthregen")) {
				switch(args[0]){
					default:
						main.setHealthRegen(!main.getHealthRegen());
						if(main.getHealthRegen()){
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_HEALTHREGENACTIVATE.get());
						} else {
							Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_HEALTHREGENDESACTIVATE.get());
						}
					case "false":
						main.setHealthRegen(false);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_HEALTHREGENACTIVATE.get());
					case "true":
						main.setHealthRegen(true);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_HEALTHREGENDESACTIVATE.get());
				}
				return true;
			}

			//orerun.minplayer
			if(cmd.getName().equalsIgnoreCase("minplayer") && sender.hasPermission("orerun.minplayer")) {
				if(args.length > 0){
					int count = Integer.valueOf(args[0]);
					if(count > 0){
						main.SetMinPlayers(count);
						Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_MINPLAYERS.get().replace("{count}", String.valueOf(count)));
					} else {
						player.sendMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_INVALIDEMINPLAYERS.get());
					}
				}
				return true;
			}
			
			//lobby
			if(cmd.getName().equalsIgnoreCase("lobby")) {

				if(main.getState() == GameState.WAITING) {
					//BungeeCord
					if(main.F_Config.getBooleanConfig("BungeeCord.Support")){
						new F_BungeeCord().sendPlayersToLobby();
					} else{
						player.teleport(main.lobby);
					}
				}
				return true;
			}
			//Global Chat
			if(cmd.getName().equalsIgnoreCase("!")) {
				sender.getServer().broadcastMessage(ChatListeners.getInstance().FormatChat(player, msg));
				return true;
			}
			
			if(cmd.getName().equalsIgnoreCase("report")) {	
				Player owner = (Player) sender;
				player = getPlayerFromString(args[0]);
				if(player != null && player != owner) {				
					if(!argsToText(args).replace(args[0], "").isEmpty()) {						
						new F_Report().reportPlayer((Player)sender, getPlayerFromString(args[0]), argsToText(args).replace(args[0], ""));
						return true;
					}
					sender.sendMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_REPORT_INVALIDREASON.get());
					return false;
				} else {
					sender.sendMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_REPORT_INVALIDPLAYER.get());
					return false;
				}
			}
			
			if(cmd.getName().equalsIgnoreCase("bug")) {
				new F_Report().reportBug(Bukkit.getServer().getServerName(), (Player)sender, argsToText(args));
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
			sender.sendMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_CANCHANGETEAM_ACTIVATED.get());
		} else {
			sender.sendMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_CANCHANGETEAM_DEACTIVATED.get());
		}
	}
	
	public void RestartGame(CommandSender sender) {
		main.Reload();
		Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.COMMANDS_RESTARTGAME.get());
	}

}
