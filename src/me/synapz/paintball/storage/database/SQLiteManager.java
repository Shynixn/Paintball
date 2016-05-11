package me.synapz.paintball.storage.database;

import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager extends MySQLManager {
    private final String dbLocation;
    private Connection connection;

    public SQLiteManager(String dbLocation) {
        super();
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
                Messenger.error(Bukkit.getConsoleSender(), "Unable to create database!");
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Unable to connect to database!");
        }

        connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/"+ dbLocation);
    }
}