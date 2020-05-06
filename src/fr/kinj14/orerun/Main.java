package fr.kinj14.orerun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Tasks.AutoStart;
import fr.kinj14.orerun.Tasks.Finish;
import fr.kinj14.orerun.Tasks.GameCycle;
import fr.kinj14.orerun.commands.Commands;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.library.Title;
import fr.kinj14.orerun.listeners.ChatListeners;
import fr.kinj14.orerun.listeners.DamageListeners;
import fr.kinj14.orerun.listeners.PlayerListeners;
import fr.kinj14.orerun.teams.Teams;
import fr.kinj14.orerun.teams.TeamsManager;
import fr.kinj14.orerun.utils.GameState;
import fr.kinj14.orerun.utils.OFireWorks;
import fr.kinj14.orerun.utils.WorldUtil;

public class Main extends JavaPlugin{
	//Instance
	public static Main instance = null;
	
	public static Main getInstance() {
		return instance;
	}
	
	//GameState
	private GameState CurrentState;
	private BukkitRunnable GameCycle = null;
	private BukkitRunnable AutoStart = null;
	private BukkitRunnable Finish = null;
	//Players
	public int MinPlayers = 0;
	private List<Player> players = new ArrayList<>();
	public Map<Player, ScoreboardSign> scorebaordMap = new HashMap<>();
	// Others
	public Location lobby;
	public WorldBorder wb;
	public FileConfiguration cfg;
	public Title title = new Title();
	public Block DepositChest;
	public WorldUtil worldUtils = new WorldUtil();
	public OFireWorks OFireWorks = new OFireWorks(); 
	public String worldname;
	
	//Team
	private List<Teams> Teams = new ArrayList<>();
	private TeamsManager TM;
	private boolean CanChangeTeam = true;
	
	@Override
	public void onEnable(){
		// Load the configuration.
		saveDefaultConfig();
		cfg = getConfig();
		
		worldname = cfg.getString("General.WordName");
		lobby = deserializeConfigLocation("Locations.Lobby");
		Bukkit.getWorld(worldname).setSpawnLocation(lobby);
		MinPlayers = cfg.getInt("General.MinPlayers");
		
		
		//Reset GameState
		setState(GameState.WAITING);
		
		//WorldBorder
		World world = Bukkit.getWorld(worldname);
		WorldBorder wb = world.getWorldBorder();
		wb.setCenter(lobby);
		wb.setSize(cfg.getInt("WorldBorder.LobbySize"));
		wb.setDamageAmount(cfg.getDouble("WorldBorder.DamageAmount"));
		wb.setDamageBuffer(cfg.getInt("WorldBorder.DamageBuffer"));
		wb.setWarningDistance(cfg.getInt("WorldBorder.WarningBlock"));
		
		if (instance != null) { // is for reload command
			instance = this;
			System.setProperty("OreRunLoaded", "true");
			// Teams
			TM = new TeamsManager();
			loadTeams();
			
			Reload();
			PrepareGame();
		} else {
			instance = this;
			System.setProperty("OreRunLoaded", "true");

			// Teams
			TM = new TeamsManager();
			loadTeams();
		}
		
		//RegisterListeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListeners(), this);
		pm.registerEvents(new DamageListeners(), this);
		pm.registerEvents(new ChatListeners(getServer()), this);
		
		//Commands
		getCommand("orerun.canchangeteam").setExecutor(new Commands());
		getCommand("orerun.restartgame").setExecutor(new Commands());
	}
	
	@Override
	public void onDisable() {
		if(isState(GameState.FINISH)) {			
			StartRestartWorld();
		}
	}
	
	// Game
	public void PrepareGame() {
		if(isState(GameState.WAITING) && getCountPlayers() >= MinPlayers) {
			//Reset Timers
			ResetAllTimers();
			AutoStart start = new AutoStart();
			setAutoStart(start);
			start.runTaskTimer(this, 0, 20);
			setState(GameState.STARTING);
			Location chestLoc = deserializeConfigLocation("Locations.DepositChest");
			chestLoc.getBlock().setType(Material.CHEST);
			chestLoc.getBlock().setMetadata("deposit", new FixedMetadataValue(this, true));
			DepositChest = chestLoc.getBlock();
		}
	}
	
	public void StartGame() {
		Location GameCenter = deserializeConfigLocation("Locations.GameCenter");
		int GameSize = cfg.getInt("WorldBorder.GameSize");
		
		Bukkit.broadcastMessage("§7[§eOreRun§7]§r Launching the game !");
		World world = Bukkit.getWorld(worldname);
		world.setTime(0);
		world.setThunderDuration(0);
		world.setWeatherDuration(0);
		world.setStorm(false);
		world.setThundering(false);
		
		for(Player player :getPlayers()) {
			if(player != null) {
				PlayerSetupGame(player);
			}
		}
		
		setState(GameState.PLAYING);
		WorldBorder wb = world.getWorldBorder();
		wb.setCenter(GameCenter);
		wb.setSize(GameSize);
		
		//Reset Timers
		ResetAllTimers();
		GameCycle cycle = new GameCycle();
		setGameCycle(cycle);
		cycle.runTaskTimer(this, 0, 20); 
	}
	
	public void Reload() {
		//Reset Timers
		ResetAllTimers();
		//Reset GameCycle
		setState(GameState.WAITING);
		if(DepositChest != null) {
			DepositChest.setType(Material.AIR);
		}
		//Reset WorldBorder
		WorldBorder wb = Bukkit.getWorld(worldname).getWorldBorder();
		wb.setCenter(lobby);
		wb.setSize(cfg.getInt("WorldBorder.LobbySize"));
		wb.setDamageAmount(cfg.getInt("WorldBorder.DamageAmount"));
		wb.setDamageBuffer(cfg.getInt("WorldBorder.DamageBuffer"));
		//Reset Scoreboards
		for(Entry<Player, ScoreboardSign> score : scorebaordMap.entrySet()) {
			System.out.println(score.getKey().getName());
			score.getValue().destroy();
		}
		scorebaordMap.clear();
		//Reset Players
		players.clear();
		for(Player p : Bukkit.getOnlinePlayers()) {
			players.add(p);
			PlayerSetup(p);
			PlayerSetupLobby(p);
		}
	}
	
	public void ReturnLobby(boolean Force) {
		if(isState(GameState.PLAYING) && getCountPlayers() <= MinPlayers || Force) {
			Reload();
		}
	}

	public void checkWin() {
		String msg = null;
		if(getCountPlayers() == 0) {
			msg = "Equality!";
		}
		
		if(getCountPlayers() == 1) {
			Player winner = players.get(0);
			msg = winner.getName()+" won !";
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.teleport(winner.getLocation());
			}
			OFireWorks.SpawnFireWorks(winner.getLocation());
		}
		
		if(msg != null) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				title.sendTitle(player, msg, "§eLobby...", 20);
			}
			
			
			setState(GameState.PREFINISH);
			ResetAllTimers();
			Finish finish = new Finish();
			setFinish(finish);
			finish.runTaskTimer(this, 0, 20); 
		}
		
		//Reset Timers
		Bukkit.broadcastMessage("§7[§eOreRun§7] "+msg);
		
		return;
	}
	
	//GameState
	public void setState(GameState State) {
		this.CurrentState = State;
	}
	
	public boolean isState(GameState State) {
		return this.CurrentState == State;
	}
	
	public GameState getState() {
		return this.CurrentState;
	}

	// Teams
	public boolean GetCanChangeTeam() {
		return CanChangeTeam;
	}

	public void setCanChangeTeam(boolean canChangeTeam) {
		CanChangeTeam = canChangeTeam;
	}
	
	public void loadTeams() {
		this.Teams.clear();
		ConfigurationSection teamsection = cfg.getConfigurationSection("General.teams");
		for(String team : teamsection.getKeys(false)) {
			String name = teamsection.getString(team + ".name");
			String tag = teamsection.getString(team + ".color").replace("&", "§");
			String[] Items = teamsection.getString(team + ".item").split("/");
			String itemid = Items[0].split(":")[1].toUpperCase();
			Location spawn = deserializeLocation(teamsection.getString(team + ".spawns"));
			byte itemdata = Byte.parseByte(Items[1]);
			
			Teams.add(new Teams(team, name, tag, itemid, itemdata, spawn));
		}
		
		System.out.println("[OreRun] " + Teams.size() + " teams is loaded !");
	}
	
	public List<Teams> getTeams(){
		return Teams;
	}
	
	public TeamsManager getTM() {
		return TM;
	}
	
	// Player
	
	public void addPlayer(Player player){
		players.add(player);
	}
	
	public void removePlayer(Player player){
		for(Teams team: getTeams()) {
			team.removePlayer(player);
		}
		players.remove(player);
	}
	
	public boolean HavePlayer(Player player){
		return players.contains(player);
	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public Integer getCountPlayers(){
		return players.size();
	}

	public void eliminatePlayer(Player player) {
		if(HavePlayer(player)) {
			removePlayer(player);
		}
		
		player.setGameMode(GameMode.SPECTATOR);
		player.sendMessage("§7[§eOreRun§7]§r You lose ...");
		checkWin();
	}
	
	public Player getRandomPlayer() {
		Player player = null;
		
		List<Player> Players = new ArrayList<>();
		for(Player rplayer : getPlayers()) {
			if(rplayer != null && !rplayer.isDead()) {
				Players.add(rplayer);
			}
		}
		
		player = Players.get(new Random().nextInt(Players.size()));
		return player;
	}
	
	public void PlayerSetup(Player player) {
		//Variables
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setExp(0f);
		
		if(!HavePlayer(player)) {
			addPlayer(player);
		}
		
		if(isState(GameState.WAITING)) {
			PlayerSetupLobby(player);
		}
		
		//Scoreboard Setup
		
		ScoreboardSign sb = new ScoreboardSign(player, "§eOreRun");
		sb.create();
		sb.setLine(0, "§7 ");
		sb.setLine(1, "Time : 0s");
		sb.setLine(2, "§9 ");
		sb.setLine(3, "Players : "+String.valueOf(getCountPlayers())+"/"+String.valueOf(Bukkit.getMaxPlayers()));
		sb.setLine(4, "§e ");
		Teams scorebTeam = getTM().searchPlayerTeam(player);
		if(scorebTeam != null) {			
			sb.setLine(5, "Team : "+scorebTeam.getTag()+scorebTeam.getName());
		}
		
		scorebaordMap.put(player, sb);
		
		// Game is started
		if(isState(GameState.PLAYING)) {
			player.teleport(getRandomPlayer());
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage("§7[§eOreRun§7]§rLe jeu a déja demarré !");
		}
	}
	
	public void PlayerSetupLobby(Player player) {
		// Spawn
		player.teleport(lobby);
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setExp(0f);
		player.setGameMode(GameMode.ADVENTURE);
		
		// Add item for select Team
		for(Teams team : getTeams()) {
			player.getInventory().addItem(team.getIcon());
		}
		getTM().addPlayer(player, getTM().randomTeam(player));
		
		// Look for LaunchGame
		PrepareGame();
	}
		
		
	
	public void PlayerSetupGame(Player player) {
		Teams playerTeam = getTM().searchPlayerTeam(player);
		if(playerTeam == null) {
			getTM().randomTeam(player);
			playerTeam = getTM().searchPlayerTeam(player);
		}
		player.teleport(playerTeam.getSpawn());
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setExp(0f);
		player.setGameMode(GameMode.SURVIVAL);
		player.updateInventory();
		title.sendTitle(player, "§7Launching the game !", "§eTeleporting...", 20);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(HavePlayer(player)) {
			removePlayer(player);
		}
		
		ReturnLobby(false);
	}
	
	// Item
	
	public double getItemPoint(ItemStack item) {
		double returnvalue = 0.0;
		ConfigurationSection PointSection = cfg.getConfigurationSection("Points");
		Set<String> ItemsCFG = cfg.getConfigurationSection("Points").getKeys(false);
		for(String s : ItemsCFG) {
			if(s == String.valueOf(item.getType().name())) {
				System.out.println(s == String.valueOf(item.getType().name()) + " x"+PointSection.getDouble(s));
				returnvalue = PointSection.getDouble(s);
				break;
			}
		}
		return returnvalue;
	}
	
	//(de)serialize
	
	public Location deserializeConfigLocation(String configname){
		String[] split = this.cfg.getString(configname).split(", ");
		return new Location(
                Bukkit.getWorld(cfg.getString("General.WordName")),
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
                );
	}
	
	public Location deserializeLocation(String name){
		String[] split = name.split(", ");
		return new Location(
				Bukkit.getWorld(cfg.getString("General.WordName")),
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
                );
	}
	
	public static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch();
   }
	
	//Restart Server/World

	public void StartRestartWorld() {
		if(isState(GameState.FINISH)) {
			players.clear();
			Teams.clear();
			for(Entry<Player, ScoreboardSign> score : scorebaordMap.entrySet()) {
				score.getValue().destroy();
			}
			scorebaordMap.clear();
			
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.kickPlayer("Server restart!");
			}

			//Unload world
			World world = Bukkit.getWorld(worldname);
			Bukkit.unloadWorld(world, false);
			//Delete world
			File worldFile = new File(world.getName());
			worldUtils.deleteWorld(worldFile);
			
			World copyworld = Bukkit.getWorld("copy"+world.getName());
			try {
				File copyworldFile = null;
				if(copyworld != null && copyworld.getWorldFolder() != null) {					
					copyworldFile = copyworld.getWorldFolder();
					worldUtils.copyWorld(copyworldFile, worldFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void ResetAllTimers() {
		BukkitRunnable gameCycle = getGameCycle();
		if(gameCycle != null) {
			gameCycle.cancel();
			setGameCycle(null);
		}
		BukkitRunnable autoStart = getAutoStart();
		if(autoStart != null) {
			autoStart.cancel();
			setAutoStart(null);
		}
		BukkitRunnable Finish = getFinish();
		if(Finish != null) {
			Finish.cancel();
			setFinish(null);
		}
	}

	public BukkitRunnable getGameCycle() {
		return GameCycle;
	}

	public void setGameCycle(BukkitRunnable gameCycle) {
		GameCycle = gameCycle;
	}

	public BukkitRunnable getAutoStart() {
		return AutoStart;
	}

	public void setAutoStart(BukkitRunnable autoStart) {
		AutoStart = autoStart;
	}

	public BukkitRunnable getFinish() {
		return Finish;
	}

	public void setFinish(BukkitRunnable finish) {
		Finish = finish;
	}
}
