package fr.kinj14.orerun.functions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.teams.Teams;

public class F_Player {
	//Instance
	public static F_Player instance = null;
	
	public static F_Player getInstance() {
		return instance;
	}
	
	protected final Main main = Main.getInstance();
	
	public F_Player() {		
		instance = this;
	}
	
	// Player
	public void eliminatePlayer(Player player) {
		if(main.HavePlayer(player)) {
			main.removePlayer(player);
		}
		
		if(main.PlayerLifeMap.containsKey(player)) {
			main.PlayerLifeMap.remove(player);
		}
		
		player.setGameMode(GameMode.SPECTATOR);
		player.sendMessage(main.GetPrefix()+OreRun_Lang.WIN_LOSEMSG.get());
		main.checkWin();
	}
		
	public Player getRandomPlayer() {
		Player player = null;
		
		List<Player> Players = new ArrayList<>();
		for(Player rplayer : main.getPlayers()) {
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
		
		if(!main.HavePlayer(player)) {
			main.addPlayer(player);
		}
		
		if(main.isState(GameState.WAITING)) {
			PlayerSetupLobby(player);
		}
		
		//Scoreboard Setup
		if(main.scorebaordMap.containsKey(player)) {
			ScoreboardSign lastscore = main.scorebaordMap.get(player);
			lastscore.sdestroy();
		}
		ScoreboardSign sb = new ScoreboardSign(player, "§eOreRun");
		sb.create();
		String timerdate = new SimpleDateFormat("mm:ss").format(0*1000);
		sb.setLine(0, "§cTime:§r "+timerdate);
		sb.setLine(1, OreRun_Lang.SCOREBOARD_PLAYERS.get()+" "+String.valueOf(main.getCountPlayers())+"/"+String.valueOf(main.getTM().GetMaxPlayersTeam()));
		sb.setLine(2, OreRun_Lang.SCOREBOARD_BORDER.get()+" "+"0");
		sb.setLine(3, OreRun_Lang.SCOREBOARD_STATS.get());
		Teams scorebTeam = main.getTM().searchPlayerTeam(player);
		if(scorebTeam != null) {					
			sb.setLine(4, scorebTeam.getTag()+"█"+OreRun_Lang.SCOREBOARD_POINTS.get()+" "+"0");
		} else {
			sb.setLine(4, OreRun_Lang.SCOREBOARD_POINTS.get()+" "+"0");
		}
		sb.setLine(5, OreRun_Lang.SCOREBOARD_LIFE.get()+" "+main.PlayerLife);
		sb.setLine(6, "§7─────────");
		sb.setLine(7, "§eplay.orerun.ovh");
		
		main.scorebaordMap.put(player, sb);
		
		// Game is started
		if(main.isState(GameState.PLAYING)) {
			player.teleport(getRandomPlayer());
			player.setGameMode(GameMode.SPECTATOR);
			player.sendMessage(main.GetPrefix()+OreRun_Lang.PLAYER_GAMEISSTARTEDMSG.get());
		}
	}
	
	public void ScoreboardUpdateBorder() {
		World world = Bukkit.getWorld(main.GameName);
		if(world != null) {
			WorldBorder wb = world.getWorldBorder();
			for (Player player : main.getPlayers()) {
				if (main.scorebaordMap.containsKey(player) && player != null) {
					ScoreboardSign sb = main.scorebaordMap.get(player);
					sb.setLine(2, OreRun_Lang.SCOREBOARD_BORDER.get()+" "+ (int) wb.getSize());
				}
			}
		}
	}
	
	public void ScoreboardUpdatePlayers() {
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			scoreb.getValue().setLine(1, OreRun_Lang.SCOREBOARD_PLAYERS.get()+" "+String.valueOf(main.getCountPlayers())+"/"+String.valueOf(main.getTM().GetMaxPlayersTeam()));
		}
	}

	public void ScoreboardResetTimer() {
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			scoreb.getKey().setLevel(0);
			scoreb.getValue().setLine(0, "§cTime:§r 00:00");
		}
	}
	
	public void PlayerSetupLobby(Player player) {
		// Spawn
		player.teleport(main.lobby);
		player.getInventory().clear();
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setExp(0f);
		player.setGameMode(GameMode.ADVENTURE);
		
		// Add item for select Team
		for(Teams team : main.getTeams()) {
			player.getInventory().addItem(team.getIcon());
		}
		
		ItemStack lobbyitem = new ItemStack(Material.COMPASS, 1);
		ItemMeta IM = lobbyitem.getItemMeta();
		IM.setDisplayName(OreRun_Lang.ITEMS_LOBBY.get());
		lobbyitem.setItemMeta(IM);
		player.getInventory().setItem(8, lobbyitem);
		//getTM().addPlayer(player, getTM().randomTeam(player));

		if(player.hasPermission("canModifySettings")){
			player.getInventory().setItem(7, new F_SetupItem().GetItem());
		}
		
		// Look for LaunchGame
		main.PrepareGame();
	}
		
	public void PlayerSetupGame(Player player) {
		Teams playerTeam = main.getTM().searchPlayerTeam(player);
		if(playerTeam == null) {
			main.getTM().randomTeam(player);
			playerTeam = main.getTM().searchPlayerTeam(player);
		}
		player.setFireTicks(0);
		player.teleport(playerTeam.getSpawn());
		player.setBedSpawnLocation(playerTeam.getSpawn(), true);
		player.getInventory().clear();
		//clear player armor
		ItemStack[] emptyArmor = new ItemStack[4];
		for(int i=0 ; i<emptyArmor.length ; i++){
			emptyArmor[i] = new ItemStack(Material.AIR);
		}
		player.getInventory().setArmorContents(emptyArmor);
		player.setFoodLevel(20);
		player.setHealth(20);
		player.setExp(0f);
		player.setGameMode(GameMode.SURVIVAL);
		player.updateInventory();
		for(PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}
		player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 1), false);
		main.title.sendTitle(player, OreRun_Lang.GAMESTATE_START_LAUNCHINGGAME.get(), OreRun_Lang.GAMESTATE_START_TELEPORTING.get(), 20);
		
		main.PlayerLifeMap.put(player, main.PlayerLife);
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(main.HavePlayer(player)) {
			main.removePlayer(player);
		}
		
		for(Teams team : main.getTeams()) {
			if(team.HavePlayer(player)) {
				team.removePlayer(player);
			}
		}
		
		if(main.scorebaordMap.containsKey(player)) {
			ScoreboardSign lastscore = main.scorebaordMap.get(player);
			lastscore.sdestroy();
		}
		
		main.ReturnLobby(false);
	}
	
	public void SetPlayerLife(Player player, int Life) {
		if(main.PlayerLifeMap.containsKey(player)) {
			main.PlayerLifeMap.replace(player, Life);
		}
		
		if(main.scorebaordMap.containsKey(player)) {
			main.scorebaordMap.get(player).setLine(5, OreRun_Lang.SCOREBOARD_LIFE.get()+" "+Life);
		}
	}
	
	public int GetPlayerLife(Player player) {
		if(main.PlayerLifeMap.containsKey(player)) {
			return main.PlayerLifeMap.get(player);
		}
		return 0;
	}
}
