package org.mrbonnieg.NameBasedWhitelist;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    private final Main plugin;
    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String noPermissions = plugin.config().getString("no-permissions");
        String pluginReloaded = plugin.config().getString("plugin-reloaded");
        String pluginEnabled = plugin.config().getString("plugin-enabled");
        String pluginDisabled = plugin.config().getString("plugin-disabled");
        String playerAdded = plugin.config().getString("player-added");
        String playerAlreadyOnTheWhitelist = plugin.config().getString("player-already-on-the-whitelist");
        String playerRemoved = plugin.config().getString("player-removed");
        String playerNotFound = plugin.config().getString("player-not-found");
        String pluginUsage = plugin.config().getString("plugin-usage");

        if (args.length == 0) {
            sender.sendMessage(pluginUsage);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) { // Reload command
            if (!sender.hasPermission("namebasedwhitelist.reload") || !sender.hasPermission("namebasedwhitelist.*")) {
                sender.sendMessage(noPermissions);
                return true;
            } else {
                plugin.reloadConfig();
                sender.sendMessage(pluginReloaded);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("add")) { // Add command
            if (!sender.hasPermission("namebasedwhitelist.add") || !sender.hasPermission("namebasedwhitelist.*")) {
                sender.sendMessage(noPermissions);
                return true;
            } else {
                List<String> whitelistedPlayers = plugin.whitelist().getStringList("players");
                if (whitelistedPlayers.contains(args[1])) {
                    sender.sendMessage(playerAlreadyOnTheWhitelist);
                    return true;
                } else {
                    whitelistedPlayers.add(args[1]);
                    plugin.whitelist().set("players", whitelistedPlayers);
                    saveWhitelist();
                    sender.sendMessage(playerAdded);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("remove")) { // Remove command
            if (!sender.hasPermission("namebasedwhitelist.remove") || !sender.hasPermission("namebasedwhitelist.*")) {
                sender.sendMessage(noPermissions);
                return true;
            } else {
                List<String> whitelistedPlayers = plugin.whitelist().getStringList("players");
                if (!whitelistedPlayers.contains(args[1])) {
                    sender.sendMessage(playerNotFound);
                    return true;
                } else {
                    whitelistedPlayers.remove(args[1]);
                    plugin.whitelist().set("players", whitelistedPlayers);
                    saveWhitelist();
                    sender.sendMessage(playerRemoved);
                    return true;
                }
            }
        }

        if (args[0].equalsIgnoreCase("enable")) { // Enable command
            if (!sender.hasPermission("namebasedwhitelist.toggle") || !sender.hasPermission("namebasedwhitelist.*")) {
                sender.sendMessage(noPermissions);
                return true;
            } else {
                plugin.config().set("enabled", true);
                plugin.saveConfig();
                sender.sendMessage(pluginEnabled);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("disable")) { // Disable command
            if (!sender.hasPermission("namebasedwhitelist.toggle") || !sender.hasPermission("namebasedwhitelist.*")) {
                sender.sendMessage(noPermissions);
                return true;
            } else {
                plugin.config().set("enabled", false);
                plugin.saveConfig();
                sender.sendMessage(pluginDisabled);
                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 1) return Lists.newArrayList("add", "remove", "enable", "disable", "reload");
        if(args.length == 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) return null;
        return Lists.newArrayList();
    }

    private void saveWhitelist() {
        try {
            plugin.whitelist().save(new File(plugin.getDataFolder(), "whitelist.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save whitelist.yml");
        }
    }
}
