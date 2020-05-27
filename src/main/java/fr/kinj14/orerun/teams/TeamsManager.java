package fr.kinj14.orerun.teams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;

public class TeamsManager {
	protected final Main main = Main.getInstance();
	
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
			player.sendMessage(main.GetPrefix()+OreRun_Lang.TEAMS_TEAMISFULL.get().replace("{teamcolor}", team.getTag()));
			return;
		}
		for(Teams ateam : main.getTeams()) {
			if(ateam.HavePlayer(player) && ateam != team) {
				ateam.removePlayer(player);
			}
		}
		if(!team.HavePlayer(player)) {
			team.addPlayer(player);
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.TEAMS_JOINMSG.get()
					.replace("{playername}", player.getName())
					.replace("{teamcolor}", team.getTag())
					.replace("{teamname}", team.getName()));
		}
		
		updateScoreBoard(player);
		main.PrepareGame();
	}
	
	public void removePlayer(Player player, Teams team) {
		if(team.HavePlayer(player)) {
			team.removePlayer(player);
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.TEAMS_LEAVEMSG.get()
					.replace("{playername}", player.getName())
					.replace("{teamcolor}", team.getTag())
					.replace("{teamname}", team.getName()));
		}
		updateScoreBoard(player);
		main.PrepareGame();
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
			if(scoreb.getKey() != null && scoreb.getValue() != null) {				
				Teams scorebTeam = main.getTM().searchPlayerTeam(scoreb.getKey());
				if(scorebTeam != null) {					
					scoreb.getValue().setLine(4, scorebTeam.getTag()+"█"+OreRun_Lang.SCOREBOARD_POINTS.get()+" "+"0");
				} else {
					scoreb.getValue().setLine(4, OreRun_Lang.SCOREBOARD_POINTS.get()+" "+"0");
				}
			}
		}
	}
	
	public void updateScoreBoardPoint(Player player) {
		Teams scorebTeam = searchPlayerTeam(player);
		if(scorebTeam != null) {
			for(Player pla : scorebTeam.getTeamPlayers()) {
				if(main.scorebaordMap.containsKey(pla)) {
					main.scorebaordMap.get(pla).setLine(4, scorebTeam.getTag()+"█"+OreRun_Lang.SCOREBOARD_POINTS.get()+" "+(int)scorebTeam.getPoints());
				}
			}
		}
	}
	
	public boolean CheckForStartGameTeam() {
		int count = 0;
		for(Teams team : main.getTeams()){
			if(team.getTeamPlayersCount() > 0) {
				count = count + 1;
			}
		}
		return count >= main.MinPlayers;
	}
	
	public int GetMaxPlayersTeam() {
		int count = 0;
		for(Teams team : main.getTeams()){
			count = count + team.getMaxPlayers();
		}
		return count;
	}

	public Player getRandomPlayerInTeam(Teams team){
		int randomNumber = new Random().nextInt(team.getTeamPlayersCount()+1-1)+1;
		return team.getTeamPlayers().get(randomNumber);
	}
}
