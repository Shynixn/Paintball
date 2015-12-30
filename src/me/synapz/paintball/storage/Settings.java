package me.synapz.paintball.storage;


import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Utils;
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
    public static int MAX_SCORE, ARENA_TIME, ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, LOBBY_COUNTDOWN, LOBBY_INTERVAL, LOBBY_NO_INTERVAL, SIGN_UPDATE_TIME;
    public static String ARENA_CHAT, SPEC_CHAT;
    public static String PREFIX, VERSION, THEME, WEBSITE, AUTHOR, SECONDARY;
    public static boolean BROADCAST_WINNER, COLOR_PLAYER_TITLE, WOOL_HELMET, TITLE_API;
    public static Map<ChatColor, String> TEAM_NAMES = new HashMap<ChatColor, String>();
    // SQL and Bungee Stuff
    // TODO: make these final
    public static boolean BungeeCord;
    public static String ServerID;
    public static boolean SQL;
    public static String HOST;
    public static int PORT;
    public static String USERNAME;
    public static String PASSWORD;
    public static String DATABASE;
    // Variables
    private static Settings settings;
    private Plugin pb;
    private FileConfiguration arena;
    private PlayerData cache;
    private File aFile;

    public Settings(Plugin plugin) {
        if (settings == null) {
            settings = this;
            init(plugin);
        }
        this.pb = plugin;
    }

    public static Settings getSettings() {
        if (settings == null) {
            settings = new Settings(Paintball.getProvidingPlugin(Paintball.class));
            settings.pb = Paintball.getProvidingPlugin(Paintball.class);
            settings.init(settings.pb);
        }
        return settings;
    }

    public void init(Plugin pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;

        // TODO: always says error on reload/start
            pb.saveResource("config.yml", false);

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

        loadValues(pb.getConfig(), pb.getDescription());
        loadSQL();
        loadBungee();
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadValues(pb.getConfig(), pb.getDescription());
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

    private void loadValues(FileConfiguration configFile, PluginDescriptionFile pluginYML) {
        // regular values
        VERSION = pluginYML.getVersion();
        WEBSITE = pluginYML.getWebsite();
        AUTHOR = pluginYML.getAuthors().toString();
        PREFIX = ChatColor.translateAlternateColorCodes('&', configFile.getString("prefix"));
        THEME = ChatColor.translateAlternateColorCodes('&', configFile.getString("theme-color"));
        SECONDARY = ChatColor.translateAlternateColorCodes('&', configFile.getString("secondary-color"));
        SIGN_UPDATE_TIME = configFile.getInt("sign-update-time");

        // arena values
        COLOR_PLAYER_TITLE = configFile.getBoolean("color-player-title");
        WOOL_HELMET = configFile.getBoolean("give-wool-helmet");
        LOBBY_COUNTDOWN = configFile.getInt("countdown.lobby.countdown");
        LOBBY_INTERVAL = configFile.getInt("countdown.lobby.interval");
        LOBBY_NO_INTERVAL = configFile.getInt("countdown.lobby.no-interval");
        ARENA_COUNTDOWN = configFile.getInt("countdown.arena.countdown");
        ARENA_INTERVAL = configFile.getInt("countdown.arena.interval");
        ARENA_NO_INTERVAL = configFile.getInt("countdown.arena.no-interval");
        ARENA_TIME = configFile.getInt("Arena-Settings.Defaults.time");
        MAX_SCORE = configFile.getInt("Arena-Settings.Defaults.max-score");
        BROADCAST_WINNER = configFile.getBoolean("Chat.broadcast-winner");
        TITLE_API = configFile.getBoolean("Options.title-api") && Bukkit.getPluginManager().getPlugin("TitleAPI") != null;

        for (String colorcode : configFile.getConfigurationSection("Teams").getKeys(false)) {
            TEAM_NAMES.put(ChatColor.getByChar(colorcode), configFile.getString("Teams." + colorcode) + "");
        }

        SPEC_CHAT = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.spectator-chat"));
        ARENA_CHAT = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.arena-chat"));
        loadSQL();
    }

    public FileConfiguration getConfig() {
        return this.pb.getConfig();
    }

    private void loadBungee() {
        if (pb.getConfig().getBoolean("BungeeCord")) {
            Settings.BungeeCord = true;
            Settings.ServerID = pb.getConfig().getString("ServerID");
        } else {
            Settings.BungeeCord = false;
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