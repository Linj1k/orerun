package fr.kinj14.orerun.functions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.kinj14.orerun.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class F_BungeeCord {
    protected final Main main = Main.getInstance();

    public void sendPlayer(Player player, String servername){
        if(main.F_Config.getBooleanConfig("BungeeCord.Support")){
            if(!player.isValid() || servername.isEmpty()){return;}
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(servername);

            player.sendPluginMessage(main, "BungeeCord", out.toByteArray());
        }
    }

    public void sendPlayersToLobby(){
        //BungeeCord
        for(Player player : Bukkit.getOnlinePlayers()){
            sendPlayer(player, main.F_Config.getStringConfig("BungeeCord.MainHub"));
        }
    }
}
