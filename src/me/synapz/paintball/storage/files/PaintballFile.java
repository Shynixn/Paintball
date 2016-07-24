package me.synapz.paintball.storage.files;

import me.synapz.paintball.enums.Databases;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PaintballFile extends File {

    protected Plugin plugin;
    protected FileConfiguration fileConfig;

    public PaintballFile(Plugin pb, String name) {
        super(pb.getDataFolder(), name);

        this.fileConfig = YamlConfiguration.loadConfiguration(this);
        this.plugin = pb;

        if (!this.exists()) {
            try {
                if (!this.getName().endsWith(".yml")) {
                    mkdir();
                } else {
                    onFirstCreate();
                    if (!Databases.SQL_ENABLED.getBoolean() && !this.getPath().contains("stats")) createNewFile();
                }
            } catch (IOException e) {
                Messenger.error(Bukkit.getConsoleSender(), "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }

        if (this.getName().endsWith(".yml"))
            this.saveFile();
    }

    public void saveFile() {
        try {
            fileConfig.save(this);
        } catch (Exception e) {
            Messenger.error(Bukkit.getConsoleSender(), "Could not save " + getName() + ".", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfig() {
        return this.fileConfig;
    }

    public void setFileConfig(FileConfiguration fileConfig) {
        this.fileConfig = fileConfig;
    }

    public void onFirstCreate() {

    }
}
