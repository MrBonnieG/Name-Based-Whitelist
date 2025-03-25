package org.mrbonnieg.NameBasedWhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {
    private FileConfiguration config;
    private FileConfiguration whitelist;

    public FileConfiguration config() {
        return config;
    }

    public FileConfiguration whitelist() {
        return whitelist;
    }

    public void onEnable() {
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(),"config.yml"));
        saveResource("whitelist.yml", false);
        whitelist = YamlConfiguration.loadConfiguration(new File(getDataFolder(),"whitelist.yml"));
        getLogger().log(Level.INFO, "Plugin enabled");

        getServer().getPluginManager().registerEvents(new Events(this), this);
        Commands commands = new Commands(this);
        getServer().getPluginCommand("nbwl").setExecutor(commands);
        getServer().getPluginCommand("nbwl").setTabCompleter(commands);
    }

    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin disabled");
    }
}
