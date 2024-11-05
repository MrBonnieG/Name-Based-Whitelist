package org.mrbonnieg.NameBasedWhitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Events implements Listener {
    private final Main plugin;
    public Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPLayerJoin(PlayerJoinEvent event) {
        if(!plugin.config().getBoolean("enabled"))
            return;

        String username = event.getPlayer().getName();
        if (!plugin.whitelist().getStringList("players").contains(username)) {
            String kickMessage = plugin.config().getString("kick-message");
            event.getPlayer().kickPlayer(kickMessage);
        }
    }
}
