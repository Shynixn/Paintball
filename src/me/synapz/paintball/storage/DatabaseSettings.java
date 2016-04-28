package me.synapz.paintball.storage;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Databases;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Jeremy(Refrigerbater) on 4/28/2016.
 */
public class DatabaseSettings {

    protected boolean sql = false;
    protected boolean mysql = true;
    protected boolean bungee = false;
    protected HashMap<UUID, Arena> bungeePlayers = new HashMap<>();
    protected String host;
    protected String username;
    protected String password;
    protected String database;
    protected String SID = "Generate";
    protected String BID;

    private FileConfiguration fileConfig;

    protected DatabaseSettings(FileConfiguration fileConfig) {
        this.fileConfig = fileConfig;

        sql = loadBoolean(Databases.SQL_ENABLED);
        mysql = loadBoolean(Databases.MY_SQL);
        host = loadString(Databases.HOST);
        username = loadString(Databases.USERNAME);
        password = loadString(Databases.PASSWORD);
        database = loadString(Databases.DATABASE);

        bungee = loadBoolean(Databases.BUNGEE_ENABLED);
    }

    protected String loadString(Databases type) {
        if (isFoundInConfig(type))
            return (String) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultString());

        return type.getDefaultString();
    }

    protected boolean loadBoolean(Databases type) {
        if (isFoundInConfig(type))
            return (boolean) loadValue(type);
        else
            fileConfig.set(type.getPath(), type.getDefaultBoolean());

        return type.getDefaultBoolean();
    }

    protected Object loadValue(Databases type) {
        return fileConfig.get(type.getPath());
    }

    protected boolean isFoundInConfig(Databases type) {
        Object value = fileConfig.get(type.getPath());

        return value != null;
    }
}
