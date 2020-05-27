package fr.kinj14.orerun.enums;

import fr.kinj14.orerun.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public enum OreRun_Files {
    CONFIG("config.yml"),
    LANG("lang.yml");

    private final String fileName;
    private final File dataFolder;

    protected final Main main = Main.getInstance();

    OreRun_Files(String fileName){
        this.fileName = fileName;
        this.dataFolder = main.getDataFolder();
    }

    public File getFile(){
        return new File(dataFolder, fileName);
    }

    public FileConfiguration getConfig(){
        return YamlConfiguration.loadConfiguration(getFile());
    }

    public void save(FileConfiguration config){
        try {
            config.save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return this.fileName;
    }

    public void create(){
        if(fileName == null || fileName.isEmpty()){
            throw new IllegalArgumentException("ResourcePath cannot be null or empty!");
        }

        InputStream in = main.getResource(fileName);
        if(in == null){
            throw new IllegalArgumentException("The resource '"+fileName+"' cannot be found in plugin jar!");
        }

        if(!dataFolder.exists() && !dataFolder.mkdir()){
            main.logger.severe("Failed to make plugin directory!");
        }

        File outFile = getFile();
        try{
            if(!outFile.exists()){
                main.logger.info("The "+fileName+" was not found, creation in progress ...");

                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int n;

                while((n = in.read(buf)) > 0){
                    out.write(buf, 0, n);
                }

                out.close();
                in.close();

                if(!outFile.exists()){
                    main.logger.severe("Unable to copy file !");
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
