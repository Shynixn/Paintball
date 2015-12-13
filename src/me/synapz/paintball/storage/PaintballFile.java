package me.synapz.paintball.storage;


import me.synapz.paintball.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import java.util.Set;

public abstract class PaintballFile {

    protected final FileConfiguration fileConfig;
    protected final File file;
    protected final String name;

    protected PaintballFile(Plugin pb, String name) {
        this.name = name;
        this.file = new File(pb.getDataFolder(), name);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }
        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
        this.saveFile();
    }

    public void saveFile() {
        try {
            this.fileConfig.save(this.file);
        } catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save " + this.name + ".", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }

    public FileConfiguration getFileConfig() {
        return this.fileConfig;
    }
}
