package me.synapz.paintball.storage;


import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.coin.CoinItems;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    // Constants
    public static int SIGN_UPDATE_TIME;
    public static int CONFIG_VERSION;

    public static String PREFIX;
    public static String VERSION;
    public static String THEME;
    public static String AUTHOR;
    public static String SECONDARY;

    public static boolean VAULT;
    public static boolean TITLE;
    public static boolean HOLOGRAPHIC_DISPLAYS;

    public static Economy ECONOMY = null;

    public static ArenaFile ARENA;
    public static PlayerData PLAYERDATA;
    public static FileConfiguration ARENA_FILE;
    public static ItemFile ITEMS;
    public static Database DATABASE;

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
        loadFromJar("database.yml");

        PLAYERDATA = new PlayerData(pb);

        loadEverything();
        ARENA.loadLeaderboards();
    }

    private void loadEverything() {
        ITEMS = new ItemFile(pb);
        ARENA = new ArenaFile(pb);
        DATABASE = new Database(pb);
        ARENA_FILE = ARENA.getFileConfig();
        loadSettings(); // loads everything in config.yml into constants

        CoinItems.getCoinItems().loadItems();
    }

    // Called on server start, reload, and pb admin reload
    private void loadSettings() {
        PluginDescriptionFile pluginYML = pb.getDescription();
        FileConfiguration config = pb.getConfig();

        VERSION                     = pluginYML.getVersion();
        AUTHOR                      = pluginYML.getAuthors().toString();
        PREFIX                      = ChatColor.translateAlternateColorCodes('&', loadString(config, "prefix"));
        THEME                       = ChatColor.translateAlternateColorCodes('&', loadString(config, "theme-color"));
        SECONDARY                   = ChatColor.translateAlternateColorCodes('&', loadString(config, "secondary-color"));
        SIGN_UPDATE_TIME            = loadInt(config, "sign-update-time");
        VAULT                       = loadBoolean(config, "vault");
        TITLE                       = loadBoolean(config, "title");
        HOLOGRAPHIC_DISPLAYS        = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
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
        Map<String, File> allFiles = new HashMap<String, File>(){{
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

    private int loadInt(FileConfiguration file, String path) {
        return (int) loadValue(file, path);
    }

    private String loadString(FileConfiguration file, String path) {
        return (String) loadValue(file, path);
    }

    private boolean loadBoolean(FileConfiguration file, String path) {
        return (boolean) loadValue(file, path);
    }

    private Object loadValue(FileConfiguration file, String path) {
        Object value = file.get(path);

        // If this value is null, it was not found, so turn this file to config_backup.yml and load another updated one
        if (value == null) {
            Settings.getSettings().backupConfig("config");
            return null;
        }

        // After backup and new one is done, get the value
        return value;
    }
}