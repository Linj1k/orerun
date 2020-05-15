package fr.kinj14.orerun.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kinj14.orerun.Main;

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
	
	public Teams(String id, String name, String tag, String itemid, byte itemdata, Location spawn) {
		cfg = main.getConfig();
		this.id = id;
		this.name = name;
		this.tag = tag;
		this.itemid = itemid;
		this.itemdata = itemdata;
		this.MaxPlayers = main.cfg.getInt("General.teams."+id+".MaxPlayers");
		this.Spawn = spawn;
	}
	
	public ItemStack getIcon() {
		ItemStack i = new ItemStack(Material.getMaterial(itemid), 1, itemdata);
		ItemMeta IM = i.getItemMeta();
		IM.setDisplayName("Join "+tag+name+" team!");
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
}
