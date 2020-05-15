package fr.kinj14.orerun.Tasks;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.utils.GameState;

public class Finish extends BukkitRunnable {
	protected final Main main = Main.getInstance();
	private int defaulttimer = 12;
	private int timer = defaulttimer;

	@Override
	public void run() {
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(1, "Finish : "+timerdate);
		}
		
		for(Player pls : main.getPlayers()) {
			pls.setLevel(timer);
		}
		
		if(timer == 5) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.kickPlayer("The Server is restarting...");
			}
		}
		
		if(timer == 0) {
			main.setState(GameState.FINISH);
			Bukkit.spigot().restart();
			cancel();
		}
		timer--;
	}

}
