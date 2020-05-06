package fr.kinj14.orerun.listeners;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.teams.Teams;
import fr.kinj14.orerun.utils.GameState;

public class PlayerListeners implements Listener {
	protected final Main main = Main.getInstance();
	
	@EventHandler
	public void PlayerLogin(PlayerLoginEvent event) {
		if(main.isState(GameState.PLAYING)) {
			event.disallow(Result.KICK_FULL, "The game is started...");
			return;
		}
		if(main.isState(GameState.PREFINISH) || main.isState(GameState.FINISH)) {
			event.disallow(Result.KICK_FULL, "The server is restarting...");
			event.setKickMessage("The server is restarting...");
			return;
		}
	}
	
	@EventHandler
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		main.PlayerSetup(player);
		
		event.setJoinMessage("§7[§eOreRun§7]§r "+ player.getName() +"§a joined the game ! <" + main.getCountPlayers()+"/"+Bukkit.getMaxPlayers()+">");
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			scoreb.getValue().setLine(3, "Players : "+main.getCountPlayers()+"/"+Bukkit.getMaxPlayers());
		}
	}
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		main.onPlayerQuit(event);
		
		event.setQuitMessage("§7[§eOreRun§7]§r "+ player.getName() +"§a has left the game ! <" + main.getCountPlayers()+"/"+Bukkit.getMaxPlayers()+">");
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			scoreb.getValue().setLine(3, "Players : "+main.getCountPlayers()+"/"+Bukkit.getMaxPlayers());
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("team")) {
			for(Teams team : main.getTeams()) {
				if (item.equals(team.getIcon())) {
					if(main.GetCanChangeTeam()) {
						main.getTM().addPlayer(player, team);
					} else {
						player.sendMessage("§7[§eOreRun§7]§r Team changes have been deactivated !");
					}
					continue;
				}
			}
			event.setCancelled(true);
			return;
		}
		
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
		
		
		if(event.getClickedBlock() != null) {
			Block target = event.getClickedBlock().getLocation().getBlock();
			
			if(target != null && target.hasMetadata("deposit")) {
				ItemStack mainhandItem = player.getInventory().getItemInMainHand();
				//int mainhandItemAmount = mainhandItem.getAmount();
				//String mainhandItemType = mainhandItem.getType().name();
				
				//ConfigurationSection PointSection = main.cfg.getConfigurationSection("Points");
				List<String> ItemsCFG = main.cfg.getConfigurationSection("Points").getStringList("Items");
				for(String s : ItemsCFG) {
						System.out.println(s.split(":")[0] + " x"+s.split(":")[1]);
				}
				
				System.out.println(ItemsCFG.size() + " "+String.valueOf(main.getItemPoint(mainhandItem))+" Points");
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void OnPlace(BlockPlaceEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void OnBreak(BlockBreakEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
	}
}
