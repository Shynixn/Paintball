package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.storage.PaintballFile;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class LeaderboardSigns implements Listener {

    // TODO: MAJOR. add leaderboard creation permissions
    // TODO: add reset stats command
    // TODO: add so when they click the sign it show the player's stats
    // TODO: make a refresh thing go everytime a statistic is added, make it set the signs's everything and do checks if it changed so we dont double change them
    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length <= 3 || !e.getLine(0).equalsIgnoreCase("pb") || !e.getLine(1).equalsIgnoreCase("lb")) return;

        if (!Message.getMessenger().signPermissionValidator(e.getPlayer(), "paintball.leaderboard.create")) {
            return;
        }

        StatType type = null;
        for (StatType t : StatType.values()) {
            if (t.getSignName().equalsIgnoreCase(e.getLine(2))) {
                type = t;
            }
        }

        if (type == null) {
            String error = e.getLine(2) + " is an invalid statistic type.";
            if (e.getLine(2).isEmpty()) {
                error = "Choose a statistic type on line 3.";
            }


            Message.getMessenger().msg(e.getPlayer(), false, ChatColor.RED, error, "Choose either " + StatType.getReadableList());
            e.getBlock().breakNaturally();
            return;
        }

        if (e.getLine(3).isEmpty()) {
            Message.getMessenger().msg(e.getPlayer(), false, ChatColor.RED, "Line 4 cannot be blank.", "Choose a rank number, for example: 3");
            e.getBlock().breakNaturally();
            return;
        }

        int i;
        try {
            i = Integer.parseInt(e.getLine(3));
        } catch (NumberFormatException ex) {
            Message.getMessenger().msg(e.getPlayer(), false, ChatColor.RED, "Line 4 must be a valid number.");
            e.getBlock().breakNaturally();
            return;
        }

        Message.getMessenger().msg(e.getPlayer(), false, ChatColor.GREEN, "Leaderboard sign successfully created!");
        // todo: beter way
        Paintball pb = (Paintball) JavaPlugin.getProvidingPlugin(Paintball.class);
        HashMap<String, String> playerAndStat = pb.getPlayerData().getPlayerAtRank(i, type);
        e.setLine(0, ChatColor.DARK_GRAY + "[" + Settings.getSettings().getTheme() + "Leaderboards" + ChatColor.DARK_GRAY + "]");
        e.setLine(1, type.getName());
        e.setLine(2, "#" + i + " " + playerAndStat.keySet().toArray()[0]);
        e.setLine(3, playerAndStat.values().toArray()[0] + "");
    }

    @EventHandler
    public void onLeaderboardSignclick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (sign.getLine(0) == null || !sign.getLine(0).equals("Paintball Leaderboard") || sign.getLine(1) == null) return;

        // todo: get player of clicked sign's all stats and show them all their stats
    }
}
