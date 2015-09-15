package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.storage.PaintballFile;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.Statistics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class LeaderboardSigns implements Listener {

    // TODO: MAJOR. add leaderboard creation permissions
    // TODO: add reset stats command
    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        // paintball.sign.use
        if (e.getLines().length == 0 || !e.getLine(0).equalsIgnoreCase("pb") || !e.getLine(1).equalsIgnoreCase("lb")) return;

        StatType type = null;
        for (StatType t : StatType.values()) {
            if (t.getSignName().equalsIgnoreCase(e.getLine(2))) {
                type = t;
            }
        }
        if (type == null) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.RED, e.getLine(2) + " is an invalid leaderboard type.", "Choose <kd/kills/deaths/killstreak/gamesplayed/won/lost>");
            e.getBlock().breakNaturally();
            return;
        }

        if (e.getLine(3).isEmpty()) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.RED, "Line 4 cannot be blank.", "Choose a rank number. For example 3");
            e.getBlock().breakNaturally();
            return;
        }
        int i;
        try {
            i = Integer.parseInt(e.getLine(3));
        } catch (NumberFormatException ex) {
            Message.getMessenger().msg(e.getPlayer(), ChatColor.RED, "Line 4 must be a valid number.");
            e.getBlock().breakNaturally();
            return;
        }


        String prefix = ChatColor.DARK_GRAY + "[" + Settings.getSettings().getTheme() + "Paintball" + ChatColor.DARK_GRAY + "]";
        Message.getMessenger().msg(e.getPlayer(), ChatColor.GREEN, "Auto Join sign successfully created!");
        e.setLine(0, ChatColor.DARK_AQUA + "Paintball Leaderboard");
        e.setLine(1, type.getName());
        e.setLine(2, "#" + i + " " + Statistics.instance.getPlayerAtRank(i, type));
        e.setLine(3, "");
    }

    @EventHandler
    public void onArenaTryToJoinOnClick(PlayerInteractEvent e) {
        // paintball.sign.create
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || !(e.getClickedBlock().getType() == Material.SIGN) && !(e.getClickedBlock().getType() != Material.SIGN_POST)) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (!sign.getLine(0).contains("Paintball") || sign.getLine(1) == null) return;

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

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Message.getMessenger().msg(player, ChatColor.RED, "No arena named " + sign.getLine(1) + " found.");
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
