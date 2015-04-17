package me.synapz.paint;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Settings {

    private static Settings settings = new Settings();

    private FileConfiguration config, arena;
    private File cFile, aFile;

    private String suffix, version;

    private Settings() {}

    public static Settings getSettings() {
        return settings;
    }

    public void init(PaintBall pb) {
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
                Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "", "Stack trace: ");
                e.printStackTrace();
            }
        }

        arena = YamlConfiguration.loadConfiguration(aFile);

        version = config.getString("version");
        suffix = config.getString("suffix");

        saveConfig();
    }

    public void saveConfig() {
        try {
            config.save(cFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save config.yml. Stack trace:");
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(cFile);
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

    public FileConfiguration getArenaFile() {
        return arena;
    }
}


