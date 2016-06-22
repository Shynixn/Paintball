package me.synapz.paintball.listeners;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.events.WagerEvent;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class WagerListener implements Listener{

    @EventHandler
    public void onWager(WagerEvent event) {
        Arena arena = event.getArena();
        ArenaPlayer arenaPlayer = event.getArenaPlayer();
        double amount = event.getAmount();
        WagerEvent.WagerResult result = event.getResult();

        switch (result) {
        case SUCCESS:
            break;
        }
    }
}
