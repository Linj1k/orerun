package fr.kinj14.orerun.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;

public class BucketListeners {
	protected final Main main = Main.getInstance();
	
	@EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) throws Exception {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }
	
	@EventHandler
    public void onPlayerBucketFille(PlayerBucketFillEvent event) throws Exception {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }
}
