package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.players.PaintballPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSystem implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatInArena(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);

        if (a != null && a.containsPlayer(player)) {
            if (a.USE_ARENA_CHAT) {
                a.getPaintballPlayer(player).chat(e.getMessage());
                e.setCancelled(true);
            }
        }
    }
}
