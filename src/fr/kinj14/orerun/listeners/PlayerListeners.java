package fr.kinj14.orerun.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
	public Map<Player,Integer> DelayList = new HashMap<>();
	
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
			scoreb.getValue().setLine(3, "Players : "+String.valueOf(main.getCountPlayers())+"/"+String.valueOf(main.getTM().GetMaxPlayersTeam()));
		}
	}
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		main.onPlayerQuit(event);
		
		event.setQuitMessage("§7[§eOreRun§7]§r "+ player.getName() +"§a has left the game ! <" + main.getCountPlayers()+"/"+Bukkit.getMaxPlayers()+">");
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			scoreb.getValue().setLine(3, "Players : "+String.valueOf(main.getCountPlayers())+"/"+String.valueOf(main.getTM().GetMaxPlayersTeam()));
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("team") && !main.isState(GameState.PLAYING)) {
			Integer Interval = 20;
			if(CheckDelay(player,Interval)) {event.setCancelled(true);return;}
			AddDelay(player, Interval);
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
		
		if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Lobby!") && !main.isState(GameState.PLAYING)) {
			Integer Interval = 20;
			if(CheckDelay(player,Interval)) {event.setCancelled(true);return;}
			AddDelay(player, Interval);
			player.teleport(main.lobby);
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
				if(mainhandItem.getType() != Material.AIR) {				
					double Point = main.getItemPoint(mainhandItem);
					if(Point != 0) {						
						int mainhandItemAmount = mainhandItem.getAmount();
						Point = Point * mainhandItemAmount;
						
						System.out.println(Point+" Points ("+item.getType().name()+")");
						
						Teams team = main.getTM().searchPlayerTeam(player);
						if(team != null) {
							team.addPoints((float)Point);
							main.getTM().updateScoreBoardPoint(player);
							player.getInventory().remove(item);
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}
	
	public void AddDelay(Player player, Integer delay) {
		if(CheckDelay(player,delay)) {return;}
		DelayList.put(player, delay);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            public void run() {
            	if(DelayList.containsKey(player) && DelayList.containsValue(delay)){
            		DelayList.remove(player, delay);
            	}
            }
		}, 0L+delay);
	}
	
	public Boolean CheckDelay(Player player, Integer delay) {
		return DelayList.containsKey(player) && DelayList.containsValue(delay);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(!main.isState(GameState.PLAYING)) {	
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(!main.isState(GameState.PLAYING)) {	
			event.setCancelled(true);
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
