package me.synapz.paintball.storage;


import me.synapz.paintball.Message;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CacheFile {

    FileConfiguration cache;
    File cacheFile;

    public CacheFile(Plugin pb) {
        cacheFile = new File(pb.getDataFolder(), "cache.yml");

        if (!cacheFile.exists()) {
            try {
                cacheFile.createNewFile();
            }
            catch (IOException e) {
                Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "", "Could not create cache.yml. Stack trace: ");
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
            Message.getMessenger().msg(Bukkit.getConsoleSender(), false, ChatColor.RED, "Could not save cache.yml.", "", "Stack trace" );
            e.printStackTrace();
        }
    }

    public void savePlayerInformation(Player player) {
        UUID id = player.getUniqueId();
        cache.set(id + ".Name", player.getName());
        cache.set(id + ".Location", player.getLocation());
        cache.set(id + ".GameMode", player.getGameMode().getValue());
        cache.set(id + ".FoodLevel", player.getFoodLevel());
        cache.set(id + ".Health", player.getHealth());
        cache.set(id + ".Inventory", Utils.getInventoryList(player, false));
        cache.set(id + ".Armour", Utils.getInventoryList(player, true));

        saveCacheFile();
    }

    public void restorePlayerInformation(UUID id) {
        Player player = Bukkit.getServer().getPlayer(id);

        player.teleport((Location) cache.get(id + ".Location"));
        player.getInventory().setContents(getLastInventoryContents(id, ".Inventory"));
        player.getInventory().setArmorContents(getLastInventoryContents(id, ".Armour"));
        player.setFoodLevel(cache.getInt(id + ".FoodLevel"));
        player.setHealth(cache.getInt(id + ".Health"));
        player.setGameMode(Utils.getLastGameMode(cache.getInt(id + ".GameMode")));

        cache.set(id.toString(), null);
        saveCacheFile();
    }

    private ItemStack[] getLastInventoryContents(UUID id, String path) {
        ItemStack[] items = new ItemStack[cache.getList(id + path).size()];
        int count = 0;
        for (Object item : cache.getList(id + path).toArray()) {
            if (item instanceof ItemStack) {
                items[count] = new ItemStack((ItemStack)item);
                count++;
            }
        }
        return items;
    }
}
