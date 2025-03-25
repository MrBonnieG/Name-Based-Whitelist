package org.mrbonnieg.NameBasedWhitelist;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    private final Main plugin;

    public Commands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.no-permissions"));
        String playerAdd = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-add"));
        String playerRemove = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-remove"));
        String playerAlreadyExists = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-already-exists"));
        String playerNotFound = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.player-not-found"));
        String pluginReload = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-reload"));
        String pluginEnable = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-enable"));
        String pluginDisable = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-disable"));
        String pluginUsage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.plugin-usage"));

        if (args.length == 0) {
            sender.sendMessage(pluginUsage);
            return true;
        }

        String cmd = args[0].toLowerCase();
        switch (cmd) {
            case "reload":
                if (!sender.hasPermission("namebasedwhitelist.manage") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    plugin.reloadConfig();
                    sender.sendMessage(pluginReload);
                    return true;
                }
            case "add":
                if (!sender.hasPermission("namebasedwhitelist.modify") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    List<String> whitelistedPlayers = plugin.whitelist().getStringList("players");
                    if (whitelistedPlayers.contains(args[1])) {
                        sender.sendMessage(playerAlreadyExists);
                        return true;
                    } else {
                        whitelistedPlayers.add(args[1]);
                        plugin.whitelist().set("players", whitelistedPlayers);
                        saveWhitelist();
                        sender.sendMessage(playerAdd);
                        return true;
                    }
                }
            case "remove":
                if (!sender.hasPermission("namebasedwhitelist.modify") || !sender.hasPermission("namebasedwhitelist.*")) {
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
                        sender.sendMessage(playerRemove);
                        return true;
                    }
                }
            case "enable":
                if (!sender.hasPermission("namebasedwhitelist.manage") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    plugin.config().set("plugin-settings.enable", true);
                    plugin.saveConfig();
                    sender.sendMessage(pluginEnable);
                    return true;
                }
            case "disable":
                if (!sender.hasPermission("namebasedwhitelist.manage") || !sender.hasPermission("namebasedwhitelist.*")) {
                    sender.sendMessage(noPermissions);
                    return true;
                } else {
                    plugin.config().set("plugin-settings.enable", false);
                    plugin.saveConfig();
                    sender.sendMessage(pluginDisable);
                    return true;
                }
            default:
                sender.sendMessage(pluginUsage);
                return true;
        }
    }

    private void saveWhitelist() {
        try {
            plugin.whitelist().save(new File(plugin.getDataFolder(), "whitelist.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save whitelist.yml");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("reload", "add", "remove", "enable", "disable");
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                playerNames.add((player.getName()));
            }
            return playerNames;
        }
        return Lists.newArrayList();
    }
}
