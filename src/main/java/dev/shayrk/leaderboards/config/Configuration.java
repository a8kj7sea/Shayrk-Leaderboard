package dev.shayrk.leaderboards.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Configuration {
    private File file;
    private FileConfiguration configurationFile;

    public Configuration(JavaPlugin Pluginparam, String child, boolean saveDefaultData) {
        file = new File(Pluginparam.getDataFolder(), child);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            if (saveDefaultData) {
                Pluginparam.saveResource(child, saveDefaultData);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException ioexception) {
                    ioexception.printStackTrace();
                }
            }
        }
        configurationFile = YamlConfiguration.loadConfiguration((File) file);
    }

    public void save() {
        try {
            configurationFile.save(file);
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    public void reload() {
        configurationFile = YamlConfiguration.loadConfiguration((File) file);
    }

    public FileConfiguration getConfigurationFile() {
        return configurationFile;
    }

}
