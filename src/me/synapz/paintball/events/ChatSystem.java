package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSystem implements Listener {

    @EventHandler
    public void onChatInArena(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);

        if (a != null && a.containsPlayer(player)) {
            a.getPaintballPlayer(player).chat(e.getMessage());
            e.setCancelled(true);
        }
    }
}
