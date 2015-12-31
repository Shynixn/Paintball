package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Arena.ArenaState;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.admin.Info;
import me.synapz.paintball.locations.SignLocation;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static me.synapz.paintball.storage.Settings.THEME;
import static me.synapz.paintball.storage.Settings.WEBSITE;
import static org.bukkit.ChatColor.*;

public class JoinSigns implements Listener {

    // TODO: MAJOR, add permissions for adding signs
    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        // paintball.sign.use
        if (e.getLines().length == 0 || !e.getLine(0).equalsIgnoreCase("pb") || e.getLine(1).equalsIgnoreCase("lb"))
            return;

        if (!e.getLine(1).equalsIgnoreCase("autojoin") && !e.getLine(1).equalsIgnoreCase("join")) {
            Message.getMessenger().msg(e.getPlayer(), false, RED, "Wrong syntax for creating Paintball sign.", "For more information: " + WEBSITE);
            e.getBlock().breakNaturally();
            return;
        }

        String prefix = DARK_GRAY + "[" + THEME + "Paintball" + DARK_GRAY + "]";
        // For Auto joining
        if (e.getLine(1).equalsIgnoreCase("autojoin")) {
            Message.getMessenger().msg(e.getPlayer(), false, GREEN, "Auto Join sign successfully created!");
            e.setLine(0, prefix);
            e.setLine(1, GREEN + "Auto Join");
            e.setLine(2, "");
            e.setLine(3, "");
            new SignLocation(e.getBlock().getLocation(), SignLocation.SignLocations.AUTOJOIN);
            return;
        }

        // For joining a specific Arena
        if (e.getLine(1).equalsIgnoreCase("join")) {
            Arena a = ArenaManager.getArenaManager().getArena(e.getLine(2));
            if (Utils.nullCheck(e.getLine(2), a, e.getPlayer())) {
                e.setLine(0, prefix);
                e.setLine(1, a.getName());
                e.setLine(2, a.getStateAsString()); // TODO add timer
                e.setLine(3, "");
                Message.getMessenger().msg(e.getPlayer(), false, GREEN, a + " join sign successfully created!");
                new SignLocation(a, e.getBlock().getLocation(), SignLocation.SignLocations.JOIN);
            } else {
                e.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler
    public void onArenaTryToJoinOnClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN && e.getClickedBlock().getType() != Material.SIGN_POST && e.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();
        if (!sign.getLine(0).contains("Paintball") || sign.getLine(1) == null) return;

        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Message.getMessenger().msg(player, false, RED, "You are already in an arena!");
            return;
        }
        if (sign.getLine(1).equals(GREEN + "Auto Join")) {
            // TODO: check if this works
            int openArenas = 0; // arenas which are able to join, if it is still 0 non are open so send error message
            HashMap<Integer, Arena> arenaSize = new HashMap<Integer, Arena>(); // a HashMap with the arena's lobby size to it's arena so you can easily fetch the arena from it's size
            List<Integer> sizes = new ArrayList<Integer>(); // all the sizes, this way we can call Collections.max and fetch the largest size to be used, then use the hashmap to get the size hooked with the arena
            for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
                if (a.getState() == ArenaState.WAITING) {
                    arenaSize.put(a.getLobbyPlayers().size(), a);
                    sizes.add(a.getLobbyPlayers().size());
                    openArenas++;
                }
            }
            if (openArenas == 0) {
                Message.getMessenger().msg(player, false, RED, "No arenas are currently opened.");
                return;
            }
            arenaSize.get(Collections.max(sizes)).joinLobby(player, null);
            return;
        }

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Message.getMessenger().msg(player, false, RED, "No arena named " + sign.getLine(1) + " found.");
            return;
        }
        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            if (sign.getLine(1).contains(a.getName())) {
                if (a.getStateAsString().contains("Waiting")) {
                    a.joinLobby(player, null);
                    return;
                } else {
                    Message.getMessenger().msg(player, false, RED, a.toString() + RED + " is not available to join!");
                }
            }
        }
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent e) {
        // Check to make sure it is a sign
        try {
            Sign sign = (Sign) e.getBlock().getState();
            sign.getType();
        } catch (ClassCastException exc) {
            return;
        }

        if (!e.getPlayer().hasPermission("paintball.sign.destroy"))
            Message.getMessenger().msg(e.getPlayer(), false, RED, "You do not have permission to destroy Paintball signs!");


        Sign sign = (Sign) e.getBlock().getState();
        SignLocation autoJoinOrLbsign = ArenaManager.getArenaManager().getSigns().get(sign.getLocation());

        if (autoJoinOrLbsign != null) {
            if (autoJoinOrLbsign.getType() == SignLocation.SignLocations.LEADERBOARD) {
                Message.getMessenger().msg(e.getPlayer(), false, GREEN, "Leaderboard sign has been successfully removed!");
            } else {
                Message.getMessenger().msg(e.getPlayer(), false, GREEN, "Autojoin sign has been successfully removed!");
            }
            autoJoinOrLbsign.removeSign();
        } else {
            Arena a = ArenaManager.getArenaManager().getArena(sign.getLine(1));
            if (a != null) {
                a.getSignLocations().get(sign.getLocation()).removeSign();
                Message.getMessenger().msg(e.getPlayer(), false, GREEN, a + "'s join sign has been successfully removed!");
            }
        }
        // todo: replace with other permission validator

        // TODO: add leaderboard and autojoin signs
    }
}
