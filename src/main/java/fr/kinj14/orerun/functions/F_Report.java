package fr.kinj14.orerun.functions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.OreRun_Lang;
import fr.kinj14.orerun.utils.QueryUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class F_Report {
	protected final Main main = Main.getInstance();
	private static F_Report instance;

	public F_Report() {
		this.setInstance(this);
	}
	
	public static F_Report getInstance() {
		return instance;
	}

	public void setInstance(F_Report instance) {
		F_Report.instance = instance;
	}
	
	public void reportBug(String server_name, Player player, String description) {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("server_name", server_name);
		parameters.put("name", player.getName());
		parameters.put("description", description);
		String query = new QueryUtils().query_POST("https://orerun.ovh/app/php/report.php", parameters);

		player.sendMessage(main.GetPrefix()+query);
	}
	
	public void reportPlayer(Player owner, Player report, String desc) {
		String ReportMsg = OreRun_Lang.PLAYER_REPORTMSG.get()
				.replace("{reporter_name}", owner.getName())
				.replace("{player_name}", report.getName())
				.replace("{Description}", desc);
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isOp()) {
				player.sendMessage(main.GetPrefix()+ReportMsg);
			}
		}
	}
}
