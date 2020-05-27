package fr.kinj14.orerun;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.kinj14.orerun.enums.OreRun_Files;
import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.functions.*;
import fr.kinj14.orerun.listeners.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Tasks.AutoStart;
import fr.kinj14.orerun.Tasks.Finish;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.library.Title;
import fr.kinj14.orerun.teams.Teams;
import fr.kinj14.orerun.teams.TeamsManager;
import fr.kinj14.orerun.utils.WorldUtil;
import net.minecraft.server.v1_12_R1.Item;

public class Main extends JavaPlugin {
	//Instance
	private static Main instance = null;
	
	public static Main getInstance() {
		return instance;
	}

	public String version = "1.3-SNAPSHOT";

	//Functions
	public fr.kinj14.orerun.functions.F_Player f_player;
	public fr.kinj14.orerun.functions.F_World F_World;
	public fr.kinj14.orerun.functions.F_Config F_Config;
	public fr.kinj14.orerun.functions.F_FireWorks OFireWorks = new F_FireWorks();
	public fr.kinj14.orerun.functions.F_SetupItem F_SetupItem;

	//Config
	public String Lobby_WorldName;
	public String GameName = "orerun_game";
	private Boolean InstantIngot;
	private Boolean InstantIngotFortune;
	private Boolean FriendlyFire;
	private Boolean HealthRegen;
	
	//GameState
	private GameState CurrentState;
	private BukkitRunnable GameCycle = null;
	private BukkitRunnable AutoStart = null;
	private BukkitRunnable Finish = null;
	private Boolean PVPState = false;
	private Boolean NoFallDamage = false;
	private Boolean StopStart = false;

	//Players
	public int MinPlayers = 0;
	private List<Player> players = new ArrayList<>();
	public Map<Player, ScoreboardSign> scorebaordMap = new HashMap<>();
	public int PlayerLife = 3;
	public Map<Player, Integer> PlayerLifeMap = new HashMap<>();

	// Others
	public Logger logger;
	public Location lobby;
	public WorldBorder wb;
	public Block DepositChest;

	//Library
	public Title title = new Title();

	//Utils
	public WorldUtil worldUtils = new WorldUtil();

	//Team
	private List<Teams> Teams = new ArrayList<>();
	private TeamsManager TM;
	private boolean CanChangeTeam = true;
	
	@Override
	public void onEnable(){
		logger = Bukkit.getLogger();
		instance = this;
		System.setProperty("OreRunLoaded", "true");

		// Load the configuration.
		saveDefaultConfig();

		F_Config = new F_Config();
		f_player = new F_Player();
		F_World = new F_World();
		F_SetupItem = new F_SetupItem();

		//load the Language
		OreRun_Files lang = OreRun_Files.LANG;
		lang.create();

		logger.info("====================================");
		logger.info(OreRun_Lang.PLUGIN_INITIALIZATION.get());
		if(F_Config.getBooleanConfig("CheckForUpdates")){
			if(F_Config.checkForUpdate()){
				logger.info("The plugin is not up to date! (check : https://orerun.ovh)");
			}
		}

		//BungeeCord
		if(F_Config.getBooleanConfig("BungeeCord.Support")){
			getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		}

		Lobby_WorldName = F_Config.getStringConfig("General.Lobby_WorldName");
		lobby = F_Config.getLocationConfig(Lobby_WorldName, "Locations.Lobby");
		Bukkit.getWorld(Lobby_WorldName).setSpawnLocation(lobby);
		MinPlayers = F_Config.getIntConfig("General.MinPlayers");
		InstantIngot = F_Config.getBooleanConfig("General.InstantIngot");
		InstantIngotFortune = F_Config.getBooleanConfig("General.InstantIngotFortune");
		PlayerLife = F_Config.getIntConfig("General.PlayerLife");
		NoFallDamage = F_Config.getBooleanConfig("General.NoFallDamage");
		FriendlyFire = F_Config.getBooleanConfig("General.FriendlyFire");
		HealthRegen = F_Config.getBooleanConfig("General.EnableHealthRegen");
		
		//Reset GameState
		setState(GameState.WAITING);
		DepositChest = null;

		// Teams
		TM = new TeamsManager();
		loadTeams();
		Reload();
		
		//RegisterListeners
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockListeners(), this);
		pm.registerEvents(new PlayerListeners(), this);
		pm.registerEvents(new DamageListeners(), this);
		pm.registerEvents(new ChatListeners(getServer()), this);
		pm.registerEvents(new ServerListPing(), this);
		pm.registerEvents(new SetupGameListeners(), this);
		
		//Commands
		getCommand("canchangeteam").setExecutor(new F_Commands());
		getCommand("restartgame").setExecutor(new F_Commands());
		getCommand("bug").setExecutor(new F_Commands());
		getCommand("report").setExecutor(new F_Commands());
		getCommand("lobby").setExecutor(new F_Commands());
		getCommand("gm").setExecutor(new F_Commands());
		getCommand("heal").setExecutor(new F_Commands());
		getCommand("feed").setExecutor(new F_Commands());
		getCommand("pvp").setExecutor(new F_Commands());
		getCommand("nofall").setExecutor(new F_Commands());
		getCommand("instantingot").setExecutor(new F_Commands());
		getCommand("instantingotfortune").setExecutor(new F_Commands());
		getCommand("friendlyfire").setExecutor(new F_Commands());
		getCommand("healthregen").setExecutor(new F_Commands());
		getCommand("minplayer").setExecutor(new F_Commands());
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
            	Bukkit.getWorlds().forEach(w -> {
					w.setGameRuleValue("naturalRegeneration", "false");
					w.setGameRuleValue("doDaylightCycle", "false");
					w.setGameRuleValue("commandBlockOutput", "false");
					w.setGameRuleValue("logAdminCommands", "false");
					w.setGameRuleValue("sendCommandFeedback", "false");
					w.setGameRuleValue("doMobSpawning", "false");
					w.setGameRuleValue("mobGriefing", "false");
					w.setTime(6000L);
					w.setDifficulty(Difficulty.NORMAL);
					w.setWeatherDuration(999999999);
					w.getEntities().clear();
				});
            }
          }, 100L);

		logger.info("====================================");
	}

	@Override
	public void onDisable() {
		System.out.println(GetPrefix() + "§r : "+OreRun_Lang.PLUGIN_DISABLE.get());

		//BungeeCord
		if(F_Config.getBooleanConfig("BungeeCord.Support")){
			new F_BungeeCord().sendPlayersToLobby();
		}
	}
	
	// Game
	public void StopAutoStart(String Error){
		//Reset Timers
		ResetAllTimers();
		setState(GameState.WAITING);
		if(Error.isEmpty()){
			Bukkit.broadcastMessage(GetPrefix()+"§6"+Error);
		}

		for(Player pls : getPlayers()) {
			pls.setLevel(0);
		}

		for(Entry<Player, ScoreboardSign> scoreb : scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(0*1000);
			scoreb.getValue().setLine(0, OreRun_Lang.GAMESTATE_START.get()+"§r "+timerdate);
		}
	}

	public void PrepareGame() {
		if(isState(GameState.WAITING) && getCountPlayers() >= getMinPlayers()) {
			if(getStopStart()){return;}
			if(TM.CheckForStartGameTeam()) {				
				//Reset Timers
				ResetAllTimers();
				
				for(Player player : getPlayers()) {
					if(getTM().searchPlayerTeam(player) == null) {					
						getTM().addPlayer(player, getTM().randomTeam(player));
					}
				}
				
				AutoStart start = new AutoStart();
				setAutoStart(start);
				start.runTaskTimer(this, 0, 20);
				setState(GameState.STARTING);
			} else {
				//Reset Timers
				ResetAllTimers();
				setState(GameState.WAITING);
			}
		}
	}
	
	
	public void StartGame() {
		//Reset Timers
		ResetAllTimers();
		for(Teams team : getTeams()) {
			team.setPoints(0);
		}
		DepositChest = null;
		F_World.generate_newWorld();
	}
	
	public void Reload() {
		//ResetMap
		DepositChest = null;
		//Reset Timers
		ResetAllTimers();
		//Reset GameCycle
		setState(GameState.WAITING);
		//Reset Scoreboards
		for(Entry<Player, ScoreboardSign> scoreb : scorebaordMap.entrySet()) {
			scoreb.getValue().sdestroy();
		}
		scorebaordMap.clear();
		PlayerLifeMap.clear();
		//Reset Players
		for(Teams team : getTeams()) {
			if(team != null) {	
				team.setPoints(0);
				team.ClearTeamPlayers();
			}
		}
		players.clear();
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
            	for(Player p : Bukkit.getServer().getOnlinePlayers()) {
        			players.add(p);
        			p.setPlayerListName(p.getName());
        			p.setDisplayName(p.getName());
        			f_player.PlayerSetup(p);
        		}
            }
          }, 50L);
	}
	
	public void ReturnLobby(boolean Force) {
		if(isState(GameState.PLAYING) && getCountPlayers() <= getMinPlayers() || Force) {
			Reload();
		}
	}

	public void checkWin() {
		String msg = null;
		String msg2 = OreRun_Lang.WIN_LOBBY.get();
		if(getCountPlayers() == 0) {
			msg = OreRun_Lang.WIN_EQUALITY.get();
		}
		
		if(getCountPlayers() == 0) {
			Player winner = players.get(0);
			msg = winner.getName()+" "+OreRun_Lang.WIN_WON.get();
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.teleport(winner.getLocation());
			}
			OFireWorks.SpawnFireWorks(winner.getLocation());
		}
		
		if(msg == null) {
			Integer winpoint = 0;
			Teams winteam = null;
			for(Teams team : Teams) {
				if(team.getPoints() >= winpoint && getTM().CheckForStartGameTeam()) {
					winpoint = (int)team.getPoints();
					winteam = team;
				}
			}
			if(winteam != null) {
				msg = winteam.getTag()+winteam.getName()+" "+OreRun_Lang.WIN_WON.get();
				msg2 = (int)winteam.getPoints()+" Points";
				for(Player player : winteam.getTeamPlayers()) {					
					OFireWorks.SpawnFireWorks(player.getLocation());
				}

				for(Player player : Bukkit.getOnlinePlayers()) {
					if(player.getGameMode() == GameMode.SPECTATOR){
						player.teleport(getTM().getRandomPlayerInTeam(winteam).getLocation());
					}
				}
			} else {
				msg = OreRun_Lang.WIN_EQUALITY.get();
			}
		}
		
		if(msg != null) {
			setState(GameState.PREFINISH);
			ResetAllTimers();
			Finish finish = new Finish();
			setFinish(finish);
			finish.runTaskTimer(this, 0, 20); 
		}
		
		for(Player player : Bukkit.getOnlinePlayers()) {
			title.sendTitle(player, msg, msg2, 60);
		}
		//Reset Timers
		Bukkit.broadcastMessage(GetPrefix()+msg);
		
		return;
	}
	
	public void createChest() {
		Location chestLoc = F_World.getGoodLocation(Bukkit.getWorld(GameName), F_Config.getLocationConfig(GameName, "Locations.DepositChest"));
		chestLoc.getBlock().setType(Material.CHEST);
		chestLoc.getBlock().setMetadata("deposit", new FixedMetadataValue(this, true));
		DepositChest = chestLoc.getBlock();
		Bukkit.broadcastMessage(GetPrefix()+OreRun_Lang.OTHERS_DEPOSITCHEST.get()+" X:"+chestLoc.getX()+" Y:"+chestLoc.getY()+" Z:"+chestLoc.getZ()+" !");
	}
	
	public void ResetAllTimers() {
		//Reset PlayerInfo
		f_player.ScoreboardResetTimer();

		//Reset Timers
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
		ConfigurationSection teamsection = F_Config.getConfigurationSection("General.teams");
		for(String team : teamsection.getKeys(false)) {
			String name = teamsection.getString(team + ".name");
			String tag = teamsection.getString(team + ".color").replace("&", "§");
			String[] Items = teamsection.getString(team + ".item").split("/");
			String itemid = Items[0].split(":")[1].toUpperCase();
			byte itemdata = Byte.parseByte(Items[1]);
			
			Teams.add(new Teams(team, name, tag, itemid, itemdata));
		}
		
		System.out.println(GetPrefix() + Teams.size() + " teams is loaded !");
	}
	
	public List<Teams> getTeams(){
		return Teams;
	}
	
	public TeamsManager getTM() {
		return TM;
	}
	
	//Player
	
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
	
	// Item
	
	public double getItemPoint(ItemStack item) {
		double returnvalue = 0;
		for(String s : getConfig().getStringList("Points.Items")) {
			//String itemname = s.split(":")[0];
			Double itempoint = Double.valueOf(s.split(":")[1]);
			if(s.contains(item.getType().name())) {
				returnvalue = itempoint;
				break;
			}
		}
		return returnvalue;
	}
	
	public void removeAllDroppedItems(String world_name) {
        World world = Bukkit.getServer().getWorld(world_name);//get the world
        
        for (Entity entity : world.getEntities()) {
            if(entity instanceof Item) {
            	entity.remove(); 
            } 
        }
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=item]");
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

	public String GetPrefix(){return "§7[§eOreRun§7] §r";}

	public void SetPVPState(Boolean state) {
		PVPState = state;
	}

	public Boolean GetPVPState() {
		return this.PVPState;
	}

	public Boolean getNoFallDamage() {
		return NoFallDamage;
	}

	public void setNoFallDamage(Boolean noFallDamage) {
		NoFallDamage = noFallDamage;
	}

	public Boolean getInstantIngot() {
		return InstantIngot;
	}

	public void setInstantIngot(Boolean instantIngot) {
		InstantIngot = instantIngot;
	}

	public Boolean getInstantIngotFortune() {
		return InstantIngotFortune;
	}

	public void setInstantIngotFortune(Boolean instantIngotFortune) {
		InstantIngotFortune = instantIngotFortune;
	}

	public Boolean getFriendlyFire() {
		return FriendlyFire;
	}

	public void setFriendlyFire(Boolean friendlyFire) {
		FriendlyFire = friendlyFire;
	}

	public Boolean getHealthRegen() {
		return HealthRegen;
	}

	public void setHealthRegen(Boolean healthRegen) {
		HealthRegen = healthRegen;
		World world = Bukkit.getWorld(GameName);
		if(world != null){
			world.setGameRuleValue("naturalRegeneration", String.valueOf(healthRegen));
		}
	}

	public int getMinPlayers(){
		return MinPlayers;
	}

	public Boolean SetMinPlayers(int count){
		if(count > 0){
			MinPlayers = count;
			return true;
		}
		return false;
	}

	public Boolean getCanChangeTeam(){
		return CanChangeTeam;
	}

	public void setCanChangeTeam(Boolean can){
		CanChangeTeam = can;
	}

	public Boolean getStopStart() {
		return StopStart;
	}

	public void setStopStart(Boolean stopStart) {
		StopStart = stopStart;
	}
}
