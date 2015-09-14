package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.PbPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;

import static org.bukkit.Color.*;
import static org.bukkit.Color.YELLOW;

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