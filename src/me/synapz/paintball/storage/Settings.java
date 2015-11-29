package me.synapz.paintball.storage;


import com.connorlinfoot.titleapi.TitleAPI;
import me.synapz.paintball.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Settings {
    
    private static Settings settings;
    
    private Plugin pb;
    private FileConfiguration arena;
    private PlayerData cache;
    private File aFile;
    
    public static int ARENA_COUNTDOWN, ARENA_INTERVAL, ARENA_NO_INTERVAL, LOBBY_COUNTDOWN, LOBBY_INTERVAL, LOBBY_NO_INTERVAL;
    private String prefix, version, theme, website, author, secondary;
    private Map<ChatColor, String> teamNames = new HashMap<ChatColor, String>();
    public static String ARENA_CHAT, SPEC_CHAT;
    public static boolean SPLASH_PAINTBALLS, COLOR_PLAYER_TITLE, WOOL_HELMET, TITLE_API;
    
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
            }
            catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create arenas.yml. Stack trace: ");
                e.printStackTrace();
            }
        }
        
        
        arena = YamlConfiguration.loadConfiguration(aFile);
        cache = new PlayerData(pb);
        
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
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }
    
    public PlayerData getCache() {
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
        SPLASH_PAINTBALLS       = configFile.getBoolean("paintball-splash");
        COLOR_PLAYER_TITLE      = configFile.getBoolean("color-player-title");
        WOOL_HELMET             = configFile.getBoolean("give-wool-helmet");
        LOBBY_COUNTDOWN         = configFile.getInt("countdown.lobby.countdown");
        LOBBY_INTERVAL          = configFile.getInt("countdown.lobby.interval");
        LOBBY_NO_INTERVAL       = configFile.getInt("countdown.lobby.no-interval");
        ARENA_COUNTDOWN         = configFile.getInt("countdown.arena.countdown");
        ARENA_INTERVAL          = configFile.getInt("countdown.arena.interval");
        ARENA_NO_INTERVAL       = configFile.getInt("countdown.arena.no-interval");
        TITLE_API               = configFile.getBoolean("title-api") && Bukkit.getPluginManager().getPlugin("TitleAPI") != null;

        for (String colorcode : configFile.getConfigurationSection("Teams").getKeys(false)) {
            teamNames.put(ChatColor.getByChar(colorcode), configFile.getString("Teams." + colorcode) + "");
        }

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

    public Map<ChatColor, String> getTeamNames() {
        return teamNames;
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