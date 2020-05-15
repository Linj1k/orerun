package fr.kinj14.orerun.library;

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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Report {
	private static Report instance;

	public Report() {
		this.setInstance(this);
	}
	
	public static Report getInstance() {
		return instance;
	}

	public void setInstance(Report instance) {
		Report.instance = instance;
	}
	
	public static class ParameterStringBuilder {
	    public static String getParamsString(Map<String, String> params) 
	      throws UnsupportedEncodingException{
	        StringBuilder result = new StringBuilder();
	 
	        for (Map.Entry<String, String> entry : params.entrySet()) {
	          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
	          result.append("=");
	          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
	          result.append("&");
	        }
	 
	        String resultString = result.toString();
	        return resultString.length() > 0
	          ? resultString.substring(0, resultString.length() - 1)
	          : resultString;
	    }
	}
	
	public String getip() {
		try
        {
            URL url = new URL("https://orerun.ovh/app/php/getip.php");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            
            try(BufferedReader br = new BufferedReader(
    		  new InputStreamReader(con.getInputStream(), "utf-8"))) {
    		    StringBuilder response = new StringBuilder();
    		    String responseLine = null;
    		    while ((responseLine = br.readLine()) != null) {
    		        response.append(responseLine.trim());
    		    }
    		    con.disconnect();
    		    return response.toString();
    		}
        }catch(IOException e)
        {
            e.printStackTrace();
        }
		return "";
	}
	
	public void query_POST(String server_name, Player player, String description) {
		try
        {
            URL url = new URL("https://orerun.ovh/app/php/report.php");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            
            Map<String, String> parameters = new HashMap<>();
            parameters.put("server_name", server_name);
            parameters.put("name", player.getName());
            parameters.put("description", description);
             
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();
            
            try(BufferedReader br = new BufferedReader(
    		  new InputStreamReader(con.getInputStream(), "utf-8"))) {
    		    StringBuilder response = new StringBuilder();
    		    String responseLine = null;
    		    while ((responseLine = br.readLine()) != null) {
    		        response.append(responseLine.trim());
    		    }
    		    System.out.println(response.toString());
    		    player.sendMessage("§7[§eOreRun§7]§4 "+response.toString());
    		}
            con.disconnect();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
	}
	
	public void reportBug(String server_name, Player player, String description) {
		query_POST(server_name, player, description);
	}
	
	public void reportPlayer(Player owner, Player report, String desc) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.isOp()) {
				player.sendMessage("§7[§eOreRun§7]§4 "+owner.getName()+" a report "+report.getName()+" :"+desc);
			}
		}
	}
}
