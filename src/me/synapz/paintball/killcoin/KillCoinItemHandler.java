package me.synapz.paintball.killcoin;

import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class KillCoinItemHandler {

    private static Map<String, KillCoinItem> items = new HashMap<>();
    private static KillCoinItemHandler handler = new KillCoinItemHandler();

    private KillCoinItemHandler() {}

    public static final KillCoinItemHandler getHandler() {
            return handler;
    }

    public void addItem(KillCoinItem item) {
        items.put(ChatColor.translateAlternateColorCodes('&', item.getItemName(true)), item);
    }

    public void showInventory(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GOLD + "KillCoin Shop");

        for (KillCoinItem item : items.values()) {
            inv.addItem(item.getItemStack(arenaPlayer, true));
        }
        player.openInventory(inv);
    }

    public Map<String, KillCoinItem> getAllItems() {
        return items;
    }

    public void loadItemsFromConfig(FileConfiguration file) {
        // TODO: Re-enable this.
        /*
        if (!file.isConfigurationSection("Kill-Coin-Shop"))
            return;

        Set<String> rawItems = file.getConfigurationSection("Kill-Coin-Shop").getKeys(false);

        for (String rawItem : rawItems) {
            new KillCoinItem("Kill-Coin-Shop." + rawItem, file);
        }
        */
    }
}