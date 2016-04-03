package me.synapz.paintball.events;

import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length <= 3 || !e.getLine(0).equalsIgnoreCase("pb") || !e.getLine(1).equalsIgnoreCase("lb")) return;

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.create")) {
            return;
        }

        StatType type = StatType.getStatType(e.getPlayer(), e.getLine(2));

        if (type == null)
            return;

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
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN)
            return;

        if (!(e.getClickedBlock().getState() instanceof Sign))
            return;

        Sign sign = (Sign) e.getClickedBlock().getState();

        if (sign.getLines().length < 4)
            return;

        Player player = e.getPlayer();

        if (!isLeaderboardSign(sign))
            return;

        if (Messenger.signPermissionValidator(player, "paintball.leaderboard.use"))
            Settings.PLAYERDATA.getStats(player, sign.getLine(1));
    }

    private boolean isLeaderboardSign(Sign sign) {
        boolean hasStatType = false;
        boolean isInFile = Settings.ARENA.getSigns().get(sign.getLocation()) != null;

        for (StatType type : StatType.values()) {
            if (sign.getLine(2).replace("/", "").replace(" ", "").equalsIgnoreCase(type.getSignName())) {
                hasStatType = true;
                break;
            }
        }

        if (!sign.getLine(0).startsWith("#"))
            return false;

        // In case the location was not found and it is a leaderboard sign, re-add it.
        if (!isInFile && sign.getLine(0).contains("#") && hasStatType) {
            new SignLocation(sign.getLocation(), SignLocation.SignLocations.LEADERBOARD);
            isInFile = true;
        }

        return isInFile;
    }
}
