package me.synapz.paintball.killcoin;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class KillCoinItemHandler {

    private static Map<String, KillCoinItem> items = new HashMap<>();
    private static KillCoinItemHandler handler = new KillCoinItemHandler();

    private KillCoinItemHandler() {}

    public static final KillCoinItemHandler getHandler() {
            return handler;
    }

    public void addItem(KillCoinItem item) {
        items.put(item.getItemName(), item);
    }

    public void removeItem(KillCoinItem item) {
        items.remove(item.getItemName(), item);
    }

    public void showInventory(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GOLD + "KillCoin Shop");

        for (KillCoinItem item : items.values()) {
            ItemStack itemStack = new ItemStack(item);
            ItemMeta meta = itemStack.getItemMeta();
            List<String> newLore = new ArrayList<String>();

            if (!item.showItem()) {
                continue;
            }

            meta.setDisplayName(item.getItemName());

            if (!item.hasDescription())
                newLore.add("Description: " + item.getDescription());

            if (item.requiresMoney())
                newLore.add("Cost: " + item.getMoney());

            if (item.requiresKillCoins())
                newLore.add("KillCoins: " + item.getKillCoins());

            // TODO: add expiration time
            if (item.hasPermission() && !(player.hasPermission(item.getPermission())))
                newLore.add(ChatColor.RED + "You don't have permission for this item!");

            boolean canBuy = true;
            StringBuilder buying = new StringBuilder();
            if (arenaPlayer.getKillCoins() < item.getKillCoins()) {
                buying.append("Don't have enough KillCoins!");
                canBuy = false;
            }

            if (!canBuy) {
                newLore.add(buying.toString());
            }

            meta.setLore(newLore);
            itemStack.setItemMeta(meta);
            inv.addItem(itemStack);
        }
        player.openInventory(inv);
    }

    public void loadItemsFromConfig(FileConfiguration file) {
        if (!file.isConfigurationSection("Kill-Coin-Shop"))
            return;

        Set<String> rawItems = file.getConfigurationSection("Kill-Coin-Shop").getKeys(false);
        List<String> paths = new ArrayList<>();

        for (String rawItem : rawItems) {
           paths.add(rawItem.toString());
        }
        for (String path : paths) {
            new KillCoinItem("Kill-Coin-Shop." + path, file);
        }
    }
}
