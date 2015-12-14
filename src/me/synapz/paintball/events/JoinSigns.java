package me.synapz.paintball.events;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Arena.ArenaState;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
                Message.getMessenger().msg(e.getPlayer(), false, GREEN, a + " join sign successfully created!");
                ArenaManager.getArenaManager().storeSignLocation(e.getBlock().getLocation(), a);
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
            // TODO: check all arenas then autojoin the one closest to the max amount of players
            for (Arena a : ArenaManager.getArenaManager().getArenas()) {
                if (a.getState() == ArenaState.WAITING) {
                    a.joinLobby(player, null);
                    return;
                }
            }
            Message.getMessenger().msg(player, false, RED, "No arenas are currently opened.");
            return;
        }

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Message.getMessenger().msg(player, false, RED, "No arena named " + sign.getLine(1) + " found.");
            return;
        }
        for (Arena a : ArenaManager.getArenaManager().getArenas()) {
            if (sign.getLine(1).contains(a.getName())) {
                if (a.getStateAsString().contains("WAITING")) {
                    a.joinLobby(player, null);
                    return;
                } else {
                    Message.getMessenger().msg(player, false, RED, a.toString() + RED + " is not available to join!");
                }
            }
        }
    }

    @EventHandler
    public void onLobbySignBreak(BlockBreakEvent e) {
        // paintball.sign.destroy
        if (!(e.getBlock().getType() == Material.SIGN) && !(e.getBlock().getType() == Material.SIGN_POST)) return;
        Sign sign = (Sign) e.getBlock().getState();

        if (!sign.getLine(0).contains("Paintball")) return;
        // todo: replace with other permission validator
        if (!e.getPlayer().hasPermission("paintball.sign.destroy"))
            Message.getMessenger().msg(e.getPlayer(), false, RED, "You do not have permission to destroy Paintball signs!");
        // make sure it is a join sign and not a auto-join sign by making sure it has an Arena state
        boolean joinSign = false;
        for (ArenaState state : ArenaState.values()) {
            if (sign.getLine(2).contains(state.toString())) {
                joinSign = true;
            }
        }

        if (joinSign) {
            Arena a = ArenaManager.getArenaManager().getArena(sign.getLine(1));
            ArenaManager.getArenaManager().removeSignLocation(sign.getLocation(), a);
            Message.getMessenger().msg(e.getPlayer(), false, GREEN, a + "'s join sign has been successfully removed!");
        }
    }
}
