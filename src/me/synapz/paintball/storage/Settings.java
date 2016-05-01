package me.synapz.paintball.storage;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.coin.CoinItems;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.storage.database.Database;
import me.synapz.paintball.storage.database.MySQLManager;
import me.synapz.paintball.storage.database.SQLiteManager;
import me.synapz.paintball.storage.files.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jeremy on 4/29/2016.
 */
public class Settings {

    public static String WEBSITE;
    public static String PREFIX;
    public static String VERSION;
    public static String THEME;
    public static String AUTHOR;
    public static String SECONDARY;

    public static boolean UPDATE_CHECK;
    public static boolean VAULT;
    public static boolean TITLE;
    public static boolean HOLOGRAPHIC_DISPLAYS;

    public static Economy ECONOMY = null;

    public static ArenaFile ARENA;
    public static PlayerDataFile PLAYERDATA;
    public static MessagesFile MESSAGES;
    public static FileConfiguration ARENA_FILE;
    public static ItemFile ITEMS;
    public static Database DATABASE;
    public static DatabaseFile DATABASE_FILE;

    // Variables
    private static Settings instance;
    private Plugin pb;

    public Settings(Plugin plugin) {
        init(plugin); // init all config.yml stuff
        Settings.ARENA.setup(); // setup arena.yml
    }

    public static Settings getSettings() {
        return instance;
    }

    private void init(Plugin pb) {
        instance = this; // inject the instance

        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;

        loadFromJar("config.yml");
        loadEverything();

        ARENA.loadLeaderboards();
    }

    private void loadEverything() {
        PLAYERDATA = new PlayerDataFile(pb);
        DATABASE_FILE = new DatabaseFile(pb);
        ITEMS = new ItemFile(pb);
        ARENA = new ArenaFile(pb);
        MESSAGES = new MessagesFile(pb);
        ARENA_FILE = ARENA.getFileConfig();

        // Tries to connect to database if SQL is enabled. If not, attempts to load previous data from the database and
        // transfers to playerdata.yml if the table exists, then drops the table.
        DATABASE = Databases.MY_SQL.getBoolean() ? new MySQLManager() : new SQLiteManager("database.db");
        try {
            DATABASE.openConnection();
            DATABASE.init();
            PLAYERDATA.setFileConfig(DATABASE.buildConfig());
        } catch (SQLException e) {
            System.out.println("[Paintball] Could not initialize database connection!");
            e.printStackTrace();
        }

        loadSettings(); // loads everything in config.yml into constants
        CoinItems.getCoinItems().loadItems();
    }

    // Called on server start, reload, and pb admin reload
    private void loadSettings() {
        PluginDescriptionFile pluginYML = pb.getDescription();

        WEBSITE = pluginYML.getWebsite();
        VERSION = pluginYML.getVersion();
        AUTHOR = pluginYML.getAuthors().toString();
        PREFIX = ChatColor.translateAlternateColorCodes('&', loadString("prefix"));
        THEME = ChatColor.translateAlternateColorCodes('&', loadString("theme-color"));
        SECONDARY = ChatColor.translateAlternateColorCodes('&', loadString("secondary-color"));
        VAULT = loadBoolean("vault");
        TITLE = loadBoolean("title");
        UPDATE_CHECK = loadBoolean("update-check");
        HOLOGRAPHIC_DISPLAYS = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadEverything();
        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            if (a.getState() == Arena.ArenaState.WAITING || a.getState() == Arena.ArenaState.DISABLED || a.getState() == Arena.ArenaState.NOT_SETUP)
                a.loadConfigValues();
            else
                a.setReload();
        }
    }

    public FileConfiguration getConfig() {
        return pb.getConfig();
    }

    private void loadFromJar(String name) {
        boolean loadConfig = true;

        for (File file : pb.getDataFolder().listFiles()) {
            if (file.getName().equals(name))
                loadConfig = false;
        }

        if (loadConfig)
            pb.saveResource(name, false);
    }

    public void backupConfig(String name) {
        Map<String, File> allFiles = new HashMap<String, File>() {{
            for (File file : pb.getDataFolder().listFiles())
                put(file.getName(), file);
        }};
        File oldConfig = allFiles.get(name + ".yml");
        int suffix = 1;

        while (allFiles.keySet().contains(name + "_backup" + suffix + ".yml"))
            suffix++;

        oldConfig.renameTo(new File(pb.getDataFolder(), name + "_backup" + suffix + ".yml"));
        init(JavaPlugin.getProvidingPlugin(Paintball.class));
    }

    private int loadInt(String path) {
        return (int) loadValue("config.yml", path);
    }

    private String loadString(String path) {
        return (String) loadValue("config.yml", path);
    }

    private boolean loadBoolean(String path) {
        return (boolean) loadValue("config.yml", path);
    }

    private Object loadValue(String name, String path) {

        Map<String, File> allFiles = new HashMap<String, File>() {{
            for (File file : JavaPlugin.getProvidingPlugin(Paintball.class).getDataFolder().listFiles())
                put(file.getName(), file);
        }};

        boolean notFoundInConfig = YamlConfiguration.loadConfiguration(allFiles.get(name)).get(path) == null;

        // If this value is null, it was not found, so turn this file to config_backup.yml and load another updated one
        if (notFoundInConfig) {
            Settings.getSettings().backupConfig("config");
            return null;
        }

        Object value = YamlConfiguration.loadConfiguration(allFiles.get(name)).get(path);

        // After backup and new one is done, get the value
        return value;
    }
}