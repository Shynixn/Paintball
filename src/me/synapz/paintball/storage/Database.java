package me.synapz.paintball.storage;

import me.synapz.paintball.enums.Databases;
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
import java.util.Random;
import java.util.Set;

public class Database extends PaintballFile {

    private Plugin pb = null;
    private Boolean bungee = false;
    private String host = null;
    private Integer port = null;
    private String username = null;
    private String password = null;
    private String database = null;
    private boolean sql = false;

    public Database(Plugin pb) {
        super(pb, "database.yml");

        sql = loadBoolean(Databases.SQL_ENABLED);
        host = loadString(Databases.HOST);
        port = loadInt(Databases.PORT);
        username = loadString(Databases.USERNAME);
        password = loadString(Databases.PASSWORD);
        database = loadString(Databases.DATABASE);

        bungee = loadBoolean(Databases.BUNGEE_ENABLED);

        if (loadString(Databases.SERVER_ID).equalsIgnoreCase("Generate")) {
            Random r = new Random(5);
            String base10ServerID = r.doubles(1073741824).toString();
            String serverID = Base64.getEncoder().encodeToString(base10ServerID.getBytes());
            setValue("Bungee.serverID", serverID);
            //run a method to start the listening for bungee commands
        }
    }

    public Boolean isBungee() {
        return bungee;
    }

    public Boolean isSQL() {
        return sql;
    }

    /*
    If any of the following are null (not set) this will set the file with the default value
    and return the default value.
     */
    private int loadInt(Databases type) {
        if (isFoundInConfig(type))
            return (int) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultInt());

        saveFile();

        return type.getDefaultInt();
    }

    private String loadString(Databases type) {
        if (isFoundInConfig(type))
            return (String) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultString());

        saveFile();

        return type.getDefaultString();
    }

    private boolean loadBoolean(Databases type) {
        if (isFoundInConfig(type))
            return (boolean) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultBoolean());

        saveFile();

        return type.getDefaultBoolean();
    }

    private Object loadValue(Databases type) {
        return fileConfig.get(type.getPath());
    }

    private boolean isFoundInConfig(Databases type) {
        Object value = fileConfig.get(type.getPath());

        return value != null;
    }

    private void setValue(String path, Object object) {
        fileConfig.set(path, object);
    }

    //TODO: Work on SQL stuff down here
    public void setupSQL(Plugin pb, String host, Integer port, String username, String password, String database) {
        this.sql = true;
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
