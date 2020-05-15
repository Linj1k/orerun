package fr.kinj14.orerun.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.library.ScoreboardSign;
import fr.kinj14.orerun.utils.GameState;

public class GameCycle extends BukkitRunnable {
	
	protected final Main main = Main.getInstance();
	private int defaulttimer = 300;
	private int timer = defaulttimer;
	private List<String> Steps = new ArrayList<>();
	
	public GameCycle() {
		int timer = main.cfg.getInt("Timers.GameTime");
		this.defaulttimer = main.cfg.getInt("Timers.GameTime");
		this.timer = timer;
		Steps = main.getConfig().getStringList("Timers.Steps");
	}
	
	public Integer getSecondsFromStep(String Step) {
		return Integer.parseInt(Step.split(", ")[0]);
	}
	
	public Integer getBlockFromStep(String Step) {
		return Integer.parseInt(Step.split(", ")[1]);
	}
	
	public Integer getSmoothDelayFromStep(String Step) {
		return Integer.parseInt(Step.split(", ")[2]);
	}
	
	private void setWorldBorder(Integer Block, Integer SmoothDelay) {
		World world = Bukkit.getWorld(main.worldname);
		WorldBorder wb = world.getWorldBorder();
		if(wb.getSize() > 30) {
			wb.setSize(wb.getSize() - Block, SmoothDelay);
		}
	}

	@Override
	public void run() {
		if(timer == defaulttimer) {
			Bukkit.broadcastMessage("§7[§eOreRun§7]§r Good luck to all !");
		}
		
		for(String step : Steps) {
			if(getSecondsFromStep(step) == timer) {
				setWorldBorder(getBlockFromStep(step), getSmoothDelayFromStep(step));
			}
		}
		
		if(main.cfg.getInt("Timers.ChestTime") == timer) {
			main.createChest();
		}
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(1, "Game : "+timerdate);
		}
		
		if(main.isState(GameState.PREFINISH) || main.isState(GameState.WAITING)) {
			main.setGameCycle(null);
			cancel();
		}
		
		main.ScoreboardUpdateBorder();
		
		if(timer == 0) {
			main.checkWin();
			main.setGameCycle(null);
			cancel();
		}
		
		timer--;
	}

}
