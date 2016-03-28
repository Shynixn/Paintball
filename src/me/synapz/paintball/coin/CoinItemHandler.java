package me.synapz.paintball.coin;

import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class CoinItemHandler {

    private static Map<String, CoinItem> items = new HashMap<>();
    private static CoinItemHandler handler = new CoinItemHandler();

    private CoinItemHandler() {}

    public static final CoinItemHandler getHandler() {
        return handler;
    }

    public void addItem(CoinItem item) {
        items.put(ChatColor.translateAlternateColorCodes('&', item.getItemName(false)), item);
    }

    public void showInventory(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        Inventory inv = Bukkit.createInventory(null, 18, ChatColor.GOLD + "Coin Shop");

        for (CoinItem item : items.values()) {
            if (item.showItem())
                inv.addItem(item.getItemStack(arenaPlayer, true));
        }
        player.openInventory(inv);
    }

    public Map<String, CoinItem> getAllItems() {
        return items;
    }
}
