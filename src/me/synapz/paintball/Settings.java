package me.synapz.paintball;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Settings {

    private static Settings settings = new Settings();

    private Paintball pb;
    private FileConfiguration config, arena;
    private File aFile;

    private String prefix, version, theme, website, author, secondary;

    private Settings() {}

    public static Settings getSettings() {
        return settings;
    }

    public void init(Paintball pb) {
        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }

        this.pb = pb;
        pb.saveDefaultConfig();
        config = pb.getConfig();

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
        loadValues();
    }

    public void reloadConfig() {
        arena = YamlConfiguration.loadConfiguration(aFile);
        pb.reloadConfig();
        config = pb.getConfig();
        loadValues();
    }

    public void saveArenaFile() {
        try {
            arena.save(aFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    private void loadValues() {
        version = pb.getDescription().getVersion();
        website = pb.getDescription().getWebsite();
        author = pb.getDescription().getAuthors().toString();
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        theme = ChatColor.translateAlternateColorCodes('&', config.getString("theme-color"));
        secondary = ChatColor.translateAlternateColorCodes('&', config.getString("secondary-color"));
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
        return secondary;
    }

}
