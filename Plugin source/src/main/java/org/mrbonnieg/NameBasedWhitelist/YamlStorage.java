package org.mrbonnieg.namebasedwhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class YamlStorage implements Storage {
    private final Main plugin;
    private final FileConfiguration whitelist;

    public YamlStorage(Main plugin){
        this.plugin = plugin;
        whitelist = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(),"whitelist.yml"));
    }

    @Override
    public List<String> getPlayers() {
        return whitelist.getStringList("players").stream().map(String::toLowerCase).toList();
    }

    @Override
    public boolean addPlayer(String username){
        List<String> whitelistedPlayers = getPlayers();
        if(whitelistedPlayers.contains(username.toLowerCase())) return false;
        whitelistedPlayers.add(username.toLowerCase());
        whitelist.set("players", whitelistedPlayers);
        saveWhitelist();
        return true;
    }

    @Override
    public boolean removePlayer(String username){
        List<String> whitelistedPlayers = getPlayers();
        if(!whitelistedPlayers.contains(username.toLowerCase())) return false;
        whitelistedPlayers.remove(username.toLowerCase());
        whitelist.set("players", whitelistedPlayers);
        saveWhitelist();
        return true;
    }

    @Override
    public void saveWhitelist() {
        try {
            whitelist.save(new File(plugin.getDataFolder(), "whitelist.yml"));
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to save whitelist.yml");
        }

    }
}
