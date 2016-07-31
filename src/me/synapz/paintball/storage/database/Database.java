package me.synapz.paintball.storage.database;

import me.synapz.paintball.enums.Databases;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

/**
 * Created by Jeremy on 4/29/2016.
 */
public abstract class Database {

    protected String host;
    protected String database;
    protected int port;
    protected String username;
    protected String password;
    protected String statsTable;

    public Database() {
        this.host = Databases.HOST.getString();
        this.database = Databases.DATABASE.getString();
        this.port = Databases.PORT.getInteger();
        this.username = Databases.USERNAME.getString();
        this.password = Databases.PASSWORD.getString();
        this.statsTable = Databases.TABLE.getString();
    }

    public abstract void openConnection() throws SQLException;

    public abstract void closeConnection() throws SQLException;

    public abstract void updateTable(FileConfiguration config) throws SQLException;

    public abstract FileConfiguration buildConfig(String uuid) throws  SQLException;

    public abstract void addStats(FileConfiguration config) throws SQLException;

    public abstract boolean doesTableExist() throws SQLException;

    protected abstract void setupDatabase() throws SQLException;

    protected abstract void setupTable() throws SQLException;

    protected abstract void attemptDataTransfer() throws SQLException;

    public void init() throws SQLException {
        if (Databases.ENABLED.getBoolean()) {
            setupDatabase();
            setupTable();
        }
        else {
            attemptDataTransfer();
        }
    }
}
