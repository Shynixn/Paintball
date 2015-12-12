package me.synapz.paintball.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Set;

public class SQLStatisticsStorage {

    Plugin pl;
    String host;
    Integer port;
    String username;
    String password;
    String database;

    public SQLStatisticsStorage(Plugin pb) {
        this.pl = pb;
    }

    public void setupSQL(String host, Integer port, String username, String password, String database) {
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
        try {
            Connection conn;
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement sql = conn.prepareStatement("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String base64Stats = result.getString("stats");
            String yamlString = Base64.getDecoder().decode(base64Stats.getBytes()).toString();
            YamlConfiguration statsYaml = new YamlConfiguration();
            statsYaml.loadFromString(yamlString);
            Set<String> keys = statsYaml.getConfigurationSection("Player-Data").getKeys(false);
            for (String key : keys) {
                ConfigurationSection stats = statsYaml.getConfigurationSection(key + ".Stats");
                String path = stats.getCurrentPath();
                yaml.set(path, stats);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return yaml;
    }

    public void executeQuery(String query) {
        Connection conn;
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
        try {
            conn = DriverManager.getConnection(host, username, password);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
