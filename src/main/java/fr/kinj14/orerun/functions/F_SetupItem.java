package fr.kinj14.orerun.functions;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Collections;

public class F_SetupItem {
    protected final Main main = Main.getInstance();

    public ItemStack GetItem(){
        return buildItemstack(new ItemStack(Material.BOOK, 1), OreRun_Lang.ITEMS_SETUP.get(), new ArrayList<>());
    }

    public void OpenSetupMenu(Player player){
        final Inventory menu = Bukkit.createInventory(player, InventoryType.CHEST, OreRun_Lang.ITEMS_SETUP.get());

        setupItem(menu);

        player.openInventory(menu);
    }

    public void updateSetupInventory(Inventory menu,Player player){
        if(menu.getName() != OreRun_Lang.ITEMS_SETUP.get()){return;}

        setupItem(menu);

        for(Entity ent : menu.getViewers()){
            if(ent instanceof Player){
                ((Player)ent).updateInventory();
            }
        }
        player.updateInventory();
    }

    public void setupItem(Inventory menu){
        //CanChangeTeam Item
        menu.setItem(0, buildItemstack(new ItemStack(Material.BANNER, 1), "CanChangeTeam", new ArrayList<>(Collections.singletonList(String.valueOf(main.getCanChangeTeam())))));
        //NoFallDamage Item
        menu.setItem(1, buildItemstack(new ItemStack(Material.ELYTRA, 1), "NoFallDamage", new ArrayList<>(Collections.singletonList(String.valueOf(main.getNoFallDamage())))));
        //IntantIngot Item
        menu.setItem(2, buildItemstack(new ItemStack(Material.IRON_INGOT, 1), "InstantIngot", new ArrayList<>(Collections.singletonList(String.valueOf(main.getInstantIngot())))));
        //IntantIngot Fortune Item
        ItemStack instantingotfortune_item = buildItemstack(new ItemStack(Material.IRON_INGOT, 1), "InstantIngotFortune", new ArrayList<>(Collections.singletonList(String.valueOf(main.getInstantIngotFortune()))));
        instantingotfortune_item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        menu.setItem(3, instantingotfortune_item);
        //FriendlyFire Item
        menu.setItem(4, buildItemstack(new ItemStack(Material.IRON_SWORD, 1), "FriendlyFire", new ArrayList<>(Collections.singletonList(String.valueOf(main.getFriendlyFire())))));
        //NaturalRegen Item
        ItemStack healthregen_item = buildItemstack(new ItemStack(Material.POTION, 1), "NaturalRegen", new ArrayList<>(Collections.singletonList(String.valueOf(main.getHealthRegen()))));
        menu.setItem(5, healthregen_item);
        //MinPlayers Item
        menu.setItem(6, buildItemstack(new ItemStack(Material.SKULL_ITEM, 1), "MinPlayers", new ArrayList<>(Collections.singletonList(String.valueOf(main.getMinPlayers())))));

        //StopStart Item
        menu.setItem(7, buildItemstack(new ItemStack(Material.WATCH, 1), "StopStart", new ArrayList<>(Collections.singletonList(String.valueOf(main.getStopStart())))));

        //Start Item
        menu.setItem(26, buildItemstack(new ItemStack(Material.FLINT_AND_STEEL, 1), "Start", new ArrayList<>()));
    }

    public static ItemStack buildItemstack(ItemStack is, String displayName, ArrayList<String> description){
        final ItemMeta im = is.getItemMeta();

        //SetItemMeta
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(!description.isEmpty()){
            for(String de : description){
                description.set(description.indexOf(de), ChatColor.translateAlternateColorCodes('&', de));
            }
            im.setLore(description);
        }
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);

        is.setItemMeta(im);

        return is;
    }
}
