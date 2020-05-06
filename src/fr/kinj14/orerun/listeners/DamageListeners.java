package fr.kinj14.orerun.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.teams.Teams;
import fr.kinj14.orerun.utils.GameState;

public class DamageListeners implements Listener {
	protected final Main main = Main.getInstance();
	
	@EventHandler
	public void OnDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		Teams playerTeam = main.getTM().searchPlayerTeam(player);
		Entity killer = player.getKiller();
		if(killer instanceof Player) {
			Teams killerTeam = main.getTM().searchPlayerTeam((Player) killer);
			Bukkit.broadcastMessage("§7[§eOreRun§7]§4 "+playerTeam.getTag()+player.getName()+" killed by "+killerTeam.getTag()+player.getKiller().getName()+" !");
		} else {
			Bukkit.broadcastMessage("§7[§eOreRun§7]§4 "+playerTeam.getTag()+player.getName()+" killed by "+killer.getName()+" !");
		}
	}
	
	@EventHandler
	public void OnDamageByEntity(EntityDamageByEntityEvent event) {
		Entity victim = event.getEntity();
		
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
		
		if(victim instanceof Player) {
			Player player = (Player)victim;
			if(event.getDamager() != null && event.getDamager() instanceof Player ) {
				Player damager = (Player)event.getDamager();
				if(main.getTM().FriendlyFire == false && main.getTM().searchPlayerTeam(player).getId() == main.getTM().searchPlayerTeam((Player)damager).getId()) {
					damager.sendMessage("§7[§eOreRun§7]§4 You cannot attack a teammate...");
					event.setCancelled(true);
				}
			}
			if(player.getHealth() <= event.getDamage()) {
				event.setDamage(0);
				main.eliminatePlayer(player);
			}
		}
	}
	
	@EventHandler
	public void OnDamage(EntityDamageEvent event) {
		Entity victim = event.getEntity();
		
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
		
		if(victim instanceof Player) {
			Player player = (Player)victim;
			if(player.getHealth() <= event.getDamage()) {
				event.setDamage(0);
				main.eliminatePlayer(player);
			}
		}
	}
	
	@EventHandler
	public void OnPVP(EntityDamageByEntityEvent event) {
		Entity victim = event.getEntity();
		
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
		
		if(victim instanceof Player) {
			Player player = (Player)victim;
			Player killer = player;
			Entity damager = event.getDamager();
			
			if(player.getHealth() <= event.getDamage()) {
				if(damager instanceof Player) {
					killer = (Player)damager;
					if(!main.getTM().FriendlyFire && main.getTM().searchPlayerTeam(player).getId() == main.getTM().searchPlayerTeam((Player)damager).getId()) {
						killer.sendMessage("§7[§eOreRun§7]§4 You cannot attack a teammate...");
						event.setCancelled(true);
						return;
					}
				}
				
				if(damager instanceof Arrow) {
					Arrow arrow = (Arrow)damager;
					if(arrow.getShooter() instanceof Player) {
						killer = (Player)arrow.getShooter();
					}
				}
				
				killer.sendMessage("You killed &c"+player.getName()+"!");
				event.setDamage(0);
				main.eliminatePlayer(player);
			}
		}
	}
}
