package me.synapz.paintball.listeners;

import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.locations.SkullLocation;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class LeaderboardSigns implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignCreate(SignChangeEvent e) {
        if (e.getLines().length <= 3 || !e.getLine(0).equalsIgnoreCase("pb") || !e.getLine(1).contains("lb")) return;

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.create"))
            return;

        StatType type = StatType.getStatType(e.getPlayer(), e.getLine(2));

        if (type == null)
            return;

        if (e.getLine(3).isEmpty()) {
            Messenger.error(e.getPlayer(), Messages.VAlID_4_NUMBER, Messages.CHOOSE_RANK_NUMBER);
            e.getBlock().breakNaturally();
            return;
        }

        int i;
        try {
            i = Integer.parseInt(e.getLine(3));
        } catch (NumberFormatException ex) {
            Messenger.error(e.getPlayer(), Messages.VAlID_4_NUMBER);
            e.getBlock().breakNaturally();
            return;
        }

        Map<String, String> playerAndStat = Settings.getSettings().getStatsFolder().getPlayerAtRankMap(i, type);
        String player = (String) playerAndStat.keySet().toArray()[0];
        String value = (String) playerAndStat.values().toArray()[0];

        if (e.getLine(1).contains("skull")) {
            Messenger.success(e.getPlayer(), Messages.SKULL_CREATED);

            Sign sign = (Sign) e.getBlock().getState();
            BlockFace directionFacing = ((org.bukkit.material.Sign) sign.getData()).getFacing();

            e.getBlock().breakNaturally();

            new SkullLocation(e.getBlock().getLocation(), type, i).makeSkullBlock(directionFacing);
        } else {
            Messenger.success(e.getPlayer(), Messages.SKULL_CREATED);

            e.setLine(0, "#" + i);
            e.setLine(1, player);
            e.setLine(2, type.getName());
            e.setLine(3, value);

            new SignLocation(e.getBlock().getLocation(), SignLocation.SignLocations.LEADERBOARD);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeaderboardSignclick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN && e.getClickedBlock().getType() != Material.SKULL && e.getClickedBlock().getType() != Material.SKULL_ITEM)
            return;

        BlockState state = e.getClickedBlock().getState();
        Player player = e.getPlayer();

        if (state instanceof Sign) {
            Sign sign = (Sign) e.getClickedBlock().getState();

            if (sign.getLines().length < 4)
                return;

            if (!isLeaderboardSign(sign))
                return;

            if (Messenger.signPermissionValidator(player, "paintball.leaderboard.use"))
                Settings.getSettings().getStatsFolder().getStats(player, sign.getLine(1));
        } else if (state instanceof Skull) {
            SignLocation signLoc = Settings.ARENA.getSigns().get(Utils.simplifyLocation(e.getClickedBlock().getLocation()));

            if (signLoc != null && signLoc instanceof SkullLocation && Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.use")) {
                Skull skull = (Skull) state;

                Settings.getSettings().getStatsFolder().getStats(player, skull.getOwner());
            }

        }
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
