package me.synapz.paintball.storage.database;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connects to and uses a SQLite database
 *
 * @author tips48
 */
public class SQLiteManager extends Database {
    private final String dbLocation;
    private Connection connection;

    public SQLiteManager(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    @Override
    public void openConnection() throws SQLException{
        if (connection != null && !connection.isClosed()) return;

        File dataFolder = new File("sqlite-db/");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File file = new File(dataFolder, dbLocation);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Unable to create database!");
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to connect to database!");
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/"+ dbLocation);
    }

    @Override
    public void init() throws SQLException {

    }

    @Override
    public void closeConnection() throws SQLException {

        if (connection != null && !connection.isClosed()) connection.close();
    }

    @Override
    public FileConfiguration buildConfig() throws SQLException {

        FileConfiguration config = null;
        return config;
    }

    @Override
    public void updateTable(FileConfiguration config) throws SQLException {

    }

    @Override
    protected void setupDatabase() throws SQLException {

    }

    @Override
    protected  void setupTable() throws SQLException {

    }

    @Override
    protected void attemptDataTransfer() throws SQLException {

    }
}