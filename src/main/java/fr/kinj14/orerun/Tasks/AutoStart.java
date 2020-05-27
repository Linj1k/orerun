package fr.kinj14.orerun.Tasks;

import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import fr.kinj14.orerun.enums.OreRun_Lang;
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
		this.defaulttimer = main.F_Config.getIntConfig("Timers.StartTime");
		this.timer = defaulttimer;
	}

	@Override
	public void run() {
		timer--;
		if(main.getCountPlayers() < main.MinPlayers) {
			main.StopAutoStart(OreRun_Lang.GAMESTATE_START_ENOUGHPLAYERS.get());
			cancel();
		}

		if(main.getStopStart()){
			main.StopAutoStart("");
			cancel();
		}

		if(!main.getTM().CheckForStartGameTeam()) {
			main.StopAutoStart(OreRun_Lang.GAMESTATE_START_TEAMCOUNT.get());
			cancel();
		}
		
		for(Player pls : main.getPlayers()) {
			pls.setLevel(timer);
		}
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(0, OreRun_Lang.GAMESTATE_START.get()+"§r "+timerdate);
		}
		
		
		if(timer == 10 || timer == 5 || timer == 3 || timer == 2 || timer == 1) {
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_START_GAMESTARTMSG.get().replace("{time}", String.valueOf(timer)));
		}
		
		if(timer == 0) {
			main.StartGame();
			cancel();
		}
	}
}
