package me.synapz.paintball;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpirationTime extends BukkitRunnable {

    private static Map<String, ExpirationTime> times = new HashMap<>();

    private final ArenaPlayer arenaPlayer;
    private final Player player;
    private double counter;
    private final KillCoinItem item;
    private final PlayerInventory inv;

    public ExpirationTime(KillCoinItem item, ArenaPlayer arenaPlayer, double counter) {
        this.counter = counter;
        this.arenaPlayer = arenaPlayer;
        this.player = arenaPlayer.getPlayer();
        this.inv = arenaPlayer.getPlayer().getInventory();
        this.item = item;

        times.put(item.getItemMeta().getDisplayName(), this);
    }

    @Override
    public void run() {
        if (arenaPlayer == null || counter <= 0) {
            this.cancel();
            return;
        } else {
            updateItem();
        }
        counter -= 0.5; // it isn't 1 bwcause we want this to run every .5 seconds so we can easily update inventories ActionBars faster
    }

    // Overrides cancel so that it cancels the task AND removes the item from inventory (if it is in the inventory)
    @Override
    public void cancel() {
        if (arenaPlayer != null) {
            // inv.remove(getItemInInventory(item.getItemMeta().getDisplayName()));
            Message.getMessenger().msg(player, true, ChatColor.RED, Settings.THEME + "Item " + Settings.SECONDARY + ChatColor.stripColor(item.getItemName()) + Settings.THEME + " has expired!");
        }
        removeActionBar();
        times.remove(item.getItemMeta().getDisplayName(), item);
        super.cancel();
    }

    private void updateItem() {
        if (item.equals(inv.getItemInHand()) && times.get(inv.getItemInHand().getItemMeta().getDisplayName()) != null) {
            // TODO: if actionbar is installed and true in config
            if (times.get(inv.getItemInHand().getItemMeta().getDisplayName()).getCounter() == counter) {
                ActionBarAPI.sendActionBar(player, Settings.THEME + "Expires in: " + Settings.SECONDARY + (int)counter + Settings.THEME + " seconds");
            }
        } else {
            removeActionBar();
        }
    }

    private void removeActionBar() {
        ActionBarAPI.sendActionBar(player, "");
    }

    private double getCounter() {
        return counter;
    }
}
