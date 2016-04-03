package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.DARK_GRAY;
import static org.bukkit.ChatColor.GREEN;

public class JoinSigns implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignCreate(SignChangeEvent e) {
        // paintball.sign.use
        if (e.getLines().length == 0 || !e.getLine(0).equalsIgnoreCase("pb") || e.getLine(1).equalsIgnoreCase("lb"))
            return;

        if (!e.getLine(1).equalsIgnoreCase("autojoin") && !e.getLine(1).equalsIgnoreCase("join")) {
            Messenger.error(e.getPlayer(), "Wrong syntax for creating Paintball sign.");
            e.getBlock().breakNaturally();
            return;
        }

        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";
        // For Auto joining
        if (e.getLine(1).equalsIgnoreCase("autojoin")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.create"))
                return;
            Messenger.success(e.getPlayer(), "Auto Join sign successfully created!");
            e.setLine(0, prefix);
            e.setLine(1, GREEN + "Auto Join");
            e.setLine(2, "");
            e.setLine(3, "");
            new SignLocation(e.getBlock().getLocation(), SignLocation.SignLocations.AUTOJOIN);
            return;
        }

        // For joining a specific Arena
        if (e.getLine(1).equalsIgnoreCase("join")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.join.create"))
                return;

            Arena a = ArenaManager.getArenaManager().getArena(e.getLine(2));
            if (Utils.nullCheck(e.getLine(2), a, e.getPlayer())) {
                e.setLine(0, prefix);
                e.setLine(1, a.getName());
                e.setLine(2, a.getStateAsString());
                e.setLine(3, "0/" + (a.getMax() <= 0 ? "0" : a.getMax()));
                Messenger.success(e.getPlayer(), a + " join sign successfully created!");
                new SignLocation(a, e.getBlock().getLocation(), SignLocation.SignLocations.JOIN);
            } else {
                e.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaTryToJoinOnClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();
        if (!sign.getLine(0).contains("Paintball") || sign.getLine(1) == null) return;

        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Messenger.error(player, "You are already in an arena!");
            return;
        }
        if (sign.getLine(1).equals(GREEN + "Auto Join")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.use"))
                return;

            Arena arenaToJoin = ArenaManager.getArenaManager().getBestArena();
            if (arenaToJoin == null) {
                Messenger.error(player, "No arenas are currently opened.");
                return;
            }

            // In case for some reason the sign is not in the sign locations (WorldEdited?) it adds it in
            if (Settings.ARENA.getSigns().get(sign.getLocation()) == null)
                new SignLocation(sign.getLocation(), SignLocation.SignLocations.AUTOJOIN);

            arenaToJoin.joinLobby(player, null);
            return;
        }

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.join.use"))
            return;

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Messenger.error(player, "No arena named " + sign.getLine(1) + " found.");
            return;
        }
        Arena arenaToJoin = ArenaManager.getArenaManager().getArenas().get(sign.getLine(1));

        // In case the sign is not found in config, add it so it can auto-update
        if (!arenaToJoin.getSignLocations().containsKey(sign.getLocation())) {
            new SignLocation(arenaToJoin, sign.getLocation(), SignLocation.SignLocations.JOIN);
            arenaToJoin.updateSigns();
        }

        if (arenaToJoin != null) {
            arenaToJoin.joinLobby(player, null);
        } else {
            Messenger.error(player, "Error arena is not available to join!");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignBreak(BlockBreakEvent e) {
        // Check to make sure it is a sign
        try {
            Sign sign = (Sign) e.getBlock().getState();
            sign.getType();
        } catch (ClassCastException exc) {
            return;
        }

        Sign sign = (Sign) e.getBlock().getState();
        SignLocation autoJoinOrLbsign = Settings.ARENA.getSigns().get(sign.getLocation());

        if (autoJoinOrLbsign != null) {
            if (autoJoinOrLbsign.getType() == SignLocation.SignLocations.LEADERBOARD) {
                if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.remove"))
                    Messenger.success(e.getPlayer(), "Leaderboard sign has been successfully removed!");
            } else {
                if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.remove"))
                    Messenger.success(e.getPlayer(), "Autojoin sign has been successfully removed!");
            }
            autoJoinOrLbsign.removeSign();
        } else {
            Arena a = ArenaManager.getArenaManager().getArena(sign.getLine(1));
            if (a != null) {
                if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.join.remove")) {
                    a.getSignLocations().get(sign.getLocation()).removeSign();
                    Messenger.success(e.getPlayer(), a.getName() + "'s join sign has been successfully removed!");
                }
            }
        }
    }
}
