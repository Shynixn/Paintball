package me.synapz.paintball.storage;


import me.synapz.paintball.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;

public class Settings {
    
    private static Settings settings = new Settings();

    private Paintball pb;
    private FileConfiguration arena;
    private CacheFile cache;
    private File aFile;

    public static int COUNTDOWN, INTERVAL, NO_INTERVAL;
    private String prefix, version, theme, website, author, secondary;
    public static String ARENA_CHAT, SPEC_CHAT;
    public static boolean SPLASH_PAINTBALLS, COLOR_PLAYER_TITLE, WOOL_HELMET, DEBUG;

    private Settings() {}

    public static Settings getSettings() {
        return settings;
    }

    public void init(Paintball pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;
        pb.saveResource("config.yml", false);

        aFile = new File(pb.getDataFolder(), "arenas.yml");

        if (!aFile.exists()) {
            try {
                aFile.createNewFile();
            }
            catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "", "Could not create arenas.yml. Stack trace: ");
                e.printStackTrace();
            }
        }


        arena = YamlConfiguration.loadConfiguration(aFile);
        cache = new CacheFile(pb);

        loadValues(pb.getConfig(), pb.getDescription());
    }

    public void reloadConfig() {
        pb.reloadConfig();
        loadValues(pb.getConfig(), pb.getDescription());
        arena = YamlConfiguration.loadConfiguration(aFile);
    }

    public void saveArenaFile() {
        try {
            arena.save(aFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    public CacheFile getCache() {
        return cache;
    }

    private void loadValues(FileConfiguration configFile, PluginDescriptionFile pluginYML) {
        // regular values
        version     = pluginYML.getVersion();
        website     = pluginYML.getWebsite();
        author      = pluginYML.getAuthors().toString();
        prefix      = ChatColor.translateAlternateColorCodes('&', configFile.getString("prefix"));
        theme       = ChatColor.translateAlternateColorCodes('&', configFile.getString("theme-color"));
        secondary   = ChatColor.translateAlternateColorCodes('&', configFile.getString("secondary-color"));

        // arena values
        SPLASH_PAINTBALLS   = configFile.getBoolean("paintball-splash");
        COLOR_PLAYER_TITLE  = configFile.getBoolean("color-player-title");
        WOOL_HELMET         = configFile.getBoolean("give-wool-helmet");
        DEBUG               = configFile.getBoolean("debug-messages");
        COUNTDOWN           = configFile.getInt("countdown.time");
        INTERVAL            = configFile.getInt("countdown.interval");
        NO_INTERVAL         = configFile.getInt("countdown.no-interval");

        // TODO: add stats tag to config.yml chts
        SPEC_CHAT   = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.spectator-chat"));
        ARENA_CHAT  = ChatColor.translateAlternateColorCodes('&', configFile.getString("Chat.arena-chat"));
    }


    public FileConfiguration getArenaFile() {
        return arena;
    }

    public String getPrefix() {
        String output = prefix == null ? ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Paintball" + ChatColor.DARK_GRAY + "] " : prefix + " ";
        return output;
    }

    public String getVersion() {
        return version;
    }

    public String getWebsite() {
        return website;
    }

    public String getTheme() {
        String code = theme == null ? "&3" : theme;
        return ChatColor.translateAlternateColorCodes('&', code);
    }

    public String getAuthor() {
        return author;
    }

    public String getSecondaryColor() {
        String code = secondary == null ? "&7" : secondary;
        return ChatColor.translateAlternateColorCodes('&', code);
    }
}
