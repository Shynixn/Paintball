package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    public void onBlockBreakInArena(BlockBreakEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to break blocks while in the arena!"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceInArena(BlockPlaceEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to place blocks while in the arena!"))
            e.setCancelled(true);
    }

    private boolean stopAction(Player player, String message) {
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (a != null) {
            Message.getMessenger().msg(player, true, ChatColor.RED, message);
            return true;
        }
        return false;
    }
}