package me.synapz.paintball.killcoin;

import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KillCoinItemHandler {

    private static Map<String, KillCoinItem> items = new HashMap<>();
    private static final KillCoinItemHandler handler = new KillCoinItemHandler();

    private KillCoinItemHandler() {

    }

    public static final KillCoinItemHandler getHandler() {
            return handler;
    }

    public void addItem(KillCoinItem item) {
        items.put(item.getName(), item);
    }

    public void removeItem(KillCoinItem item) {
        items.remove(item.getName(), item);
    }

    public void showInventory(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GOLD + "KillCoin Shop");

        for (KillCoinItem item : items.values()) {
            // Display name
            // Description
            // Cost
            // KillCoins
            // Expiration Time
            // Permission

            ItemMeta meta = item.getItemMeta();
            List<String> newLore = new ArrayList<String>();

            if (!item.isToShow())
                continue;

            meta.setDisplayName(item.getName());

            if (!item.getDescription().isEmpty())
                newLore.add("Description: " + item.getDescription());

            if (item.requiredMoney() != -1 || item.requiredMoney() != 0)
                newLore.add("Cost: " + item.requiredMoney());

            if (item.requiredKillCoins() != -1 || item.requiredKillCoins() != 0)
                newLore.add("KillCoins: " + item.requiredKillCoins());

            if (item.requiresPermission() && !(player.hasPermission(item.getPermission())))
                newLore.add(ChatColor.RED + "You don't have permission for this item!");

            boolean canBuy = true;
            StringBuilder buying = new StringBuilder();
            if (arenaPlayer.getKillCoins() < item.requiredKillCoins()) {
                buying.append("Don't have enough KillCoins!");
                canBuy = false;
            }

            if (!canBuy) {
                newLore.add(buying.toString());
            }

            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
        player.openInventory(inv);
    }

    public void loadItemsFromConfig() {
        FileConfiguration file = Settings.getSettings().getConfig();

        if (!file.isConfigurationSection("Kill-Coin-Shop"))
            return;
        Set<String> rawItems = file.getConfigurationSection("Kill-Coin-Shop").getKeys(false);

        for (String rawItem : rawItems) {
            new KillCoinItem(rawItem);
        }
    }
}
