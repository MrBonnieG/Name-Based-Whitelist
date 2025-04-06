package org.mrbonnieg.NameBasedWhitelist;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
    private final Main plugin;

    public Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getConfig().getBoolean("plugin-settings.enable")) {
            return;
        }

        String username = event.getPlayer().getName();
        if (!plugin.getStorage().getPlayers().contains(username)) {
            event.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("messages.kick-message")));
        }
    }
}