package fr.kinj14.orerun.listeners;

import java.util.HashMap;
import java.util.Map;

import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.functions.F_BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.teams.Teams;

public class PlayerListeners implements Listener {
	protected final Main main = Main.getInstance();
	public Map<Player,Integer> DelayList = new HashMap<>();
	
	@EventHandler
	public void OnJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int playerCount = Bukkit.getOnlinePlayers().size();
		
		main.f_player.PlayerSetup(player);

		String joinmsg = OreRun_Lang.PLAYER_JOINMSG.get()
				.replace("{playername}", player.getName())
				.replace("{playerCount}", String.valueOf(playerCount))
				.replace("{maxPlayers}", String.valueOf(main.getTM().GetMaxPlayersTeam()));
		event.setJoinMessage(main.GetPrefix()+joinmsg);
		
		main.f_player.ScoreboardUpdatePlayers();
	}
	
	@EventHandler
	public void OnQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		int playerCount = Bukkit.getOnlinePlayers().size()-1;
		
		main.f_player.onPlayerQuit(event);

		String quitmsg = OreRun_Lang.PLAYER_QUITMSG.get()
				.replace("{playername}", player.getName())
				.replace("{playerCount}", String.valueOf(playerCount))
				.replace("{maxPlayers}", String.valueOf(main.getTM().GetMaxPlayersTeam()));
		event.setQuitMessage(main.GetPrefix()+quitmsg);
		
		main.f_player.ScoreboardUpdatePlayers();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if(item != null && item.hasItemMeta()){
			if(item.getItemMeta().getDisplayName().contains("team") && main.isState(GameState.WAITING)) {
				Integer Interval = 20;
				if(CheckDelay(player,Interval)) {event.setCancelled(true);return;}
				AddDelay(player, Interval);
				for(Teams team : main.getTeams()) {
					if (item.equals(team.getIcon())) {
						if(main.GetCanChangeTeam()) {
							main.getTM().addPlayer(player, team);
						} else {
							player.sendMessage(main.GetPrefix()+ OreRun_Lang.PLAYER_CANCHANGETEAMMSG.get());
						}
						continue;
					}
				}
				event.setCancelled(true);
				return;
			}

			if(item.getItemMeta().getDisplayName().contains(OreRun_Lang.ITEMS_LOBBY.get()) && main.isState(GameState.WAITING)) {
				Integer Interval = 20;
				if(CheckDelay(player,Interval)) {event.setCancelled(true);return;}
				AddDelay(player, Interval);

				//BungeeCord
				if(main.F_Config.getBooleanConfig("BungeeCord.Support")){
					new F_BungeeCord().sendPlayersToLobby();
				} else{
					player.teleport(main.lobby);
				}

				event.setCancelled(true);
				return;
			}
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
							team.addItemsPoints(mainhandItem);
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
    public void onPlayerPickEvent(EntityPickupItemEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }
}
