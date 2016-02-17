package me.synapz.paintball.storage;


import me.synapz.paintball.*;
import me.synapz.paintball.killcoin.KillCoinItemHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {

    // Constants
    public static int SIGN_UPDATE_TIME;
    public static int CONFIG_VERSION;
    public static int PORT;

    public static String PREFIX;
    public static String VERSION;
    public static String THEME;
    public static String WEBSITE;
    public static String AUTHOR;
    public static String SECONDARY;
    public static String USERNAME;
    public static String PASSWORD;
    public static String DATABASE;
    public static String SERVER_ID;
    public static String HOST;

    public static boolean TITLE_API;
    public static boolean VAULT;
    public static boolean SQL;
    public static boolean BUNGEE_CORD;

    public static ArenaFile ARENA;
    public static PlayerData PLAYERDATA;
    public static FileConfiguration ARENA_FILE;

    // Variables
    private static Settings instance;
    private Plugin pb;

    public Settings(Plugin plugin) {
        init(plugin); // init all config.yml stuff
        instance = this; // inject the instance
        this.pb = plugin; // set the plugin variable
        Settings.ARENA.setup(); // setup arena.yml
    }

    public static Settings getSettings() {
        return instance;
    }

    private void init(Plugin pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;

        // TODO: always says error on reload/start
        pb.saveResource("config.yml", false);

        ARENA = new ArenaFile(pb);
        PLAYERDATA = new PlayerData(pb);
        ARENA_FILE = ARENA.getFileConfig();

        loadEverything();
    }

    private void loadEverything() {
        loadSettings(); // loads everything in config.yml into constants
        loadSQL(); // loads sql if it is enabled
        KillCoinItemHandler.getHandler().loadItemsFromConfig(pb.getConfig()); // loads all killcoin items
        loadBungee(); // loads bungee support if enabled
    }

    // Called on server start, reload, and pb admin reload
    private void loadSettings() {
        FileConfiguration config = pb.getConfig();
        PluginDescriptionFile pluginYML = pb.getDescription();

        VERSION                     = pluginYML.getVersion();
        WEBSITE                     = pluginYML.getWebsite();
        AUTHOR                      = pluginYML.getAuthors().toString();
        PREFIX                      = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        THEME                       = ChatColor.translateAlternateColorCodes('&', config.getString("theme-color"));
        SECONDARY                   = ChatColor.translateAlternateColorCodes('&', config.getString("secondary-color"));
        CONFIG_VERSION              = config.getInt("version");
        SIGN_UPDATE_TIME            = config.getInt("sign-update-time");
        TITLE_API                   = config.getBoolean("Options.title-api") && Bukkit.getPluginManager().getPlugin("TitleAPI") != null;
        VAULT                       = config.getBoolean("Options.vault");
        SQL                         = config.getBoolean("Options.SQL");
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadEverything();

        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            a.loadConfigValues();
        }
    }

    public FileConfiguration getConfig() {
        return pb.getConfig();
    }

    private void loadBungee() {
        if (pb.getConfig().getBoolean("BungeeCord")) {
            BUNGEE_CORD = true;
            SERVER_ID = pb.getConfig().getString("ServerID");
        } else {
            BUNGEE_CORD = false;
        }
    }

    private void loadSQL() {
        FileConfiguration config = pb.getConfig();
        SQL = config.getBoolean("SQL");
        if (SQL) {
            HOST = config.getString("SQL-Settings.host");
            PORT = config.getInt("SQL-Settings.port");

            USERNAME = config.getString("SQL-Settings.user");
            PASSWORD = config.getString("SQL-Settings.pass");
            DATABASE = config.getString("SQL-Settings.database");
            setupSQL();
        }
    }

    //TODO: Add check for if SQL has been recently disabled and reinsert the stats
    private void setupSQL() {
        if (!SQL) {
            return;
        }
        Utils.executeQuery("CREATE DATABASE IF NOT EXISTS " + DATABASE);
        Utils.executeQuery("CREATE TABLE IF NOT EXISTS Paintball_Stats (id INTEGER not null,stats LONGTEXT,PRIMARY KEY (id))");
        try {
            Connection conn;
            conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
            if (conn == null) {
                Bukkit.getLogger().severe("SQL Details are incorrect or incomplete, Please update them and try again!");
                Settings.SQL = false;
                return;
            }
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
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Failed to download SQL backup!");
        }
    }
}