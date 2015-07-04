package me.synapz.paintball;


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
    private File aFile;

    private String prefix, version, theme, website, author, secondary;
    public static boolean SPLASH_PAINTBALLS, COLOR_PLAYER_TITLE, WOOL_HELMET;

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
        loadValues(pb.getConfig(), pb.getDescription());
    }

    public void reloadConfig() {
        arena = YamlConfiguration.loadConfiguration(aFile);
        pb.reloadConfig();
        loadValues(pb.getConfig(), pb.getDescription());
    }

    public void saveArenaFile() {
        try {
            arena.save(aFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
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
        COLOR_PLAYER_TITLE       = configFile.getBoolean("color-player-title");
        WOOL_HELMET         = configFile.getBoolean("wool-helmet");
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
