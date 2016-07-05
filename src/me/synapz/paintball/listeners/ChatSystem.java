package me.synapz.paintball.listeners;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatSystem implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChatInArena(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);

        if (a != null && a.containsPlayer(player)) {
            if (a.USE_ARENA_CHAT) {
                PaintballPlayer pbPlayer = a.getPaintballPlayer(player);

                a.getPaintballPlayer(player).chat(ChatColor.translateAlternateColorCodes('&', e.getMessage()), pbPlayer instanceof ArenaPlayer && a.PER_TEAM_CHAT_ARENA || pbPlayer instanceof LobbyPlayer && a.PER_TEAM_CHAT_LOBBY);
                e.setCancelled(true);
            }
        }
    }
}
