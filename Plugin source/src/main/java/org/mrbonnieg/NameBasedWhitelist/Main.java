package org.mrbonnieg.NameBasedWhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Main extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private FileConfiguration whitelist;

    public FileConfiguration config() {
        return config;
    }

    public FileConfiguration whitelist() {
        return whitelist;
    }

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(),"config.yml");
        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        File whitelistFile = new File(getDataFolder(),"whitelist.yml");
        if(!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Failed to create whitelist.yml");
            }
        }
        whitelist = YamlConfiguration.loadConfiguration(whitelistFile);

        getServer().getPluginManager().registerEvents(new Events(this), this);
        Commands commands = new Commands(this);
        getServer().getPluginCommand("nbwl").setExecutor(commands);
        getServer().getPluginCommand("nbwl").setTabCompleter(commands);
    }
}
