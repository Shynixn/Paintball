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

    public void backupConfig() {
        Map<String, File> allFiles = new HashMap<String, File>(){{
            for (File file : pb.getDataFolder().listFiles())
                put(file.getName(), file);
        }};
        File oldConfig = allFiles.get("config.yml");
        int suffix = 1;

        while (allFiles.keySet().contains("config_backup" + suffix + ".yml"))
            suffix++;

        oldConfig.renameTo(new File(pb.getDataFolder(), "config_backup" + suffix + ".yml"));
        init(JavaPlugin.getProvidingPlugin(Paintball.class));
    }

    private void init(Plugin pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;

        boolean loadConfig = true;

        for (File file : pb.getDataFolder().listFiles()) {
            if (file.getName().equals("config.yml"))
                loadConfig = false;
        }

        if (loadConfig)
            pb.saveResource("config.yml", false);

        PLAYERDATA = new PlayerData(pb);

        loadEverything();
        ARENA.loadLeaderboards();
    }

    private void loadEverything() {
        ITEMS = new ItemFile(pb);
        ARENA = new ArenaFile(pb);
        ARENA_FILE = ARENA.getFileConfig();
        loadSettings(); // loads everything in config.yml into constants

        CoinItems.getCoinItems().loadItems();
    }

    // Called on server start, reload, and pb admin reload
    private void loadSettings() {
        FileConfiguration config = pb.getConfig();
        PluginDescriptionFile pluginYML = pb.getDescription();

        VERSION                     = pluginYML.getVersion();
        AUTHOR                      = pluginYML.getAuthors().toString();
        PREFIX                      = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        THEME                       = ChatColor.translateAlternateColorCodes('&', config.getString("theme-color"));
        SECONDARY                   = ChatColor.translateAlternateColorCodes('&', config.getString("secondary-color"));
        CONFIG_VERSION              = config.getInt("version");
        SIGN_UPDATE_TIME            = config.getInt("sign-update-time");
        VAULT                       = config.getBoolean("vault");
        TITLE                       = config.getBoolean("title");
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

}