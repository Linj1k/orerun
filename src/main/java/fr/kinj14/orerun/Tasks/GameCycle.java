package fr.kinj14.orerun.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.library.ScoreboardSign;

public class GameCycle extends BukkitRunnable {
	
	protected final Main main = Main.getInstance();
	private int defaulttimer = 300;
	private int timer = defaulttimer;
	private List<String> Steps = new ArrayList<>();
	private final int ChestTime;
	private final int PVPTime;
	
	public GameCycle() {
		int timer = main.F_Config.getIntConfig("Timers.GameTime") * 60;
		this.defaulttimer = main.F_Config.getIntConfig("Timers.GameTime") * 60;
		this.timer = timer;
		Steps = main.getConfig().getStringList("Timers.Steps");
		ChestTime = main.F_Config.getIntConfig("Timers.ChestTime") * 60;
		PVPTime = main.F_Config.getIntConfig("Timers.PVPTime") * 60;
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
		World world = Bukkit.getWorld(main.GameName);
		WorldBorder wb = world.getWorldBorder();
		if(wb.getSize() > 30) {
			double BorderSize = wb.getSize() - Block;
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_BORDER.get().replace("{blocks}", String.valueOf(BorderSize)));
			wb.setSize(BorderSize, SmoothDelay);
		}
	}

	@Override
	public void run() {
		if(timer == defaulttimer) {
			Bukkit.broadcastMessage(main.GetPrefix()+ OreRun_Lang.GAMESTATE_GAMECYCLE_GOODLUCKMSG.get());
		}
		
		for(String step : Steps) {
			if(getSecondsFromStep(step) * 60 == timer) {
				setWorldBorder(getBlockFromStep(step), getSmoothDelayFromStep(step));
			}
		}
		
		if(ChestTime == timer) {
			main.createChest();
		}
		
		if(PVPTime == timer) {
			main.SetPVPState(true);
			for(Player player : main.getPlayers()) {
				main.f_player.SetPlayerLife(player, 0);
			}
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.GAMESTATE_GAMECYCLE_PVPACTIVATE.get());
		}
		
		for(Entry<Player, ScoreboardSign> scoreb : main.scorebaordMap.entrySet()) {
			String timerdate = new SimpleDateFormat("mm:ss").format(timer*1000);
			scoreb.getValue().setLine(0, OreRun_Lang.GAMESTATE_GAMECYCLE.get()+"§r "+timerdate);
		}
		
		if(main.isState(GameState.PREFINISH) || main.isState(GameState.WAITING)) {
			main.setGameCycle(null);
			cancel();
		}
		
		main.f_player.ScoreboardUpdateBorder();
		
		if(timer == 0) {
			main.checkWin();
			main.setGameCycle(null);
			cancel();
		}
		
		timer--;
	}

}
