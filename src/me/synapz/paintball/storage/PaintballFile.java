package me.synapz.paintball.storage;


import me.synapz.paintball.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class PaintballFile {

    private FileConfiguration fileConfig;
    private File file;
    private String name;

    public PaintballFile(Plugin pb, String name) {
        this.name = name;

        file = new File(pb.getDataFolder(), name);

        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }
        fileConfig = YamlConfiguration.loadConfiguration(file);
        saveFile();
    }

    public void saveFile() {
        try {
            fileConfig.save(file);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save " + name + ".", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public FileConfiguration getFileConfig() {
        return fileConfig;
    }
}
