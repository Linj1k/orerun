package fr.kinj14.orerun.listeners;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.teams.Teams;
import fr.kinj14.orerun.teams.TeamsManager;


public class ChatListeners implements Listener {
	protected static final Logger logger = Logger.getLogger("Minecraft");
	protected final transient Server server;
	protected final Main main = Main.getInstance();
	private static ChatListeners instance;
	
	public ChatListeners(final Server server) {
		this.server = server;
		this.setInstance(this);
	}
	
	public static ChatListeners getInstance() {
		return instance;
	}

	public void setInstance(ChatListeners instance) {
		ChatListeners.instance = instance;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		String message = FormatChat(player, event.getMessage());
		
		if(player.isDead() || player.getGameMode() == GameMode.SPECTATOR) {
			event.setCancelled(true);
			return;
		}
		
		if(event.getMessage().contains("!")) {
			message = "§r[§7Global§r]"+message.replace("!", "");
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.sendMessage(message);
			}
		} else {
			TeamsManager tm = main.getTM();
			Teams playerTeam = tm.searchPlayerTeam(player);
			if(tm != null && playerTeam != null) {
				message = "§r["+playerTeam.getTag()+"Team§r]"+message;
				for(Player pteam : playerTeam.getTeamPlayers()) {
					pteam.sendMessage(message);
				}
			} else {
				message = "§r[§7Global§r]"+message.replace("!", "");
				for(Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(message);
				}
			}
		}
		System.out.println(message);
		event.setCancelled(true);
		return;
	}
	
	public String FormatChat(Player player, String msg) {
		//{DisplayName},{PlayerName},{Level},{Health},{MaxHealth},{Food},{CustomName},{Gamemode},{Locale},{PlayerListName},{World},{Message}
		String finalmsg = main.cfg.getString("General.ChatFormat");
		finalmsg = finalmsg.replace("{DisplayName}", player.getDisplayName());
		finalmsg = finalmsg.replace("{PlayerName}", player.getName());
		finalmsg = finalmsg.replace("{Level}", String.valueOf(player.getExp()));
		finalmsg = finalmsg.replace("{Health}", String.valueOf(player.getHealth()));
		finalmsg = finalmsg.replace("{Food}", String.valueOf(player.getFoodLevel()));
		finalmsg = finalmsg.replace("{MaxHealth}", String.valueOf(player.getHealthScale()));
		finalmsg = finalmsg.replace("{CustomName}", String.valueOf(player.getCustomName()));
		finalmsg = finalmsg.replace("{Gamemode}", String.valueOf(player.getGameMode()));
		finalmsg = finalmsg.replace("{Locale}", String.valueOf(player.getLocale()));
		finalmsg = finalmsg.replace("{PlayerListName}", String.valueOf(player.getPlayerListName()));
		finalmsg = finalmsg.replace("{World}", String.valueOf(player.getWorld()));
		finalmsg = finalmsg.replace("{Message}", msg);
		return finalmsg;
	}
}
