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
    public static int ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, LOBBY_COUNTDOWN, LOBBY_INTERVAL, LOBBY_NO_INTERVAL;
    public static String ARENA_CHAT, SPEC_CHAT;
    public static String PREFIX, VERSION, THEME, WEBSITE, AUTHOR, SECONDARY;
    public static boolean COLOR_PLAYER_TITLE, WOOL_HELMET, TITLE_API;
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
        if (Settings.settings == null) {
            Settings.settings = this;
            this.init(plugin);
        }
        pb = plugin;
    }

    public static Settings getSettings() {
        if (Settings.settings == null) {
            Settings.settings = new Settings(Paintball.getProvidingPlugin(Paintball.class));
            Settings.settings.pb = Paintball.getProvidingPlugin(Paintball.class);
            Settings.settings.init(Settings.settings.pb);
        }
        return Settings.settings;
    }

    public void init(Plugin pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;

        // TODO: always says error on reload/start
        if (!(new File(pb.getDataFolder(), "config.yml").exists())) {
            pb.saveResource("config.yml", false);
        }

        this.aFile = new File(pb.getDataFolder(), "arenas.yml");

        if (!this.aFile.exists()) {
            try {
                this.aFile.createNewFile();
            } catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create arenas.yml. Stack trace: ");
                e.printStackTrace();
            }
        }


        this.arena = YamlConfiguration.loadConfiguration(this.aFile);
        this.cache = new PlayerData(pb);

        this.loadValues(pb.getConfig(), pb.getDescription());
        this.loadSQL();
        this.loadBungee();
    }

    public void reloadConfig() {
        this.pb.reloadConfig();
        this.loadValues(this.pb.getConfig(), this.pb.getDescription());
        this.arena = YamlConfiguration.loadConfiguration(this.aFile);
    }

    public void saveArenaFile() {
        try {
            this.arena.save(this.aFile);
        } catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public PlayerData getCache() {
        return this.cache;
    }

    // Adds a new arena to config.yml with values default
    public void addNewConfigSection(Arena a) {
        FileConfiguration config = pb.getConfig();
        List<String> valuesToSet = new ArrayList<String>() {{
            add("kills-to-win");
            add("time-to-win");
            add("Join-Arena.open-kit-menu");
            add("Join-Arena.give-wool-helmet");
            add("Join-Lobby.give-wool-helmet");
            add("Join-Lobby.color-player-title");
            add("Rewards.Kill-Coins.per-kill");
            add("Rewards.Kill-Coins.per-death");
            add("Rewards.Money.per-kill");
            add("Rewards.Money.per-death");
            add("Rewards.Money.per-win");
            add("Rewards.Money.per-defeat");
        }};

        for (String value : valuesToSet) {
            config.set("Arena-Settings." + "Arenas." + a.getDefaultName() + "." + value, "default");
        }
        pb.saveConfig();
    }

    public void removeArenaConfigSection(Arena a) {
        pb.getConfig().set("Arena-Settings." + "Arenas." + a.getDefaultName(), null);
        pb.saveConfig();
    }
    private void loadValues(FileConfiguration configFile, PluginDescriptionFile pluginYML) {
        // regular values
        Settings.VERSION = pluginYML.getVersion();
        Settings.WEBSITE = pluginYML.getWebsite();
        Settings.AUTHOR = pluginYML.getAuthors().toString();
        Settings.PREFIX = ChatColor.translateAlternateColorCodes('&', configFile.getString("prefix"));
        Settings.THEME = ChatColor.translateAlternateColorCodes('&', configFile.getString("theme-color"));
        Settings.SECONDARY = ChatColor.translateAlternateColorCodes('&', configFile.getString("secondary-color"));

        // arena values
        Settings.COLOR_PLAYER_TITLE = configFile.getBoolean("color-player-title");
        Settings.WOOL_HELMET = configFile.getBoolean("give-wool-helmet");
        Settings.LOBBY_COUNTDOWN = configFile.getInt("countdown.lobby.countdown");
        Settings.LOBBY_INTERVAL = configFile.getInt("countdown.lobby.interval");
        Settings.LOBBY_NO_INTERVAL = configFile.getInt("countdown.lobby.no-interval");
        Settings.ARENA_COUNTDOWN = configFile.getInt("countdown.arena.countdown");
        Settings.ARENA_INTERVAL = configFile.getInt("countdown.arena.interval");
        Settings.ARENA_NO_INTERVAL = configFile.getInt("countdown.arena.no-interval");
        Settings.TITLE_API = configFile.getBoolean("title-api") && Bukkit.getPluginManager().getPlugin("TitleAPI") != null;

        for (String colorcode : configFile.getConfigurationSection("Teams").getKeys(false)) {
            Settings.TEAM_NAMES.put(ChatColor.getByChar(colorcode), configFile.getString("Teams." + colorcode) + "");
        }

        Settings.SPEC_CHAT = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.spectator-chat"));
        Settings.ARENA_CHAT = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.arena-chat"));
        this.loadSQL();
    }

    private void loadBungee() {
        if (this.pb.getConfig().getBoolean("BungeeCord")) {
            BungeeCord = true;
            ServerID = this.pb.getConfig().getString("ServerID");
        } else {
            BungeeCord = false;
        }
    }

    private void loadSQL() {
        FileConfiguration config = this.pb.getConfig();
        Settings.SQL = config.getBoolean("SQL");
        if (Settings.SQL) {
            Settings.HOST = config.getString("SQL-Settings.host");
            Settings.PORT = config.getInt("SQL-Settings.port");
            Settings.USERNAME = config.getString("SQL-Settings.user");
            Settings.PASSWORD = config.getString("SQL-Settings.pass");
            Settings.DATABASE = config.getString("SQL-Settings.database");
            this.setupSQL();
        }
    }

    //TODO: Add check for if SQL has been recently disabled and reinsert the stats
    private void setupSQL() {
        if (!Settings.SQL) {
            return;
        }
        Utils.executeQuery("CREATE DATABASE IF NOT EXISTS " + Settings.DATABASE);
        Utils.executeQuery("CREATE TABLE IF NOT EXISTS Paintball_Stats (id INTEGER not null,stats STRING,PRIMARY KEY (id))");
        try {
            Connection conn;
            conn = DriverManager.getConnection(Settings.HOST, Settings.USERNAME, Settings.PASSWORD);
            PreparedStatement sql = conn.prepareStatement("SELECT statsFROM `Paintball_Stats` WHERE id = '1';");
            ResultSet result = sql.executeQuery();
            result.next();
            String encoded = result.getString("stats");
            File file = new File(this.pb.getDataFolder(), "playerdata.yml");
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
        return this.arena;
    }
}