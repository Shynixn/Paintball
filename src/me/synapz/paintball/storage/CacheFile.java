package me.synapz.paintball.storage;


import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class CacheFile {

    FileConfiguration cache;
    File cacheFile;

    public CacheFile(Paintball pb) {
        cacheFile = new File(pb.getDataFolder(), "cache.yml");

        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            }
            catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "", "Could not create cache.yml. Stack trace: ");
                e.printStackTrace();
            }
        }
        cache = YamlConfiguration.loadConfiguration(cacheFile);
        saveCacheFile();
    }

    public void saveCacheFile() {
        try {
            cache.save(cacheFile);
        }catch (Exception e) {
            Message.getMessenger().msg(Bukkit.getConsoleSender(), ChatColor.RED, "Could not save cache.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    public void savePlayerInformation(Player player) {
        UUID id = player.getUniqueId();
        cache.set(id + ".Name", player.getName());
        cache.set(id + ".Inventory", player.getInventory().getContents());
        cache.set(id + ".Location", player.getLocation());
        cache.set(id + ".Gamemode", player.getGameMode());
        cache.set(id + ".FoodLevel", player.getFoodLevel());
        cache.set(id + ".Health", player.getHealth());
        saveCacheFile();
    }

    public void restorePlayerInformation(UUID id) {
        Player player = Bukkit.getServer().getPlayer(id);

        player.teleport(getPlayerLastLocation(id));
        player.getInventory().setContents(getPlayerLastInventory(id).getContents());
        // todo: set fly mode, gamemode, everything from last time
        cache.set(id + "", null);
        saveCacheFile();
    }

    private Location getPlayerLastLocation(UUID id) {
        return (Location) cache.get(id + ".Location");
    }

    private Inventory getPlayerLastInventory(UUID id) {
        return (Inventory) cache.get(id + ".Inventory");
    }
}
