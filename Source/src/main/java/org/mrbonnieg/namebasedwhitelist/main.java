package org.mrbonnieg.namebasedwhitelist;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class main extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private FileConfiguration whitelist;

    @Override
    public void onEnable() {
        // Create plugin directory
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Create config.yml
        File configFile = new File(getDataFolder(),"config.yml");
        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        // Create whitelist.yml
        File whitelistFile = new File(getDataFolder(),"whitelist.yml");
        if(!whitelistFile.exists()) {
            try {
                whitelistFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("Failed to create whitelist.yml");
            }
        }
        whitelist = YamlConfiguration.loadConfiguration(whitelistFile);

        // Register events
        getServer().getPluginManager().registerEvents(this,this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) return Lists.newArrayList("add", "remove", "enable", "disable", "reload");
        if(args.length == 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) return null;
        return Lists.newArrayList();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String noPermissions = config.getString("no-permissions");
        String pluginReloaded = config.getString("plugin-reloaded");
        String pluginEnabled = config.getString("plugin-enabled");
        String pluginDisabled = config.getString("plugin-disabled");
        String playerAdded = config.getString("player-added");
        String playerAlreadyOnTheWhitelist = config.getString("player-already-on-the-whitelist");
        String playerRemoved = config.getString("player-removed");
        String playerNotFound = config.getString("player-not-found");
        String pluginUsage = config.getString("plugin-usage");

        if (command.getName().equalsIgnoreCase("nbwl")) {
            if (args.length == 0) {
                sender.sendMessage(pluginUsage);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) { // Reload command
                if (!sender.hasPermission("namebasedwhitelist.reload") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    reloadConfig();
                    sender.sendMessage(pluginReloaded);
                }
            }


            if (args[0].equalsIgnoreCase("add")) { // Add command
                if (!sender.hasPermission("namebasedwhitelist.add") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    List<String> whitelistedPlayers = whitelist.getStringList("players");
                    if (whitelistedPlayers.contains(args[1])) {
                        sender.sendMessage(playerAlreadyOnTheWhitelist);
                        return true;
                    } else {
                        whitelistedPlayers.add(args[1]);
                        whitelist.set("players", whitelistedPlayers);
                        saveWhitelist();
                        sender.sendMessage(playerAdded);
                    }
                }
            }

            if (args[0].equalsIgnoreCase("remove")) { // Remove command
                if (!sender.hasPermission("namebasedwhitelist.remove") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    List<String> whitelistedPlayers = whitelist.getStringList("players");
                    if (!whitelistedPlayers.contains(args[1])) {
                        sender.sendMessage(playerNotFound);
                        return true;
                    } else {
                        whitelistedPlayers.remove(args[1]);
                        whitelist.set("players", whitelistedPlayers);
                        saveWhitelist();
                        sender.sendMessage(playerRemoved);
                    }
                }
            }

            if (args[0].equalsIgnoreCase("enable")) { // Enable command
                if (!sender.hasPermission("namebasedwhitelist.toggle") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    config.set("enabled", true);
                    saveConfig();
                    sender.sendMessage(pluginEnabled);
                }
            }

            if (args[0].equalsIgnoreCase("disable")) { // Disable command
                if (!sender.hasPermission("namebasedwhitelist.toggle") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    config.set("enabled", false);
                    saveConfig();
                    sender.sendMessage(pluginDisabled);
                }
            }
        }
        return true;
    }



    @EventHandler
    public void onPLayerJoin(PlayerJoinEvent event) {
        // Check plugin enabled
        if(!config.getBoolean("enabled"))
            return;

        // Check Player
        String username = event.getPlayer().getName();
        if(!whitelist.getStringList("players").contains(username)) {
            String kickMessage = config.getString("kick-message");
            event.getPlayer().kickPlayer(kickMessage);
        }
    }

    private void saveWhitelist() {
        try {
            whitelist.save(new File(getDataFolder(), "whitelist.yml"));
        } catch (IOException e) {
            getLogger().severe("Failed to save whitelist.yml");
        }
    }

}
