package me.synapz.paintball.storage;


import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Utils;
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
    public static int MAX_SCORE;
    public static int ARENA_TIME;
    public static int ARENA_COUNTDOWN;
    public static int ARENA_INTERVAL;
    public static int ARENA_NO_INTERVAL;
    public static int LOBBY_COUNTDOWN;
    public static int LOBBY_INTERVAL;
    public static int LOBBY_NO_INTERVAL;
    public static int SIGN_UPDATE_TIME;
    public static int CONFIG_VERSION;
    public static int PORT;
    public static int ROUND_TIME;
    public static int WIN_WAIT_TIME;
    public static int KILLCOIN_PER_KILL;
    public static int KILLCOIN_PER_DEATH;
    public static int MONEY_PER_KILL;
    public static int MONEY_PER_DEATH;
    public static int MONEY_PER_WIN;
    public static int MONEY_PER_DEFEAT;

    public static String ARENA_CHAT;
    public static String SPEC_CHAT;
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

    public static boolean BROADCAST_WINNER;
    public static boolean COLOR_PLAYER_TITLE_ARENA;
    public static boolean TITLE_API;
    public static boolean VAULT;
    public static boolean SQL;
    public static boolean BUNGEE_CORD;
    public static boolean USE_ARENA_CHAT;
    public static boolean PER_TEAM_CHAT; // TODO: os doubled in config
    public static boolean OPEN_KILLCOIN_MENU; // TODO: rename in config
    public static boolean GIVE_WOOL_HELMET_ARENA;
    public static boolean GIVE_WOOL_HELMET_LOBBY;
    public static boolean COLOR_PLAYER_TITLE_LOBBY;
    public static boolean GIVE_TEAM_SWITCHER;

    public static final Map<ChatColor, String> TEAM_NAMES = new HashMap<ChatColor, String>();

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
        LOBBY_COUNTDOWN             = config.getInt("countdown.lobby.countdown");
        LOBBY_INTERVAL              = config.getInt("countdown.lobby.interval");
        LOBBY_NO_INTERVAL           = config.getInt("countdown.lobby.no-interval");
        ARENA_COUNTDOWN             = config.getInt("countdown.arena.countdown");
        ARENA_INTERVAL              = config.getInt("countdown.arena.interval");
        ARENA_NO_INTERVAL           = config.getInt("countdown.arena.no-interval");
        MAX_SCORE                   = config.getInt("Arena-Settings.Defaults.max-score");
        ROUND_TIME                  = config.getInt("Arena-Settings.Defaults.time");
        WIN_WAIT_TIME               = config.getInt("Arena-Settings.Defaults.win-waiting-time");
        PER_TEAM_CHAT               = config.getBoolean("Arena-Settings.Defaults.per-team-chat");
        OPEN_KILLCOIN_MENU          = config.getBoolean("Arena-Settings.Defaults."); // TODO: remove this option; no kits
        GIVE_WOOL_HELMET_ARENA      = config.getBoolean("Arena-Settings.Defaults.Join-Arena.give-wool-helmet");
        COLOR_PLAYER_TITLE_ARENA    = config.getBoolean("Arena-Settings.Defaults.Join-Arena.color-player-title");
        GIVE_WOOL_HELMET_LOBBY      = config.getBoolean("Arena-Settings.Defaults.Join-Lobby.give-wool-helmet");
        COLOR_PLAYER_TITLE_LOBBY    = config.getBoolean("Arena-Settings.Defaults.Join-Lobby.color-player-title");
        GIVE_TEAM_SWITCHER          = config.getBoolean("Arena-Settings.Defaults.Join-Lobby.give-team-switcher");
        KILLCOIN_PER_KILL           = config.getInt("Arena-Settings.Rewards.Kill-Coins.per-kill");
        KILLCOIN_PER_DEATH          = config.getInt("Arena-Settings.Rewards.Kill-Coins.per-death");
        MONEY_PER_KILL              = config.getInt("Arena-Settings.Rewards.Money.per-kill");
        MONEY_PER_DEATH             = config.getInt("Arena-Settings.Rewards.Money.per-death");
        MONEY_PER_WIN               = config.getInt("Arena-Settings.Rewards.Money.per-win");
        MONEY_PER_DEFEAT            = config.getInt("Arena-Settings.Rewards.Money.per-defeat");
        USE_ARENA_CHAT              = config.getBoolean("Arena-Settings.Chat.arena-chat");
        BROADCAST_WINNER            = config.getBoolean("Arena-Settings.Chat.broadcast-winner");
        SPEC_CHAT = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.spectator-chat"));
        ARENA_CHAT = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.arena-chat"));

        for (String colorcode : config.getConfigurationSection("Teams").getKeys(false)) {
            TEAM_NAMES.put(ChatColor.getByChar(colorcode), config.getString("Teams." + colorcode) + "");
        }
    }

    // Variables
    private static Settings instance;
    private Plugin pb;
    private FileConfiguration arena;
    private PlayerData cache;
    private File aFile;

    public Settings(Plugin plugin) {
        init(plugin);
        instance = this;
        this.pb = plugin;
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

        // TODO: should be removed and replaced with ArenaFile.java
        aFile = new File(pb.getDataFolder(), "arenas.yml");

        if (!aFile.exists()) {
            try {
                aFile.createNewFile();
            } catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create arenas.yml. Stack trace: ");
                e.printStackTrace();
            }
        }


        arena = YamlConfiguration.loadConfiguration(aFile);
        cache = new PlayerData(pb);

        loadEverything();
    }

    private void loadEverything() {
        loadSettings();
        loadSQL();
        KillCoinItemHandler.getHandler().loadItemsFromConfig(pb.getConfig());
        loadSQL();
        loadBungee();
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadEverything();
        arena = YamlConfiguration.loadConfiguration(aFile);
    }

    public void saveArenaFile() {
        try {
            arena.save(aFile);
        } catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public PlayerData getCache() {
        return cache;
    }

    // Adds a new arena to config.yml with values default
    public void addNewConfigSection(Arena a) {
        FileConfiguration config = this.pb.getConfig();
        List<String> valuesToSet = new ArrayList<String>() {{
            this.add("kills-to-win");
            this.add("time-to-win");
            this.add("Join-Arena.open-kit-menu");
            this.add("Join-Arena.give-wool-helmet");
            this.add("Join-Lobby.give-wool-helmet");
            this.add("Join-Lobby.color-player-title");
            this.add("Rewards.Kill-Coins.per-kill");
            this.add("Rewards.Kill-Coins.per-death");
            this.add("Rewards.Money.per-kill");
            this.add("Rewards.Money.per-death");
            this.add("Rewards.Money.per-win");
            this.add("Rewards.Money.per-defeat");
        }};

        for (String value : valuesToSet) {
            config.set("Arena-Settings." + "Arenas." + a.getDefaultName() + "." + value, "default");
        }
        this.pb.saveConfig();
    }

    public void removeArenaConfigSection(Arena a) {
        this.pb.getConfig().set("Arena-Settings." + "Arenas." + a.getDefaultName(), null);
        this.pb.saveConfig();
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
    // TODO: make a new ArenaFile class
    public FileConfiguration getArenaFile() {
        return arena;
    }
}