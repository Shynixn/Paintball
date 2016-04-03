package me.synapz.paintball.storage;

import org.bukkit.plugin.Plugin;

public class Database extends PaintballFile {

    private String password = null;

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
}
