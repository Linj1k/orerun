package fr.kinj14.orerun.gui;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class GUI implements InventoryHolder {
    protected final Main main = Main.getInstance();
    protected Inventory inventory;
    protected Player player;

    public GUI(Player p){
        this.player = p;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public void open() {
        //The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        //grab all the items specified to be used for this menu and add to inventory
        this.setMenuItems();

        //open the inventory for the player
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setMenuItems(){}

    public ItemStack GetItem(){
        return buildItemstack(new ItemStack(Material.BOOK, 1), OreRun_Lang.ITEMS_SETUP.get(), new ArrayList<>());
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

    public void updateInventory(){
        this.setMenuItems();
        player.updateInventory();
    }
}
