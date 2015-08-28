package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    @EventHandler
    public void onArenaQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(p);
        if (a != null) {
            a.leave(p);
        }
    }

    @EventHandler
    public void onItemBreakInArena(BlockBreakEvent e) {
        Arena a = ArenaManager.getArenaManager().getArena(e.getPlayer());
        if (a != null) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.RED, "You are not allowed to break blocks while in arena!");
            e.setCancelled(true);
        }
    }
}