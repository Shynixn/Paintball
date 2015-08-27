package me.synapz.paintball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static void removePlayerSettings(Player player) {
        // todo: exp saves
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setFireTicks(0);
    }

    public static List<ItemStack> getInventoryList(Player p, boolean isArmourList) {
        ItemStack[] list = isArmourList ? p.getInventory().getArmorContents() : p.getInventory().getContents();
        List<ItemStack> returnList = new ArrayList<ItemStack>();
        for (ItemStack item : list) {
            if (item != null) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    public static GameMode getLastGameMode(int gamemodeValue) {
        switch (gamemodeValue) {
            case 0:
                return GameMode.SURVIVAL;
            case 1:
                return GameMode.CREATIVE;
            case 2:
                return GameMode.ADVENTURE;
            case 3:
                return GameMode.SPECTATOR;
            default:
                return GameMode.SURVIVAL;
        }
    }

    public static void countdown(final Arena a, final int seconds) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("Paintball"), new Runnable() {
            int i = seconds;
            public void run() {
                a.broadcastMessage(ChatColor.GREEN, "Game starting in " + i + " seconds!");
                i--;
            }
        }, 10*20L);
    }
}
