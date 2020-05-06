package fr.kinj14.orerun.library;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.FooterEmbed;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;

import fr.kinj14.orerun.Main;

public class DiscordReport implements Listener {
	//Inspiration from : https://github.com/Acme-Plugins/DiscordReport
	protected final Main main = Main.getInstance();
	String playerReportWebhook = main.cfg.getString("Discord.playerReportWebhook");
	String bugReportWebhook = main.cfg.getString("Discord.bugReportWebhook");
	
	public void checkCfg() {
		if(playerReportWebhook.equals("") | bugReportWebhook.equals("")) {
            System.out.println("§7[§eOreRun§7]§r You have not set WebHook URLs in config.yml");
            System.out.println("§7[§eOreRun§7]§r FATAL! The plugin is not functional and will shut down!!");
        }
	}
	
	@EventHandler
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        String message = String.join(" ", args);
        String player = sender.getName();
        if(command.getName().equals("player")){
        	checkCfg();
            if(message.equals("")){
                sender.sendMessage(ChatColor.RED + "Please supply a message!");
            }else{
                TemmieWebhook playerwebhook = new TemmieWebhook(playerReportWebhook);
                DiscordEmbed playerembed = DiscordEmbed.builder()
                        .title("Player Report - " + player) // Report type and reporter
                        .description(message) // Report Description
                        .footer(FooterEmbed.builder() // Credit Footer
                                .text("DiscordReport - by konsyr11") // Credit myself
                                .build()) // Build the footer
                        .thumbnail(ThumbnailEmbed.builder() // Thumbnail
                                .url("https://cdn.discordapp.com/attachments/634094893752516609/684644756508508162/user-4.png") // Thumbnail URL
                                .height(128) // Specify thumbnail height
                                .build()) // Build the thumbnail
                        .build(); // Build the embed

                DiscordMessage playermessage = DiscordMessage.builder()
                        .username("DiscordReport") // Name the Webhook
                        .content("@here") // Mass ping online staff
                        .avatarUrl("https://cdn.discordapp.com/attachments/634094893752516609/684644829652844550/warning.png") // Avatar url
                        .embeds(Arrays.asList(playerembed)) // Insert the embed
                        .build(); // Build the message
                playerwebhook.sendMessage(playermessage);
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "DiscordReport" + ChatColor.WHITE + "] " + ChatColor.GOLD + "The player has been reported to the staff team.");
            }
        }
        if(command.getName().equals("bug")){
        	checkCfg();
            if(message.equals("")){
                sender.sendMessage(ChatColor.RED + "Please supply a message!");
            }else{
                TemmieWebhook bugwebhook = new TemmieWebhook(bugReportWebhook);
                DiscordEmbed bugembed = DiscordEmbed.builder()
                        .title("Bug Report - " + player) // Report type and reporter
                        .description(message) // Report Description
                        .footer(FooterEmbed.builder() // Credit Footer
                                .text("DiscordReport - by konsyr11") // Credit myself
                                .build()) // Build the footer
                        .thumbnail(ThumbnailEmbed.builder() // Thumbnail
                                .url("https://cdn.discordapp.com/attachments/634094893752516609/684645315256778755/error.png") // Thumbnail URL
                                .height(128) // Specify thumbnail height
                                .build()) // Build the thumbnail
                        .build(); // Build the embed

                DiscordMessage playermessage = DiscordMessage.builder()
                        .username("DiscordReport") // Name the Webhook
                        .content("@here") // Mass ping online staff
                        .avatarUrl("https://cdn.discordapp.com/attachments/634094893752516609/684644829652844550/warning.png") // Avatar url
                        .embeds(Arrays.asList(bugembed)) // Insert the embed
                        .build(); // Build the message
                bugwebhook.sendMessage(playermessage);
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.RED + "DiscordReport" + ChatColor.WHITE + "] " + ChatColor.GOLD + "The bug has been reported to the staff team.");
            }
        }
        return false;
    }
}
