package me.synapz.paintball.storage.files;

import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Jeremy on 4/29/2016.
 */
public class PaintballFile extends File {

    protected FileConfiguration fileConfig;

    protected PaintballFile(Plugin pb, String name) {
        super(pb.getDataFolder(), name);

        this.fileConfig = YamlConfiguration.loadConfiguration(this); // give settings a file to look into if it is the database file

        if (!this.exists()) {
            try {
                createNewFile();
            } catch (IOException e) {
                Messenger.error(Bukkit.getConsoleSender(), "Could not create " + name + ". Stack trace: ");
                e.printStackTrace();
            }
        }

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
}
