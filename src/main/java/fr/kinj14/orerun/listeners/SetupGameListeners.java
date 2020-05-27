package fr.kinj14.orerun.listeners;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.Tasks.AutoStart;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.functions.F_SetupItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
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

                main.F_SetupItem.OpenSetupMenu(player);

                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Player player = (Player)event.getWhoClicked();
        final Inventory inventory = event.getClickedInventory();

        if(inventory != null && inventory.getName().equalsIgnoreCase(OreRun_Lang.ITEMS_SETUP.get()) && player.hasPermission("canModifySettings")){
            final int clickedSlot = event.getSlot();
            final ItemStack item = inventory.getItem(clickedSlot);

            if(item != null){
                if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                    ItemMeta meta = item.getItemMeta();
                    if(meta.getDisplayName().equalsIgnoreCase("CanChangeTeam")){
                        main.setCanChangeTeam(!main.getCanChangeTeam());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("NoFallDamage")){
                        main.setNoFallDamage(!main.getNoFallDamage());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("InstantIngot")){
                        main.setInstantIngot(!main.getInstantIngot());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("InstantIngotFortune")){
                        main.setInstantIngotFortune(!main.getInstantIngotFortune());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("FriendlyFire")){
                        main.setFriendlyFire(!main.getFriendlyFire());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("NaturalRegen")){
                        main.setHealthRegen(!main.getHealthRegen());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("MinPlayers")){
                        if(event.getAction() == InventoryAction.PICKUP_ALL){
                            main.SetMinPlayers(main.getMinPlayers()+1);
                        } else if(event.getAction() == InventoryAction.PICKUP_HALF){
                            main.SetMinPlayers(main.getMinPlayers()-1);
                        }
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("StopStart")){
                        main.setStopStart(!main.getStopStart());
                    }
                    if(meta.getDisplayName().equalsIgnoreCase("Start")){
                        if(main.isState(GameState.WAITING) && main.getCountPlayers() >= main.getMinPlayers()) {
                            if (main.getTM().CheckForStartGameTeam()) {
                                for(Player play : main.getPlayers()) {
                                    if(main.getTM().searchPlayerTeam(play) == null) {
                                        main.getTM().addPlayer(play, main.getTM().randomTeam(play));
                                    }
                                }
                                main.setState(GameState.STARTING);
                                main.StartGame();
                            }
                        }
                    }
                    main.PrepareGame();
                    main.F_SetupItem.updateSetupInventory(inventory, player);
                    event.setCancelled(true);
                    return;
                }
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
