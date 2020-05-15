package fr.kinj14.orerun.Tasks;

import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;

public class AutoStart extends BukkitRunnable {
	
	protected final Main main = Main.getInstance();
	private int defaulttimer = 10;
	private int timer = defaulttimer;
	
	public AutoStart() {
		this.defaulttimer = main.cfg.getInt("Timers.StartTime");
		this.timer = defaulttimer;
	}

	@Override
	public void run() {
		timer--;
		if(main.getCountPlayers() < main.MinPlayers || Bukkit.getOnlinePlayers().size() < main.MinPlayers) {
			main.ReturnLobby(true);
			Bukkit.broadcastMessage("§7[§eOreRun§7]§6 There are not enough players !");
			cancel();
		}
		
		for(Player pls : main.getPlayers()) {
			pls.setLevel(timer);
		}
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(1, "Start : "+timerdate);
		}
		
		
		if(timer == 10 || timer == 5 || timer == 3 || timer == 2 || timer == 1) {
			Bukkit.broadcastMessage("§7[§eOreRun§7]§6 The game starts in §e"+ timer +"§6s !");
		}
		
		if(timer == 0) {
			main.StartGame();
			cancel();
		}
	}

}
