package me.synapz.paintball;

import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpirationTime extends BukkitRunnable {

    private final ArenaPlayer arenaPlayer;
    private int counter;
    private final ItemStack item;

    // Creates a new countdown
    public ExpirationTime(ItemStack item, ArenaPlayer arenaPlayer, int counter) {
        this.counter = counter;
        this.arenaPlayer = arenaPlayer;
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
        counter--;
    }

    // Overrides cancel so that it cancels the task AND removes the arena from the tasks
    @Override
    public void cancel() {
        if (arenaPlayer != null)
            arenaPlayer.getPlayer().getInventory().remove(item);
        super.cancel();
    }

    private void updateItem() {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Settings.THEME + " Time Left: " + Settings.SECONDARY + counter + Settings.THEME + " seconds");
        item.setItemMeta(meta);
        arenaPlayer.getPlayer().updateInventory();
    }
}
