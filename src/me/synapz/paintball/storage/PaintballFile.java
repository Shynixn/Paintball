package me.synapz.paintball.storage;


import me.synapz.paintball.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Set;

public abstract class PaintballFile {

    private final FileConfiguration fileConfig;
    private final File file;
    private final String name;
    /**
     * SQL Stuffs
     **/

    Boolean sql = false;
    String host;
    Integer port;
    String username;
    String password;
    String database;
    public PaintballFile(Plugin pb, String name) {
        this.name = name;

        this.file = new File(pb.getDataFolder(), name);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }
        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
        this.saveFile();
    }

    public void saveFile() {
        try {
            if (this.sql && this.file.getName().contains("playerdata")) {
                this.removeStats(this.fileConfig).save(this.file);
            }
            this.fileConfig.save(this.file);
        } catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save " + this.name + ".", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public FileConfiguration getFileConfig() {
        if (this.sql && this.file.getName().contains("playerdata")) {
            return this.addStats(this.fileConfig);
        }
        return this.fileConfig;
    }

    public void setupSQL(String host, Integer port, String username, String password, String database) {
        this.sql = true;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        executeQuery("CREATE DATABASE IF NOT EXISTS " + database);
        executeQuery("CREATE TABLE IF NOT EXISTS Paintball_Stats (id INTEGER not null,stats STRING,PRIMARY KEY (id))");
    }

    public FileConfiguration removeStats(FileConfiguration yaml) {
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

    public FileConfiguration addStats(FileConfiguration yaml) {
        YamlConfiguration statsYaml = new YamlConfiguration();
        try {
            Connection conn;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            statsYaml.loadFromString(yamlString);
        } catch (Exception e) {
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

    public void executeQuery(String query) {
        Connection conn;
        try {
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
