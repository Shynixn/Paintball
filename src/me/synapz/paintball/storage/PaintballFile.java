package me.synapz.paintball.storage;


import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class PaintballFile extends File {

    protected FileConfiguration fileConfig;
    protected DatabaseSettings settings;

    protected PaintballFile(Plugin pb, String name) {
        super(pb.getDataFolder(), name);

        this.fileConfig = YamlConfiguration.loadConfiguration(this); // give settings a file to look into if it is the database file
        settings = new DatabaseSettings(fileConfig);

        if (!this.exists()) {
            try {
                createNewFile();
            } catch (IOException e) {
                Messenger.error(Bukkit.getConsoleSender(), "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }
        if (settings.sql && this.getName().contains("playerdata")) {
            this.fileConfig = Settings.DATABASE.addStats(YamlConfiguration.loadConfiguration(this));
        }
        this.saveFile();
    }

    public void saveFile() {
        try {
            if (settings.sql && this.getName().contains("playerdata")) {
                Settings.DATABASE.removeStats(fileConfig).save(this);
            } else {
                fileConfig.save(this);
            }
        } catch (Exception e) {
            Messenger.error(Bukkit.getConsoleSender(), "Could not save " + getName() + ".", "", "Stack trace");
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfig() {
        return this.fileConfig;
    }
}