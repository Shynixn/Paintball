package me.synapz.paintball.listeners;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.locations.SignLocation;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.*;

public class PaintballSigns implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignCreate(SignChangeEvent e) {
        // paintball.sign.use
        if (e.getLines().length == 0 || !e.getLine(0).equalsIgnoreCase("pb") || e.getLine(1).contains("lb"))
            return;

        if (!e.getLine(1).equalsIgnoreCase("autojoin") && !e.getLine(1).equalsIgnoreCase("join")
                && !e.getLine(1).equalsIgnoreCase("leave") && !e.getLine(1).equalsIgnoreCase("spectate")) {
            Messenger.error(e.getPlayer(), Messages.SIGN_WRONG_SYNTAX);
            e.getBlock().breakNaturally();
            return;
        }

        String prefix = Messages.SIGN_TITLE.getString();
        // For Auto joining
        if (e.getLine(1).equalsIgnoreCase("autojoin")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.create"))
                return;
            Messenger.success(e.getPlayer(), Messages.SIGN_AUTOJOIN_CREATED);
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
                Messenger.success(e.getPlayer(), Messages.SIGN_JOIN_CREATED);
                new SignLocation(a, e.getBlock().getLocation(), SignLocation.SignLocations.JOIN);
            } else {
                e.getBlock().breakNaturally();
                return;
            }
        }

        // For leaving
        if (e.getLine(1).equalsIgnoreCase("leave")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.leave.create"))
                return;

            e.setLine(0, prefix);
            e.setLine(1, Messages.SIGN_LEAVE.getString());
            Messenger.success(e.getPlayer(), Messages.SIGN_LEAVE_CREATED);
        }

        // For spectating a specific arena
        if (e.getLine(1).equalsIgnoreCase("spectate")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.spectate.create"))
                return;

            Arena a = ArenaManager.getArenaManager().getArena(e.getLine(2));
            if (Utils.nullCheck(e.getLine(2), a, e.getPlayer())) {
                e.setLine(0, prefix);
                e.setLine(1, a.getName());
                e.setLine(2, Messages.SIGN_SPECTATE.getString());
                Messenger.success(e.getPlayer(), Messages.SIGN_SPECTATE_CREATED);
            } else {
                e.getBlock().breakNaturally();
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaTryToJoinOnClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN
                && e.getClickedBlock().getType() != Material.SIGN_POST
                && e.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();
        if (!sign.getLine(0).contains(Messages.SIGN_TITLE.getString()) || sign.getLine(1) == null) return;

        if (sign.getLine(1).equals(Messages.SIGN_LEAVE.getString())) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.leave.use"))
                return;

            Arena arena = ArenaManager.getArenaManager().getArena(player);
            if (arena == null) {
                Messenger.error(player, Messages.NOT_IN_ARENA);
                return;
            } else {
                arena.getPaintballPlayer(player).leave();
                return;
            }
        }

        if (ArenaManager.getArenaManager().getArena(player) != null) {
            Messenger.error(player, Messages.IN_ARENA);
            return;
        }

        if (sign.getLine(1).equals(GREEN + "Auto Join")) {
            if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.use"))
                return;

            Arena arenaToJoin = ArenaManager.getArenaManager().getBestArena();
            if (arenaToJoin == null) {
                Messenger.error(player, Messages.NO_ARENAS);
                return;
            }

            // In case for some reason the sign is not in the sign locations (WorldEdited?) it adds it in
            if (Settings.ARENA.getSigns().get(sign.getLocation()) == null)
                new SignLocation(sign.getLocation(), SignLocation.SignLocations.AUTOJOIN);

            arenaToJoin.joinLobby(player, null);
            return;
        } else if (sign.getLine(2).equalsIgnoreCase(Messages.SIGN_SPECTATE.getString())) {
            return;
        }

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.join.use"))
            return;

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_FOUND)
                    .replace(Tag.ARENA, sign.getLine(1)).build());
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
            Messenger.error(player, Messages.CANNOT_JOIN);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onArenaTryToSpectateClick(PlayerInteractEvent e) {
        if (!(e.getAction() == Action.RIGHT_CLICK_BLOCK) || e.getClickedBlock().getType() != Material.SIGN
                && e.getClickedBlock().getType() != Material.SIGN_POST
                && e.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        if (!(e.getClickedBlock().getState() instanceof Sign)) return;
        Sign sign = (Sign) e.getClickedBlock().getState();
        Player player = e.getPlayer();
        if (!sign.getLine(0).contains(Messages.SIGN_TITLE.getString()) || sign.getLine(1) == null) return;

        if (!sign.getLine(2).equalsIgnoreCase(Messages.SIGN_SPECTATE.getString()))
            return;

        if (!Messenger.signPermissionValidator(e.getPlayer(), "paintball.spectate.use"))
        return;

        if (ArenaManager.getArenaManager().getArena(sign.getLine(1)) == null) {
            Messenger.error(player, new MessageBuilder(Messages.ARENA_NOT_FOUND)
                    .replace(Tag.ARENA, sign.getLine(1)).build());
            return;
        }

        Arena arenaToSpectate = ArenaManager.getArenaManager().getArenas().get(sign.getLine(1));

        if (arenaToSpectate != null) {
            if (Utils.canJoinSpectate(arenaToSpectate, player))
                arenaToSpectate.joinSpectate(player);
        } else {
            Messenger.error(player, Messages.CANNOT_SPECTATE);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSignBreak(BlockBreakEvent e) {
        // Check to make sure it is a sign
        BlockState state = e.getBlock().getState();

        if (!(state instanceof Sign) && !(state instanceof Skull))
            return;

        SignLocation autoJoinOrLbsign = Settings.ARENA.getSigns().get(e.getBlock().getLocation());

        if (state instanceof Sign) {
            Sign sign = (Sign) e.getBlock().getState();

            if (autoJoinOrLbsign != null) {
                if (autoJoinOrLbsign.getType() == SignLocation.SignLocations.LEADERBOARD) {
                    if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.remove"))
                        Messenger.success(e.getPlayer(), Messages.SIGN_LEADERBOARD_REMOVED);
                } else {
                    if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.autojoin.remove"))
                        Messenger.success(e.getPlayer(), Messages.SIGN_AUTOJOIN_REMOVED);
                }
                autoJoinOrLbsign.removeSign();
            } else {
                Arena a = ArenaManager.getArenaManager().getArena(sign.getLine(1));
                if (a != null) {
                    if (sign.getLine(2).equals(Messages.SIGN_SPECTATE.getString())) {
                        if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.spectate.remove"))
                            Messenger.success(e.getPlayer(), new MessageBuilder(Messages.SIGN_SPECTATE_REMOVED)
                                    .replace(Tag.ARENA, a.getName()).build());
                    } else if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.join.remove")) {
                        a.getSignLocations().get(sign.getLocation()).removeSign();
                        Messenger.success(e.getPlayer(), new MessageBuilder(Messages.SIGN_JOIN_REMOVED)
                                .replace(Tag.ARENA, a.getName()).build());
                    }
                } else if (sign.getLine(1).equals(Messages.SIGN_LEAVE.getString())) {
                    if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.leave.remove"))
                        Messenger.success(e.getPlayer(), Messages.SIGN_LEAVE_REMOVED);
                }
            }
        } else if (state instanceof Skull) {
            if (autoJoinOrLbsign != null) {
                if (Messenger.signPermissionValidator(e.getPlayer(), "paintball.leaderboard.remove"))
                    Messenger.success(e.getPlayer(), Messages.SKULL_LEADERBOARD_REMOVED);

                autoJoinOrLbsign.removeSign();
            }
        }


    }
}
