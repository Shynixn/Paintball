package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler
    public void inGameLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(p);
        if (a != null) {
            a.leave(p);
        }
    }
}
