package fr.kinj14.orerun.listeners;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.enums.GameState;
import fr.kinj14.orerun.enums.OreRun_Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPing implements Listener {
    protected final Main main = Main.getInstance();
    private final int slots = main.getTM().GetMaxPlayersTeam();

    @EventHandler
    public void onServerList(ServerListPingEvent e){
        e.setMaxPlayers(slots);

        if(main.isState(GameState.WAITING)){
            e.setMotd(OreRun_Lang.SERVER_MOTD_LOBBY.get());
        } else {
            e.setMotd(OreRun_Lang.SERVER_MOTD_GAME.get());
        }
    }
}
