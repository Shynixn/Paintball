package me.synapz.paintball.countdowns;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Utils;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class ExpirationCountdown extends PaintballCountdown {

    /*
    This Countdown class is responsible for CoinItems which have an ExpirationTime
     */

    private static Map<String, ExpirationCountdown> times = new HashMap<>();

    private final ArenaPlayer arenaPlayer;
    private final Player player;
    private final CoinItem item;
    private final PlayerInventory inv;

    public ExpirationCountdown(CoinItem item, ArenaPlayer arenaPlayer, double counter) {
        super(counter+1);
        this.counter = counter;
        this.arenaPlayer = arenaPlayer;
        this.player = arenaPlayer.getPlayer();
        this.inv = arenaPlayer.getPlayer().getInventory();
        this.item = item;
        this.end = 1;

        times.put(item.getItemMeta().getDisplayName(), this);
    }

    @Override
    public void onFinish() {
        return;
    }

    @Override
    public void onIteration() {
        ItemStack itemInHand = inv.getItemInHand();
        if (ProtectionCountdown.godPlayers.keySet().contains(player.getName()))
            return; // Dont want it to double when it players
        if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && item.equals(itemInHand) && times.get(inv.getItemInHand().getItemMeta().getDisplayName()) != null) {
            if (times.get(inv.getItemInHand().getItemMeta().getDisplayName()).getCounter() == counter) {
                BountifulAPI.sendActionBar(player, Messenger.EXPIRATION_TIME.replace("%time%", String.valueOf((int)(counter-1))));
            }
        } else if (itemInHand != null && itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName() && times.get(inv.getItemInHand().getItemMeta().getDisplayName()) != null) {
        } else {
            Utils.removeActionBar(player);
        }
    }

    @Override
    public boolean stop() {
        // Will stop if... The player is null (left), the counter is finished, the player's health isn't 0 (aren't dead), and the inventory contains the item
        return player == null || arenaPlayer == null || counter <= 0 || player.getHealth() != 0 && !inventoryContainsItem();
    }

    @Override
    public boolean intervalCheck() {
        return true;
    }

    // Overrides cancel so that it cancels the task AND removes the item from inventory (if it is in the inventory)
    @Override
    public void cancel() {
        item.remove(arenaPlayer);
        times.remove(item.getItemName(true), this);
        Utils.removeActionBar(player);
        super.cancel();
    }

    private boolean inventoryContainsItem() {
        for (ItemStack itemStack : player.getInventory()) {
            if (item.equals(itemStack))
                return true;
        }
        return false;
    }
}