package me.synapz.paintball.coin;

import me.synapz.paintball.enums.Items;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class CoinItemHandler {

    private static Map<String, CoinItem> items = new HashMap<>();
    private static CoinItemHandler handler = new CoinItemHandler();
    private static final List<Items> duelWieldItems = new ArrayList<Items>() {{
        add(Items.DUEL_WIELD);
    }};

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
        Map<Integer, CoinItem> sortedItems = new HashMap<>();

        for (CoinItem item : items.values()) {
            int price = 0;

            if (item.showItem()) {

                if (item.hasPermission())
                    price += 100000;

                if (item.requiresCoins())
                    price += item.getCoins()*2+.1;

                if (item.requiresMoney())
                    price += item.getMoney()*2+.2;

                if (item.hasExpirationTime())
                    price += .3;

                while (sortedItems.keySet().contains(price))
                    price++;

                sortedItems.put(price, item);
            }
        }

        List<Integer> sortedValues = new ArrayList<>(sortedItems.keySet());

        Collections.sort(sortedValues);

        for (Integer key : sortedValues) {
            inv.addItem(sortedItems.get(key).getItemStack(arenaPlayer, true));
        }

        player.openInventory(inv);
    }

    public Map<String, CoinItem> getAllItems() {
        return items;
    }

    public List<Items> getDuelWieldItems() {
        return duelWieldItems;
    }
}
