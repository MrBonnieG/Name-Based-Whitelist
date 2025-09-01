package org.mrbonnieg.namebasedwhitelist;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class MySqlStorage implements Storage {
    private final Main plugin;
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private Connection connection;

    public MySqlStorage(Main plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.host = config.getString("database.host", "localhost:3306");
        this.database = config.getString("database.name", "database");
        this.username = config.getString("database.username", "root");
        this.password = config.getString("database.password", "");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::initConnection);
    }

    private synchronized void initConnection() {
        try {
            if(connection != null && !connection.isClosed()){ return; }
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false&allowPublicKeyRetrieval=true";
            connection = DriverManager.getConnection(url, username, password);
            createTableIfNotExists();
            plugin.getLogger().log(Level.INFO, "MySQL connection established.");
        } catch (SQLException exception) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL database!", exception);
        } catch (ClassNotFoundException exception) {
            plugin.getLogger().log(Level.SEVERE, "MySQL JDBC driver not found!", exception);
        }
    }

    private void createTableIfNotExists() {
        try (Statement statement = getConnection().createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS whitelist (" +
                    "username VARCHAR(255) PRIMARY KEY" +
                    ");";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create whitelist table!", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initConnection();
        }
        return connection;
    }

    @Override
    public List<String> getPlayers() {
        List<String> whitelist = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT username FROM whitelist;")) {
            while (resultSet.next()) {
                whitelist.add(resultSet.getString("username").toLowerCase());
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load whitelist from MySQL!", e);
        }
        return whitelist;
    }

    @Override
    public boolean addPlayer(String username) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement statement = conn.prepareStatement("INSERT INTO whitelist (username) VALUES (?) ON DUPLICATE KEY UPDATE username = username;")) {
                statement.setString(1, username.toLowerCase());
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to add player '" + username.toLowerCase() + "' to MySQL!", e);
            }
        });
        return true;
    }

    @Override
    public boolean removePlayer(String username) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = getConnection();
                 PreparedStatement statement = conn.prepareStatement("DELETE FROM whitelist WHERE username = ?;")) {
                statement.setString(1, username.toLowerCase());
                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to remove player '" + username.toLowerCase() + "' from MySQL!", e);
            }
        });
        return true;
    }

    @Override
    public void saveWhitelist() {}
}
