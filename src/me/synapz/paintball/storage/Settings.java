package me.synapz.paintball.storage;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.coin.CoinItems;
import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.database.Database;
import me.synapz.paintball.storage.database.MySQLManager;
import me.synapz.paintball.storage.database.SQLiteManager;
import me.synapz.paintball.storage.files.*;
import me.synapz.paintball.utils.Messenger;
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

public class Settings {

    public static String WEBSITE;
    public static String VERSION;
    public static String THEME;
    public static String AUTHOR;
    public static String SECONDARY;

    public static boolean UPDATE_CHECK;
    public static boolean VAULT;
    public static boolean HOLOGRAPHIC_DISPLAYS;

    public static Economy ECONOMY = null;

    public static ArenaFile ARENA;
    public static PlayerDataFile PLAYERDATA;
    public static MessagesFile MESSAGES;
    public static FileConfiguration ARENA_FILE;
    public static ItemFile ITEMS;
    public static LogsFile LOGS;
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
        loadSettings(); // loads everything in config.yml into constants
        MESSAGES = new MessagesFile(pb);
        PLAYERDATA = new PlayerDataFile(pb);
        DATABASE_FILE = new DatabaseFile(pb);
        ITEMS = new ItemFile(pb);
        CoinItems.getCoinItems().loadItems();
        ARENA = new ArenaFile(pb);
        ARENA_FILE = ARENA.getFileConfig();
        ScoreboardLine.loadScoreboardLines();
        StatType.loadStatNames();

        // Tries to connect to database if SQL is enabled. If not, attempts to load previous data from the database and
        // transfers to playerdata.yml if the table exists, then drops the table.
        DATABASE = Databases.MY_SQL.getBoolean() ? new MySQLManager() : new SQLiteManager("database.db");
        try {
            DATABASE.openConnection();
            DATABASE.init();
            if (Databases.SQL_ENABLED.getBoolean()) {
                if (PLAYERDATA.exists() && DATABASE.doesTableExist()) {
                    DATABASE.addStats(PLAYERDATA.getFileConfig());
                }
                else {
                    DATABASE.updateTable(PLAYERDATA.getFileConfig());
                }
                PLAYERDATA.setFileConfig(DATABASE.buildConfig());
                PLAYERDATA.delete();
            }
        } catch (SQLException e) {
            if (Databases.SQL_ENABLED.getBoolean()) {
                Messenger.error(Bukkit.getConsoleSender(), "Could not initialize database connection!");
                Databases.SQL_ENABLED.setBoolean(false);
                DATABASE_FILE.saveFile();
            }
        }
    }

    // Called on server start, reload, and pb admin reload
    private void loadSettings() {
        PluginDescriptionFile pluginYML = pb.getDescription();

        WEBSITE = pluginYML.getWebsite();
        VERSION = pluginYML.getVersion();
        AUTHOR = pluginYML.getAuthors().toString();
        THEME = ChatColor.translateAlternateColorCodes('&', loadString("theme-color"));
        SECONDARY = ChatColor.translateAlternateColorCodes('&', loadString("secondary-color"));
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

    public PlayerDataFile getPlayerData() {
        return PLAYERDATA;
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
