package me.synapz.paintball.storage.database;

import me.synapz.paintball.storage.files.UUIDStatsFile;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.*;
import java.util.UUID;

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

    /**
     * This will save each file's data at once.
     * @param config
     * @throws SQLException
     */
    @Override
    public void updateTable(FileConfiguration config) throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO " + statsTable + " VALUES('" + config.getString("UUID") + "'" +
                ",'" + config.getString("Username") + "'," + config.getInt("Kills") + "," + config.getInt("Deaths") + "," + config.getInt("Shots") +
                "," + config.getInt("Hits") + "," + config.getInt("Highest-Kill-Streak") + "," + config.getInt("Games-Played") +
                "," + config.getInt("Wins") + "," + config.getInt("Defeats") + "," + config.getInt("Ties") +
                "," + config.getInt("Flags-Captured") + "," + config.getInt("Flags-Dropped") + "," + config.getInt("Time-Played") +
                ") ON DUPLICATE KEY UPDATE" + " username = '" + config.getString("Username") + "',kills = " + config.getInt("Kills") + ",deaths = " + config.getInt("Deaths") +
                ",shots = " + config.getInt("Shots") + ",hits = " + config.getInt("Hits") + ",highest_kill_streak = " +
                config.getInt("Highest-Kill-Streak") + ",games_played = " + config.getInt("Games-Played") + ",wins = " +
                config.getInt("Wins") + ",defeats = " + config.getInt("Defeats") + ",ties = " + config.getInt("Ties") +
                ",flags_captured = " + config.getInt("Flags-Captured") + ",flags_dropped = " + config.getInt("Flags-Dropped") +
                ",time_played = " + config.getInt("Time-Played") + ";");
        statement.executeUpdate();
    }

    @Override
    public FileConfiguration buildConfig(String uuid) throws SQLException {
        openConnection();
        FileConfiguration config = new YamlConfiguration();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + statsTable
                + " WHERE (uuid = '" + uuid + "';");
        ResultSet set = statement.executeQuery();
        if (!set.next()) return config;
        String username = set.getString("username");
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

        config.set("UUID", uuid);
        config.set("Username", username);
        config.set("Kills", kills);
        config.set("Deaths", deaths);
        config.set("Shots", shots);
        config.set("Hits", hits);
        config.set("Highest-Kill-Streak", streak);
        config.set("Games-Played", games);
        config.set("Wins", wins);
        config.set("Defeats", defeats);
        config.set("Ties", ties);
        config.set("Flags-Captured", flag_cap);
        config.set("PFlags-Dropped", flag_drop);
        config.set("Time-Played", time_played);
        return config;
    }

    /**
     * This will save each file's data at once.
     * @param config
     * @throws SQLException
     */
    @Override
    public void addStats(FileConfiguration config) throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("UPDATE " + statsTable + " SET kills = kills + " +
                config.getInt("Kills") + ",deaths = deaths + " + config.getInt("Deaths") +
                ",shots = shots + " + config.getInt("Shots") + ",hits = hits + " + config.getInt("Hits") +
                ",highest_kill_streak = highest_kill_streak + " + config.getInt("Highest-Kill-Streak") +
                ",games_played = games_played + " + config.getInt("Games-Played") + ",wins = wins + " +
                config.getInt("Wins") + ",defeats = defeats + " + config.getInt("Defeats") + ",ties = ties + " +
                config.getInt("Ties") + ",flags_captured = flags_captured + " + config.getInt("Flags-Captured") +
                ",flags_dropped = flags_dropped + " + config.getInt("Flags-Dropped") + ",time_played = time_played + " +
                config.getInt("Time-Played") + " WHERE (uuid = '" + config.getString("UUID") + "');");
        statement.executeUpdate();
    }

    @Override
    public boolean doesTableExist() throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE '" + statsTable + "';");
        ResultSet set = statement.executeQuery();
        return set.next();
    }

    @Override
    protected void setupDatabase() throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database);
        statement.executeUpdate();
    }

    @Override
    protected void setupTable() throws SQLException {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + statsTable +
                " (uuid VARCHAR(48) NOT NULL,username VARCHAR(16) NOT NULL,kills INT NOT NULL,deaths INT NOT NULL,shots INT NOT NULL,hits INT NOT NULL," +
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

        statement = connection.prepareStatement("SELECT uuid FROM " + statsTable + ";");
        set = statement.executeQuery();
        while (set.next()) {
            FileConfiguration config = buildConfig(set.getString("uuid"));
            UUIDStatsFile uuidStatsFile = new UUIDStatsFile(UUID.fromString(set.getString("uuid")));
        }
        PreparedStatement drop = connection.prepareStatement("DROP TABLE " + statsTable);
        drop.executeUpdate();
    }
}
