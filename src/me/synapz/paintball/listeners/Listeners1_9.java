package me.synapz.paintball.listeners;

import me.synapz.paintball.enums.Messages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Jeremy on 4/29/2016.
 */
public class Listeners1_9 extends BaseListener implements Listener {

    @EventHandler
    public void onDuelWield(PlayerSwapHandItemsEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to duel wield!"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTryToDuelWield(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (e.getRawSlot() == 45) {
            if (stopAction(player, Messages.ARENA_NO_DUEL_WIELD.getString()))
                e.setCancelled(true);
        }
    }
}
