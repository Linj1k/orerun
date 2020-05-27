package fr.kinj14.orerun.enums;

import fr.kinj14.orerun.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;

import java.util.HashMap;
import java.util.Map;

public enum OreRun_Lang {
    PLUGIN_INITIALIZATION,
    PLUGIN_DISABLE,
    SERVER_MOTD_LOBBY,
    SERVER_MOTD_GAME,
    WORLD_DESTROYOLDWORLD,
    WORLD_UNLOADOLDWORLD,
    WORLD_GENERATENEWWORLD,
    GAMESTATE_START,
    GAMESTATE_START_GAMESTARTMSG,
    GAMESTATE_START_ENOUGHPLAYERS,
    GAMESTATE_START_TEAMCOUNT,
    GAMESTATE_START_LAUNCHINGGAME,
    GAMESTATE_START_TELEPORTING,
    GAMESTATE_GAMECYCLE,
    GAMESTATE_GAMECYCLE_BORDER,
    GAMESTATE_GAMECYCLE_PVPACTIVATE,
    GAMESTATE_GAMECYCLE_PVPDESACTIVATE,
    GAMESTATE_GAMECYCLE_NOFALLACTIVATE,
    GAMESTATE_GAMECYCLE_NOFALLDESACTIVATE,
    GAMESTATE_GAMECYCLE_INSTANTINGOTACTIVATE,
    GAMESTATE_GAMECYCLE_INSTANTINGOTDESACTIVATE,
    GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEACTIVATE,
    GAMESTATE_GAMECYCLE_INSTANTINGOTFORTUNEDESACTIVATE,
    GAMESTATE_GAMECYCLE_FRIENDLYFIREACTIVATE,
    GAMESTATE_GAMECYCLE_FRIENDLYFIREDESACTIVATE,
    GAMESTATE_GAMECYCLE_HEALTHREGENACTIVATE,
    GAMESTATE_GAMECYCLE_HEALTHREGENDESACTIVATE,
    GAMESTATE_GAMECYCLE_GOODLUCKMSG,
    GAMESTATE_FINISH,
    SCOREBOARD_PLAYERS,
    SCOREBOARD_BORDER,
    SCOREBOARD_STATS,
    SCOREBOARD_POINTS,
    SCOREBOARD_LIFE,
    TEAMS_TEAMISFULL,
    TEAMS_JOINMSG,
    TEAMS_LEAVEMSG,
    PLAYER_JOINMSG,
    PLAYER_QUITMSG,
    PLAYER_GAMEISSTARTEDMSG,
    PLAYER_CANCHANGETEAMMSG,
    PLAYER_REPORTMSG,
    DAMAGE_KILLEDBY,
    DAMAGE_PVPWAIT,
    DAMAGE_FRIENDLYFIRE,
    ITEMS_SETUP,
    ITEMS_LOBBY,
    ITEMS_JOINTEAM,
    WIN_EQUALITY,
    WIN_LOBBY,
    WIN_WON,
    WIN_LOSEMSG,
    CHAT_GLOBAL,
    CHAT_TEAM,
    COMMANDS_REPORT_INVALIDREASON,
    COMMANDS_REPORT_INVALIDPLAYER,
    COMMANDS_CANCHANGETEAM_ACTIVATED,
    COMMANDS_CANCHANGETEAM_DEACTIVATED,
    COMMANDS_RESTARTGAME,
    COMMANDS_MINPLAYERS,
    COMMANDS_INVALIDEMINPLAYERS,
    OTHERS_DEPOSITCHEST,
    OTHERS_TEAMLOADED;

    private static final Map<OreRun_Lang, String> VALUES = new HashMap<>();

    static{
        for (OreRun_Lang lang : values()){
            VALUES.put(lang, lang.getFromFile());
        }

        Main.getInstance().logger.info("Lang file read successfully!");
    }

    public String get(){
        return VALUES.get(this);
    }

    private String getFromFile(){
        FileConfiguration config = OreRun_Files.LANG.getConfig();
        String key = name().toLowerCase().replace('_','-');
        String value = config.getString(key);

        if(value == null){
            value = "";
        }

        return ChatColor.translateAlternateColorCodes('&', value);
    }
}
