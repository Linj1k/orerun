package fr.kinj14.orerun.gui;

import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class GUI_GameSetup extends GUI {
    public GUI_GameSetup(Player p) {
        super(p);
    }

    @Override
    public String getMenuName() {
        return OreRun_Lang.ITEMS_SETUP.get();
    }

    @Override
    public int getSlots() {
        return InventoryType.CHEST.getDefaultSize();
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(player.hasPermission("canModifySettings")){
            final ItemStack item = inventory.getItem(event.getSlot());

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
                    this.updateInventory();
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;
        //CanChangeTeam Item
        menu.setItem(0, this.buildItemstack(new ItemStack(Material.BANNER, 1), "CanChangeTeam", new ArrayList<>(Collections.singletonList(String.valueOf(main.getCanChangeTeam())))));
        //NoFallDamage Item
        menu.setItem(1, this.buildItemstack(new ItemStack(Material.ELYTRA, 1), "NoFallDamage", new ArrayList<>(Collections.singletonList(String.valueOf(main.getNoFallDamage())))));
        //IntantIngot Item
        menu.setItem(2, this.buildItemstack(new ItemStack(Material.IRON_INGOT, 1), "InstantIngot", new ArrayList<>(Collections.singletonList(String.valueOf(main.getInstantIngot())))));
        //IntantIngot Fortune Item
        ItemStack instantingotfortune_item = this.buildItemstack(new ItemStack(Material.IRON_INGOT, 1), "InstantIngotFortune", new ArrayList<>(Collections.singletonList(String.valueOf(main.getInstantIngotFortune()))));
        instantingotfortune_item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        menu.setItem(3, instantingotfortune_item);
        //FriendlyFire Item
        menu.setItem(4, this.buildItemstack(new ItemStack(Material.IRON_SWORD, 1), "FriendlyFire", new ArrayList<>(Collections.singletonList(String.valueOf(main.getFriendlyFire())))));
        //NaturalRegen Item
        ItemStack healthregen_item = this.buildItemstack(new ItemStack(Material.POTION, 1), "NaturalRegen", new ArrayList<>(Collections.singletonList(String.valueOf(main.getHealthRegen()))));
        menu.setItem(5, healthregen_item);
        //MinPlayers Item
        menu.setItem(6, this.buildItemstack(new ItemStack(Material.SKULL_ITEM, 1), "MinPlayers", new ArrayList<>(Collections.singletonList(String.valueOf(main.getMinPlayers())))));

        //StopStart Item
        menu.setItem(7, this.buildItemstack(new ItemStack(Material.WATCH, 1), "StopStart", new ArrayList<>(Collections.singletonList(String.valueOf(main.getStopStart())))));

        //Start Item
        menu.setItem(26, this.buildItemstack(new ItemStack(Material.FLINT_AND_STEEL, 1), "Start", new ArrayList<>()));
    }

    @Override
    public ItemStack GetItem(){
        return buildItemstack(new ItemStack(Material.BOOK, 1), OreRun_Lang.ITEMS_SETUP.get(), new ArrayList<>());
    }
}
