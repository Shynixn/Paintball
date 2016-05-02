package me.synapz.paintball.storage.database;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.enums.Databases;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Set;

/**
 * Created by Jeremy(Refrigerbater) on 4/28/2016.
 */
public class MySQLManager extends Database{

    private Connection connection;

    public MySQLManager() {

        super();
    }

    @Override
    public void openConnection() throws SQLException {

            synchronized (this) {
                try {
                if (connection != null && !connection.isClosed()) return;

                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                } catch (ClassNotFoundException e) {
                    System.out.println("[Paintball] Could not connect to the MySQL database.");
                }
            }
    }

    @Override
    public void closeConnection() throws SQLException {

        if (connection != null && !connection.isClosed()) connection.close();
    }

    @Override
    public void updateTable(FileConfiguration config) throws SQLException {
        openConnection();
        if (!config.contains("Player-Data")) return;
        Set<String> keys = config.getConfigurationSection("Player-Data").getKeys(false);
        for (String uuid : keys) {
            ConfigurationSection section = config.getConfigurationSection("Player-Data." + uuid);
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + statsTable + " VALUES('" + uuid + "'" +
                    "," + section.getInt("Kills") + "," + section.getInt("Deaths") + "," + section.getInt("Shots") +
                    "," + section.getInt("Hits") + "," + section.getInt("Highest-Kill-Streak") + "," + section.getInt("Games-Played") +
                    "," + section.getInt("Wins") + "," + section.getInt("Defeats") + "," + section.getInt("Ties") +
                    "," + section.getInt("Flags-Captured") + "," + section.getInt("Flags-Dropped") + "," + section.getInt("Time-Played") +
                    ") ON DUPLICATE KEY UPDATE kills = " + section.getInt("Kills") + ",deaths = " + section.getInt("Deaths") +
                    ",shots = " + section.getInt("Shots") + ",hits = " + section.getInt("Hits") + ",highest_kill_streak = " +
                    section.getInt("Highest-Kill-Streak") + ",games_played = " + section.getInt("Games-Played") + ",wins = " +
                    section.getInt("Wins") + ",defeats = " + section.getInt("Defeats") + ",ties = " + section.getInt("Ties") +
                    ",flags_captured = " + section.getInt("Flags-Captured") + ",flags_dropped = " + section.getInt("Flags-Dropped") +
                    ",time_played = " + section.getInt("Time-Played") + ";");
            statement.executeUpdate();
        }
    }

    @Override
    public FileConfiguration buildConfig() throws SQLException {
        openConnection();
        FileConfiguration config = new YamlConfiguration();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + statsTable + ";");
        ResultSet set = statement.executeQuery();
        while (set.next()) {
            String uuid = set.getString("uuid");
            int kills = set.getInt("kills");
            int deaths = set.getInt("deaths");
            int shots = set.getInt("shots");
            int hits = set.getInt("hits");
            int streak = set.getInt("highest_kill_streak");
            int games = set.getInt("games_played");
            int wins = set.getInt("wins");
            int defeats = set.getInt("defeats");
            int ties = set.getInt("ties");
            int flag_cap = set.getInt("flags_captured");
            int flag_drop = set.getInt("flags_dropped");
            int time_played = set.getInt("time_played");

            config.set("Player-Data." + uuid + ".Kills", kills);
            config.set("Player-Data." + uuid + ".Deaths", deaths);
            config.set("Player-Data." + uuid + ".Shots", shots);
            config.set("Player-Data." + uuid + ".Hits", hits);
            config.set("Player-Data." + uuid + ".Highest-Kill-Streak", streak);
            config.set("Player-Data." + uuid + ".Games-Played", games);
            config.set("Player-Data." + uuid + ".Wins", wins);
            config.set("Player-Data." + uuid + ".Defeats", defeats);
            config.set("Player-Data." + uuid + ".Ties", ties);
            config.set("Player-Data." + uuid + ".Flags-Captured", flag_cap);
            config.set("Player-Data." + uuid + ".Flags-Dropped", flag_drop);
            config.set("Player-Data." + uuid + ".Time-Played", time_played);
        }
        return config;
    }

    @Override
    protected void setupDatabase() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
        statement.executeUpdate();
    }

    @Override
    protected void setupTable() throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + statsTable +
                " (uuid VARCHAR(48) NOT NULL,kills INT NOT NULL,deaths INT NOT NULL,shots INT NOT NULL,hits INT NOT NULL," +
                "highest_kill_streak INT NOT NULL,games_played INT NOT NULL,wins INT NOT NULL,defeats INT NOT NULL," +
                "ties INT NOT NULL,flags_captured INT NOT NULL,flags_dropped INT NOT NULL,time_played INT NOT NULL,PRIMARY KEY (uuid));");
        statement.executeUpdate();
    }

    @Override
    protected void attemptDataTransfer() throws SQLException {
        openConnection();

        PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE '" + statsTable + "';");
        ResultSet set = statement.executeQuery();
        if (!set.next()) return;

        FileConfiguration config = buildConfig();
        File playerData = new File(Paintball.getInstance().getDataFolder(), "playerdata.yml");
        try {
            if (!playerData.exists()) {
                playerData.createNewFile();
                config.save(playerData);
            }

            PreparedStatement drop = connection.prepareStatement("DROP TABLE " + statsTable);
            drop.executeUpdate();
        } catch (IOException e) {
            System.out.println("[Paintball] Failed to load data from MySQL database!");
            e.printStackTrace();
        }
    }
}
