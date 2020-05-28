package fr.kinj14.orerun.listeners;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.gui.GUI_GameSetup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class SetupGameListeners implements Listener {
    protected final Main main = Main.getInstance();
    public Map<Player,Integer> DelayList = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equalsIgnoreCase(OreRun_Lang.ITEMS_SETUP.get())) {
                Integer Interval = 20;
                if(CheckDelay(player,Interval)) {event.setCancelled(true);return;}
                AddDelay(player, Interval);

                new GUI_GameSetup(player).open();

                event.setCancelled(true);
                return;
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
}
