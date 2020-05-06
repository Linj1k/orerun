package fr.kinj14.orerun.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;

public class TeamsManager {
	protected final Main main = Main.getInstance();
	public Boolean FriendlyFire;
	
	public TeamsManager() {
		FriendlyFire = main.cfg.getBoolean("General.FriendlyFire");
	}
	
	public Teams searchPlayerTeam(Player player) {
		Teams team = null;
		for(Teams allteam : main.getTeams()) {
			if(allteam.HavePlayer(player)) {
				team = allteam;
				break;
			}
		}
		return team;
	}

	public void addPlayer(Player player, Teams team) {
		if(team.getTeamPlayersCount() >= team.getMaxPlayers()) {
			player.sendMessage("§7[§eOreRun§7]§6 This "+team.getTag()+"team §6is already full!");
			return;
		}
		for(Teams ateam : main.getTeams()) {
			if(ateam.HavePlayer(player) && ateam.getId() != team.getId()) {
				ateam.removePlayer(player);
			}
		}
		if(!team.HavePlayer(player)) {
			team.addPlayer(player);
			Bukkit.broadcastMessage("§7[§eOreRun§7]§6 "+ player.getName() +" join "+ team.getTag() + team.getName() +" Team §6!");
		}
		
		updateScoreBoard(player);
	}
	
	public void removePlayer(Player player, Teams team) {
		if(team.HavePlayer(player)) {
			team.removePlayer(player);
			Bukkit.broadcastMessage("§7[§eOreRun§7]§6 "+ player.getName() +" leave "+ team.getTag() + team.getName() +" Team §6!");
		}
		updateScoreBoard(player);
	}
	
	public Teams randomTeam(Player player) {
		List<Teams> rteam = new ArrayList<>();
		for(Teams team : main.getTeams()) {
			if(team.getTeamPlayersCount() < team.getMaxPlayers() && !team.HavePlayer(player)) {
				rteam.add(team);
			}
		}
		
		Teams finalteam = rteam.get(new Random().nextInt(rteam.size()));
		return finalteam;
	}
	
	public void updateScoreBoard(Player player) {
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			Teams scorebTeam = main.getTM().searchPlayerTeam(scoreb.getKey());
			scoreb.getValue().setLine(5, "Team : "+scorebTeam.getTag()+scorebTeam.getName());
		}
	}
}
