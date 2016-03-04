package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class LeaderboardSigns implements Listener {

    // TODO: MAJOR. add leaderboard creation permissions
    // TODO: add reset stats command
    // TODO: add so when they click the sign it show the player's stats
    // TODO: make a refresh thing go everytime a statistic is added, make it set the signs's everything and do checks if it changed so we dont double change them
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length <= 3 || !e.getLine(0).equalsIgnoreCase("pb") || !e.getLine(1).equalsIgnoreCase("lb")) return;

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.create")) {
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


            Messenger.error(e.getPlayer(), error, "Choose either " + StatType.getReadableList());
            e.getBlock().breakNaturally();
            return;
        }

        if (e.getLine(3).isEmpty()) {
            Messenger.error(e.getPlayer(), "Line 4 cannot be blank.", "Choose a rank number, for example: 3");
            e.getBlock().breakNaturally();
            return;
        }

        int i;
        try {
            i = Integer.parseInt(e.getLine(3));
        } catch (NumberFormatException ex) {
            Messenger.error(e.getPlayer(), "Line 4 must be a valid number.");
            e.getBlock().breakNaturally();
            return;
        }

        Messenger.success(e.getPlayer(), "Leaderboard sign successfully created!");
        HashMap<String, String> playerAndStat = Settings.PLAYERDATA.getPlayerAtRank(i, type);
        e.setLine(0, "#" + i);
        e.setLine(1, playerAndStat.keySet().toArray()[0] + "");
        e.setLine(2, type.getName());
        e.setLine(3, playerAndStat.values().toArray()[0] + "");
        new SignLocation(e.getBlock().getLocation(), SignLocation.SignLocations.LEADERBOARD);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeaderboardSignclick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN) return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (sign.getLine(0) == null || !sign.getLine(0).equals("Paintball Leaderboard") || sign.getLine(1) == null) return;

        // todo: get player of clicked sign's all stats and show them all their stats
    }
}
