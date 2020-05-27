package fr.kinj14.orerun.functions;

import fr.kinj14.orerun.Main;
import fr.kinj14.orerun.utils.QueryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class F_Config {
    protected final Main main = Main.getInstance();
    protected FileConfiguration cfg;

    public F_Config(){
        cfg = main.getConfig();
    }

    public String getStringConfig(String StringName){
        return cfg.getString(StringName);
    }

    public int getIntConfig(String IntName){
        return cfg.getInt(IntName);
    }

    public Boolean getBooleanConfig(String BooleanName){
        return cfg.getBoolean(BooleanName);
    }

    public Location getLocationConfig(String WorldName, String LocationName){
        return deserializeLocation(WorldName, cfg.getString(LocationName));
    }

    public ConfigurationSection getConfigurationSection(String ConfigurationSectionName){
        return cfg.getConfigurationSection(ConfigurationSectionName);
    }

    //(de)serialize

    public Location deserializeConfigLocation(String World_Name, String configname){
        String[] split = getStringConfig(configname).split(", ");
        return new Location(
                Bukkit.getWorld(World_Name),
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
        );
    }

    public Location deserializeLocation(String World_Name, String name){
        String[] split = name.split(", ");
        return new Location(
                Bukkit.getWorld(World_Name),
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
        );
    }

    public static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + ", " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch();
    }

    public Boolean checkForUpdate(){
        String version = main.version;
        Map<String, String> parameters = new HashMap<>();
        String query = new QueryUtils().query_POST("https://orerun.ovh/app/php/checkforupdate.php", parameters);
        return !version.equalsIgnoreCase(query);
    }
}
