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

    public static final Map<ChatColor, String> TEAM_NAMES = new HashMap<ChatColor, String>();

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

        for (String colorcode : config.getConfigurationSection("Teams").getKeys(false)) {
            TEAM_NAMES.put(ChatColor.getByChar(colorcode), config.getString("Teams." + colorcode) + "");
        }
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadEverything();

        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            a.loadConfigValues();
        }
    }

    // Adds a new arena to config.yml with values default
    public void addNewConfigSection(Arena a) {
        FileConfiguration config = pb.getConfig();
        List<String> valuesToSet = new ArrayList<String>() {{
            this.add("max-score");
            this.add("time");
            this.add("win-waiting-time");
            this.add("kill-coin-shop");
            this.add("safe-time");
            
            this.add("Join-Arena.give-wool-helmet");
            this.add("Join-Arena.color-player-title");
            this.add("Join-Arena.per-team-chat");

            this.add("Join-Lobby.give-wool-helmet");
            this.add("Join-Lobby.color-player-title");
            this.add("Join-Lobby.give-team-switcher");
            this.add("Join-Lobby.per-team-chat");

            this.add("Rewards.Kill-Coins.per-kill");
            this.add("Rewards.Kill-Coins.per-death");
            this.add("Rewards.Money.per-kill");
            this.add("Rewards.Money.per-death");
            this.add("Rewards.Money.per-win");
            this.add("Rewards.Money.per-defeat");

            this.add("Chat.arena-chat");
            this.add("Chat.broadcast-winner");
            this.add("Chat.spectator-chat");
            this.add("Chat.arena-chat");

            this.add("Countdown.lobby.countdown");
            this.add("Countdown.lobby.interval");
            this.add("Countdown.lobby.no-interval");

            this.add("Countdown.arena.countdown");
            this.add("Countdown.arena.interval");
            this.add("Countdown.arena.no-interval");
        }};

        for (String value : valuesToSet) {
            config.set(a.getConfigPath(value), "default");
        }
        pb.saveConfig();
    }

    public void removeArenaConfigSection(Arena a) {
        pb.getConfig().set("Per-Arena-Settings." + "Arenas." + a.getDefaultName(), null);
        pb.saveConfig();
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