package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSystem implements Listener {

    @EventHandler
    public void onChatInArena(AsyncPlayerChatEvent e) {
        for (Arena a : ArenaManager.getArenaManager().getArenas()) {
            if (a.containsPlayer(e.getPlayer())) {
                a.chat(e.getPlayer(), e.getMessage());
                e.setCancelled(true);
            }
        }
    }
}
