package me.synapz.paintball;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpirationTime extends BukkitRunnable {

    private final ArenaPlayer arenaPlayer;
    private final Player player;
    private double counter;
    private final KillCoinItem item;
    private final Inventory inv;

    // Creates a new countdown
    public ExpirationTime(KillCoinItem item, ArenaPlayer arenaPlayer, double counter) {
        this.counter = counter;
        this.arenaPlayer = arenaPlayer;
        this.player = arenaPlayer.getPlayer();
        this.inv = arenaPlayer.getPlayer().getInventory();
        this.item = item;
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

    // Overrides cancel so that it cancels the task AND removes the arena from the tasks
    @Override
    public void cancel() {
        if (arenaPlayer != null) {
            arenaPlayer.getPlayer().getInventory().remove(item);
            Message.getMessenger().msg(player, true, ChatColor.RED, Settings.THEME + "Item " + Settings.SECONDARY + ChatColor.stripColor(item.getItemName()) + Settings.THEME + " has expired!");
        }
        super.cancel();
    }

    private void updateItem() {
        ItemStack itemInHand = player.getItemInHand();
        if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && itemInHand.getItemMeta().getDisplayName().equals(item.getItemName())) {
            // TODO: if actionbar is installed and true in config
            ActionBarAPI.sendActionBar(player, Settings.THEME + "Expires in: " + Settings.SECONDARY + (int)counter + Settings.THEME + " seconds");
        } else {
            ActionBarAPI.sendActionBar(player, "");
        }
        // TODO: disallow to buy this item 2 times
        // TODO: what if player moves the item?
    }
}
