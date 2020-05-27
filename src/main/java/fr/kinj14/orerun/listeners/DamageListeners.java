package fr.kinj14.orerun.listeners;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.teams.Teams;

public class DamageListeners implements Listener {
	protected final Main main = Main.getInstance();
	
	@EventHandler
	public void OnDeath(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		Teams playerTeam = main.getTM().searchPlayerTeam(player);
		Entity killer = player.getKiller();
		if(killer instanceof Player) {
			Teams killerTeam = main.getTM().searchPlayerTeam((Player) killer);
			String Deathmsg = OreRun_Lang.DAMAGE_KILLEDBY.get()
					.replace("{playername}", playerTeam.getTag()+player.getName())
					.replace("{killername}", killerTeam.getTag()+killer.getName());
			event.setDeathMessage(main.GetPrefix()+Deathmsg);
			killerTeam.addKill(player);
		} else {
			if(killer != null) {
				String Deathmsg = OreRun_Lang.DAMAGE_KILLEDBY.get()
						.replace("{playername}", playerTeam.getTag()+player.getName())
						.replace("{killername}", killer.getName());
				event.setDeathMessage(main.GetPrefix()+Deathmsg);
			}
		}
		event.setDeathMessage(main.GetPrefix()+"§4"+playerTeam.getTag()+"█§4"+event.getDeathMessage());
		main.f_player.SetPlayerLife(player, main.f_player.GetPlayerLife(player)-1);

		if(main.f_player.GetPlayerLife(player) <= 0) {
			main.f_player.eliminatePlayer(player);
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
				if(PVP(player, damager) == false) {	
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void OnDamage(EntityDamageEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}

		if (event.getEntity() instanceof Player){
			if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && main.getNoFallDamage()) {
				event.setCancelled(true);
				return;
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
					if(PVP(player, killer) == false) {		
						event.setCancelled(true);
						return;
					}
				}
				
				if(damager instanceof Arrow) {
					Arrow arrow = (Arrow)damager;
					if(arrow.getShooter() instanceof Player) {
						killer = (Player)arrow.getShooter();
						if(PVP(player, killer) == false) {	
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		}
	}
	
	public Boolean PVP(Player player, Player killer) {
		if(!main.GetPVPState()) {
			killer.sendMessage(main.GetPrefix()+OreRun_Lang.DAMAGE_PVPWAIT.get());
			return false;
		}
		
		if(!main.getFriendlyFire() && main.getTM().searchPlayerTeam(player).getId() == main.getTM().searchPlayerTeam(killer).getId()) {
			killer.sendMessage(main.GetPrefix()+ OreRun_Lang.DAMAGE_FRIENDLYFIRE.get());
			return false;
		}
		return true;
	}
	
	@EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) throws Exception {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }
	
	@EventHandler
    public void onExplosionEvent(ExplosionPrimeEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
    	if(!main.isState(GameState.PLAYING)) {
    		event.setCancelled(true);
			return;
		}
    }
}
