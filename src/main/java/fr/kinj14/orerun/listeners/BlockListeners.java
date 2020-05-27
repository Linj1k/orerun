package fr.kinj14.orerun.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.inventory.ItemStack;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;

public class BlockListeners implements Listener {
	protected final Main main = Main.getInstance();
	
	@EventHandler
	public void OnPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void OnBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
		
		if(main.getInstantIngot()) {
			OreToIngot(event, Material.IRON_ORE, Material.IRON_INGOT);
			OreToIngot(event, Material.GOLD_ORE, Material.GOLD_INGOT);
		}
	}
	
	public Boolean OreToIngot(BlockBreakEvent event, Material ore, Material ingot) {
		Block block = event.getBlock();
		Player p = event.getPlayer();
		if(main.getInstantIngot() && block.getType().equals(ore)) {
			if (p.getInventory().getItemInMainHand().getType() == Material.STONE_PICKAXE || p.getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE) {
		        if (p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {		        	
		        	return false; 
		        }
				int ItemCount = 1;
				if (main.getInstantIngotFortune() && p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
			        if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 1) {
			          ItemCount = 2;
			        } else if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 2) {
			          ItemCount = 3;
			        } else if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 3) {
			          ItemCount = 4;
			        } 
				}
				Location blockLocation = block.getLocation().add(0.35,0.35,0.35);
				block.getWorld().dropItemNaturally(blockLocation, new ItemStack(ingot, ItemCount));
				block.setType(Material.AIR);
				event.setCancelled(true);
				return true;
			}
		}
		return false;
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
    }
	
	@EventHandler
	public void onLeavesDecay(LeavesDecayEvent event) {
		if(!main.isState(GameState.PLAYING)) {
			event.setCancelled(true);
			return;
		}
	}
}
