package org.mrbonnieg.namebasedwhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    private FileConfiguration config;
    private Storage storage;

    public FileConfiguration config() { return config; }
    public Storage getStorage() { return storage; }

    @Override
    public void onEnable() {
        saveResource("config.yml", false);
        config = YamlConfiguration.loadConfiguration(new File(getDataFolder(),"config.yml"));
        saveResource("whitelist.yml", false);

        getLogger().log(Level.INFO, "Plugin enabled");

        String storageType = config.getString("settings.storage-type", "yml");
        switch (storageType) {
            case "mysql":
                storage = new MySqlStorage(this);
                getLogger().log(Level.INFO, "Storage type: MySql");
                break;
            default:
                storage = new YamlStorage(this);
                getLogger().log(Level.INFO, "Storage type: Yaml");
                break;
        }

        getServer().getPluginManager().registerEvents(new Events(this), this);
        Commands commands = new Commands(this);
        getServer().getPluginCommand("nbwl").setExecutor(commands);
        getServer().getPluginCommand("nbwl").setTabCompleter(commands);
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Plugin disabled");
    }
}
