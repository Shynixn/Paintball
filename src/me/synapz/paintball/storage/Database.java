package me.synapz.paintball.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import java.util.Set;

public class Database extends PaintballFile {

    private Plugin pb = null;
    private Boolean bungee = false;
    private Boolean SQL = false;
    private String host = null;
    private Integer port = null;
    private String username = null;
    private String password = null;
    private String database = null;
    // TODO: Put other values

    public Database(Plugin pb) {
        super(pb, "database.yml");


        // TODO: Load all values through loadString, loadInt, or loadBoolean, which checks to make sure the values are not null
        password = loadString("SQL.password");
    }

    public String getPassword() {
        return password;
    }

    private int loadInt(String path) {
        return (int) loadValue(path);
    }

    private String loadString(String path) {
        return (String) loadValue(path);
    }

    private boolean loadBoolean(String path) {
        return (boolean) loadValue(path);
    }

    private Object loadValue(String path) {
        Object value = fileConfig.get(path);

        // If this value is null, it was not found, so turn this file to database_backup.yml and load another updated one
        if (value == null) {
            Settings.getSettings().backupConfig("database");
            return null;
        }

        // After backup and new one is done, get the value
        return value;
    }

    //TODO: Work on SQL stuff down here
    public void setupSQL(Plugin pb, String host, Integer port, String username, String password, String database) {
        this.SQL = true;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.pb = pb;
        executeQuery("CREATE DATABASE IF NOT EXISTS " + database);
        executeQuery("CREATE TABLE IF NOT EXISTS Paintball_Stats (id INTEGER not null,stats STRING,PRIMARY KEY (id))");
        try {
            Connection conn;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String encoded = result.getString("stats");
            File file = new File(pb.getDataFolder(), "playerdata.yml");
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            yaml.set("Stats", encoded);
            yaml.save(file);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("Failed to download SQL backup!");
        }
    }

    private FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            ResultSet result = executeQuery("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (InvalidConfigurationException | SQLException e) {
            e.printStackTrace();
            Bukkit.getLogger().info("SQL connection failed! Using offline backup until we can connect again");
            if (yaml.contains("Stats")) {
                String base64Stats = yaml.getString("Stats");
                String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
                try {
                    statsYaml.loadFromString(yamlString);
                } catch (InvalidConfigurationException e1) {
                    e1.printStackTrace();
                    Bukkit.getLogger().severe("Failed to load offline config! Please check SQL connection and playerdata file!");
                }
            } else {
                Bukkit.getLogger().severe("Statistics Down!! We have no SQL connection and don't have a backup of stats!");
            }
        }

        Set<String> keys = statsYaml.getConfigurationSection("Player-Data").getKeys(false);
        for (String key : keys) {
            ConfigurationSection stats = statsYaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            yaml.set(path, stats);
        }
        return yaml;
    }

    private FileConfiguration removeStats(FileConfiguration yaml) {
        Set<String> keys = yaml.getConfigurationSection("Player-Data").getKeys(false);
        YamlConfiguration statsYaml = new YamlConfiguration();
        for (String key : keys) {
            ConfigurationSection stats = yaml.getConfigurationSection(key + ".Stats");
            String path = stats.getCurrentPath();
            statsYaml.set(path, stats);
            yaml.set(path, null);
        }
        byte[] byteArray = statsYaml.saveToString().getBytes();
        String encoded = Base64.getEncoder().encode(byteArray).toString();
        yaml.set("Stats", encoded);
        executeQuery("INSERT INTO Paintball_Stats (id,stats) VALUES (1," + encoded + ")");
        return yaml;
    }

    private ResultSet executeQuery(String query) {
        Connection conn;
        try {
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement(query);
            ResultSet result = sql.executeQuery();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
