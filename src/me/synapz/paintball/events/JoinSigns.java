package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class JoinSigns implements Listener {

    // TODO: MAJOR, add permissions for adding signs
    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length == 0 || e.getLines().length >= 1 && !e.getLine(0).contains("pb")) return;

        if (e.getLine(1).equalsIgnoreCase("autojoin") == false && e.getLine(1).equalsIgnoreCase("join") == false) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.RED, "Wrong syntax for creating Paintball sign.", "For more information: " + Settings.getSettings().getWebsite());
            e.getBlock().breakNaturally();
            return;
        }

        String prefix = ChatColor.DARK_GRAY + "[" + Settings.getSettings().getTheme() + "Paintball" + ChatColor.DARK_GRAY + "]";
        // For Auto joining
        if (e.getLine(1).equalsIgnoreCase("autojoin")) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.GREEN, "Auto Join sign successfully created!");
            e.setLine(0, prefix);
            e.setLine(1, ChatColor.GREEN + "Auto Join");
            e.setLine(2, "");
            e.setLine(3, "");
            return;
        }

        // For joining a specific Arena
        if (e.getLine(1).equalsIgnoreCase("join")) {
            Arena a = ArenaManager.getArenaManager().getArena(e.getLine(2));
            if (Utils.nullCheck(e.getLine(2), a, e.getPlayer())) {
                e.setLine(0, prefix);
                e.setLine(1, a.getName());
                e.setLine(2, a.getStateAsString());
                e.setLine(3, "");
                Message.getMessenger().msg(e.getPlayer(), ChatColor.GREEN, a.toString() + " join sign successfully created!");
            } else {
                e.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler
    public void onArenaTryToJoinOnClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || !(e.getClickedBlock().getType() == Material.SIGN) && !(e.getClickedBlock().getType() != Material.SIGN_POST)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (!sign.getLine(0).contains("Paintball")) return;

        if (sign.getLine(1).equals(ChatColor.GREEN + "Auto Join")) {
            for (Arena a : ArenaManager.getArenaManager().getArenas()) {
                if (a.getState() == Arena.ArenaState.WAITING) {
                    a.joinLobby(player, null);
                    return;
                }
            }
            Message.getMessenger().msg(player, ChatColor.RED, "No arenas are currently opened.");
            return;
        }

        for (Arena a : ArenaManager.getArenaManager().getArenas()) {
            if (sign.getLine(1).contains(a.getName())) {
                if (a.getStateAsString().contains("WAITING")) {
                    a.joinLobby(player, null);
                    return;
                } else {
                    Message.getMessenger().msg(player, ChatColor.RED, a.toString() + ChatColor.RED + " is not available to join!");
                }
            }
        }


    }
}
