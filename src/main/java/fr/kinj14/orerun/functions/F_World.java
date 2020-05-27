package fr.kinj14.orerun.functions;

import java.io.File;
import java.util.Iterator;
import java.util.Random;

import fr.kinj14.orerun.Tasks.GameCycle;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.teams.Teams;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import fr.kinj14.orerun.Main;

// BETA !
public class F_World {
	protected final Main main = Main.getInstance();

	public void generate_newWorld() {
		Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.WORLD_DESTROYOLDWORLD.get());
		World world = findWorld(main.GameName);
		if(world != null) {
			Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.WORLD_UNLOADOLDWORLD.get());
			for(Player p : world.getPlayers()){
				p.teleport(Bukkit.getWorld(main.Lobby_WorldName).getSpawnLocation());
			}
			Bukkit.getServer().unloadWorld(world, false);
			System.out.println(world.getName() + " unloaded!");
		}
		if(new File(main.GameName).exists()){
			Destroy_newWorld(new File(main.GameName));
		}

		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			public void run() {
				File Path = new File(main.GameName);
				if(!new File(main.GameName).exists()) {
					Bukkit.broadcastMessage(main.GetPrefix()+OreRun_Lang.WORLD_GENERATENEWWORLD.get());
					WorldCreator wc = new WorldCreator(main.GameName);

					wc.seed(new Random().nextLong());
					wc.environment(World.Environment.NORMAL);
					wc.type(WorldType.NORMAL);
					wc.generatorSettings("biome_1");
					wc.createWorld();

					World world = Bukkit.getWorld(main.GameName);
					if (!main.F_Config.getBooleanConfig("General.EnableHealthRegen")){
						world.setGameRuleValue("naturalRegeneration", "false");
					}
					world.setGameRuleValue("doDaylightCycle", "false");
					world.setGameRuleValue("commandBlockOutput", "false");
					world.setGameRuleValue("logAdminCommands", "false");
					world.setGameRuleValue("sendCommandFeedback", "false");
					world.setGameRuleValue("doMobSpawning", "false");
					world.setTime(6000);
					world.setDifficulty(Difficulty.NORMAL);
					world.setWeatherDuration(999999999);

					Location GameCenter = main.F_Config.getLocationConfig(main.GameName, "Locations.GameCenter");
					int GameSize = main.F_Config.getIntConfig("WorldBorder.GameSize");
					WorldBorder wb = world.getWorldBorder();
					wb.setCenter(GameCenter);
					wb.setSize(GameSize);

					main.setState(GameState.PLAYING);

					GameCycle cycle = new GameCycle();
					main.setGameCycle(cycle);
					cycle.runTaskTimer(main, 0, 20);

					for(Teams team : main.getTeams()){
						team.setSpawn(spawnbeast(world));
					}

					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
						public void run() {
							for(Player player : main.getPlayers()){
								if(player != null) {
									main.f_player.PlayerSetupGame(player);
								}
							}
						}
					}, 50L);
				}
			}
		}, 50L);
	}
	
	public boolean Destroy_newWorld(File path) {
		if(path.exists()) {
            File files[] = path.listFiles();
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    Destroy_newWorld(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
	}

	public void DeleteEntities(String world_name) {
		if(world_name != null){
			World world = Bukkit.getWorld(world_name);
			for (LivingEntity ent : world.getLivingEntities()) {
				if (ent instanceof Animals) {continue;}
				if (ent.getType() == EntityType.PLAYER || ent.getType() == EntityType.ITEM_FRAME ||
						ent.getType() == EntityType.MINECART) continue;

				ent.remove();
			}
		}
	}

	public Location spawnbeast(World world) {
		int x = new Random().nextInt((int)world.getWorldBorder().getSize()/2);
		int z = new Random().nextInt((int)world.getWorldBorder().getSize()/2);

		Block block = world.getHighestBlockAt(x, z);
		//Location yb = new Location(world, x,block.getY() - 1,z);
		//Block CheckBlock = yb.getBlock();
		//if(CheckBlock.getType() == Material.WATER || CheckBlock.getType() == Material.LAVA || CheckBlock.getType() == Material.STATIONARY_WATER || CheckBlock.getType() == Material.STATIONARY_LAVA){
		//	return spawnbeast(world);
		//}

		int y = block.getY() + 1;
		Location loc = new Location(world, x, y, z);
		return loc;
	}

	public Location getGoodLocation(World world, Location loca) {
		int x = (int)loca.getX();
		int z = (int)loca.getZ();
		Block block = world.getHighestBlockAt(x, z);

		int y = block.getY();
		Location loc = new Location(world, x, y, z);
		return loc;
	}

	public World findWorld(String string){
		Iterator<World> worlds = Bukkit.getServer().getWorlds().iterator();
		while(worlds.hasNext()){
			World world = worlds.next();
			if(world.getName().equalsIgnoreCase(string)){
				return world;
			}
		}
		return null;
	}
}