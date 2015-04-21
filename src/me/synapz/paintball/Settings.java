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
    private File cFile, aFile;

    public String prefix, version, theme;

    private Settings() {}

    public static Settings getSettings() {
        return settings;
    }

    public void init(Paintball pb) {
        this.pb = pb;
        config = pb.getConfig();
        config.options().copyDefaults(true);
        cFile = new File(pb.getDataFolder(), "config.yml");

        if (!pb.getDataFolder().exists()) {
            pb.getDataFolder().mkdir();
        }
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

        version = pb.getDescription().getVersion();
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        theme = ChatColor.translateAlternateColorCodes('&', config.getString("theme-color"));

    }

    public void saveConfig() {
        try {
            config.save(cFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save config.yml. Stack trace:");
            e.printStackTrace();
        }
    }

    /**
     * DOES NOT WORK
     */
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cFile);
        arena = YamlConfiguration.loadConfiguration(aFile);
        try {
            config.save(cFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save config.yml. Stack trace:");
            e.printStackTrace();
        }
    }

    public void saveArenaFile() {
        try {
            arena.save(aFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save arenas.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    public FileConfiguration getArenaFile() {
        return arena;
    }
}


