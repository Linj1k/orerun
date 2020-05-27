package fr.kinj14.orerun.Tasks;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;

import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.functions.F_BungeeCord;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.library.ScoreboardSign;

public class Finish extends BukkitRunnable {
	protected final Main main = Main.getInstance();
	private int defaulttimer = 12;
	private int timer = defaulttimer;

	@Override
	public void run() {
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(0, OreRun_Lang.GAMESTATE_FINISH.get()+"§r "+timerdate);
		}
		
		for(Player pls : main.getPlayers()) {
			pls.setLevel(timer);
		}
		
		if(timer == 0) {
			main.setState(GameState.FINISH);
			main.ReturnLobby(true);

			//BungeeCord
			if(main.F_Config.getBooleanConfig("BungeeCord.Support")){
				new F_BungeeCord().sendPlayersToLobby();
			}

			cancel();
		}
		timer--;
	}

}
