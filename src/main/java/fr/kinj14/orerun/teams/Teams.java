package fr.kinj14.orerun.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.utils.WorldUtil;

public class Teams {
	protected final Main main = Main.getInstance();
	public FileConfiguration cfg;
	private String id;
	private String name;
	private String tag;
	private String itemid;
	private byte itemdata;
	private Location Spawn;
	private int MaxPlayers = 1;
	private float points = 0;
	private List<Player> players = new ArrayList<>();
	private List<ItemStack> ItemsPoints = new ArrayList<>();
	private Map<Player, Integer> Kills = new HashMap<>();
	
	public Teams(String id, String name, String tag, String itemid, byte itemdata) {
		cfg = main.getConfig();
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.itemid = itemid;
		this.itemdata = itemdata;
		this.MaxPlayers = main.F_Config.getIntConfig("General.teams."+id+".MaxPlayers");
	}
	
	public ItemStack getIcon() {
		ItemStack i = new ItemStack(Material.getMaterial(itemid), 1, itemdata);
		ItemMeta IM = i.getItemMeta();
		IM.setDisplayName(OreRun_Lang.ITEMS_JOINTEAM.get().replace("{teamname}", tag+name));
		i.setItemMeta(IM);
		return i;
	}
	
	public void addPlayer(Player player) {
		if(!HavePlayer(player) && getTeamPlayersCount() < getMaxPlayers()) {
			players.add(player);
			player.setPlayerListName(tag+player.getName()+"§r");
			player.setDisplayName(tag+player.getName()+"§r");
		}
	}
	
	public void removePlayer(Player player) {
		if(HavePlayer(player)) {
			players.remove(player);
			player.setPlayerListName(player.getName());
			player.setDisplayName(player.getName());
		}
	}
	
	public boolean HavePlayer(Player player) {
		return players.contains(player);
	}
	
	public List<Player> getTeamPlayers(){
		return players;
	}
	
	public void ClearTeamPlayers() {
		players.clear();
	}
	
	public int getTeamPlayersCount() {
		return players.size();
	}

	public String getName() {
		return name;
	}

	public String getTag() {
		return tag;
	}

	public int getMaxPlayers() {
		return MaxPlayers;
	}

	public String getId() {
		return id;
	}

	public Location getSpawn() {
		return Spawn;
	}

	public void setSpawn(Location loc) { Spawn = loc; }

	public float getPoints() {
		return points;
	}

	public void setPoints(float points) {
		this.points = points;
	}
	
	public void addPoints(float points) {
		this.points = this.points + points;
	}
	
	public void removePoints(float points) {
		this.points = this.points - points;
	}

	public List<ItemStack> getItemsPoints() {
		return ItemsPoints;
	}

	public void setItemsPoints(List<ItemStack> itemsPoints) {
		ItemsPoints = itemsPoints;
	}
	
	public void addItemsPoints(ItemStack itemsPoints) {
		ItemsPoints.add(itemsPoints);
	}

	public Map<Player, Integer> getKills() {
		return Kills;
	}

	public void setKills(Map<Player, Integer> kills) {
		Kills = kills;
	}
	
	public void addKill(Player player) {
		if(Kills.containsKey(player)) {
			Kills.replace(player, Kills.get(player) + 1);
		} else {			
			Kills.put(player, 1);
		}
	}
	
	public void removeKill(Player player, int Kill) {
		if(Kills.containsKey(player)) {
			Kill = Kills.get(player) - Kill;
			new WorldUtil();
			Kills.replace(player, (int)WorldUtil.clamp(Kill, 0, Kill));
		}
	}
}
