package me.synapz.paintball.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

/**
 * Created by Jeremy on 4/29/2016.
 */
public class Listeners1_9 extends BaseListener implements Listener{

    @EventHandler
    public void onDuelWield(PlayerSwapHandItemsEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to duel wield!"))
            e.setCancelled(true);
    }
}
