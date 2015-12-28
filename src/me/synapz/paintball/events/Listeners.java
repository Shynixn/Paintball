package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.Wool;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public class Listeners implements Listener {

    // When ever a player leaves the game, make them leave the arena so they get their stuff
    @EventHandler
    public void onArenaQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (isInArena(player)) {
            a.getAllPlayers().get(player).leaveArena();
        }
    }

    // Don't let players break blocks in arena
    @EventHandler
    public void onBlockBreakInArena(BlockBreakEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to break blocks while in the arena!"))
            e.setCancelled(true);
    }

    // Don't let players place blocks in arena
    @EventHandler
    public void onBlockPlaceInArena(BlockPlaceEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to place blocks while in the arena!"))
            e.setCancelled(true);
    }

    // Whenever a player clicks an item in an arena, handles snowballs, game switches, everything
    @EventHandler
    public void clickItemInArena(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            // TODO: check if they clicked on the team they are already on
            if (gamePlayer instanceof LobbyPlayer) {
                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (item.getItemMeta().getDisplayName().contains("Join")) { // check to make sure it is a team changing object
                        for (Team t : a.getArenaTeamList()) {
                            if (item.getItemMeta().getDisplayName().contains(t.getTitleName())) {
                                if (!t.isFull()) {
                                    lobbyPlayer.setTeam(t);
                                } else {
                                    Message.getMessenger().msg(player, true, true, ChatColor.RED + "Team " + t.getTitleName().toLowerCase() + ChatColor.RED + " is full!");
                                    break;
                                }
                            }
                        }
                        player.closeInventory();
                    } else if (e.getItem().getItemMeta().getDisplayName().contains("Click to change team")) {
                        Inventory inv = Bukkit.createInventory(null, 18, "Team Switcher");
                        for (Team t : a.getArenaTeamList()) {
                            // Make a new inventory and place all teams (except the one they are on) into it
                            if (t != lobbyPlayer.getTeam()) {
                                inv.addItem(Utils.makeWool(t.getChatColor() + t.getTitleName(), t.getDyeColor(), t));
                            }
                        }
                        player.openInventory(inv);
                    }
                }
                e.setCancelled(true);
            } else if (gamePlayer instanceof ArenaPlayer) {
                ArenaPlayer arenaPlayer = (ArenaPlayer) gamePlayer;

                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (item.getType() == Material.DOUBLE_PLANT && item.getItemMeta().getDisplayName().contains("KillCoin Shop")) {
                        arenaPlayer.giveShop();
                    } else if (item.getItemMeta().getDisplayName().contains("Paintball")) { // paintball item
                        arenaPlayer.shoot(e);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemMoveInArena(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof LobbyPlayer) {
                if (e.getInventory().getName().contains("Team Switcher")) {
                    for (Team t : a.getArenaTeamList()) {
                        if (clickedItem.getItemMeta().getDisplayName().contains(t.getTitleName())) {
                            if (!t.isFull()) {
                                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                                lobbyPlayer.setTeam(t);
                            } else {
                                Message.getMessenger().msg(player, true, true, ChatColor.RED + "Team " + t.getTitleName().toLowerCase() + ChatColor.RED + " is full!");
                            }
                            break;
                        }
                    }
                    e.setCancelled(true);
                }
            } else if (gamePlayer instanceof SpectatorPlayer) {
                Message.getMessenger().msg(player, true, ChatColor.RED, "You are not allowed to move items in your inventory!");
                e.setCancelled(true);
            } else if (gamePlayer instanceof ArenaPlayer) {
                if (clickedItem.getItemMeta().getDisplayName().contains("KillCoin Shop")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onShootItemFromInventoryInArena(PlayerDropItemEvent e) {
        Player player = (Player) e.getPlayer();

        if (isInArena(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeathInArena(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();
        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof ArenaPlayer) {
                ArenaPlayer arenaPlayer = (ArenaPlayer) gamePlayer;
                arenaPlayer.die();

                if (isInArena(killer)) {
                    ArenaPlayer arenaKiller = (ArenaPlayer) a.getPaintballPlayer(killer);
                    arenaKiller.kill();
                }
            }
        }
    }

    @EventHandler
    public void onPaintballHit(EntityDamageByEntityEvent e) {
        Player player = e.getEntity() instanceof Player ? (Player) e.getEntity() : null;
        Player hitter = e.getDamager() instanceof Player ? (Player) e.getDamager() : null;

        if (player == null || hitter == null) {
            return;
        }

        if (isInArena(player) && isInArena(hitter)) {
            Arena a = getArena(hitter);
            ArenaPlayer arenaPlayer = (ArenaPlayer) a.getPaintballPlayer(hitter);

            Settings.getSettings().getCache().incrementStat(StatType.HITS, arenaPlayer);
        }
    }

    @EventHandler
    public void onDamageAsLobbyOrSpectator(EntityDamageEvent e) {
        Player player = e.getEntity() instanceof Player ? (Player) e.getEntity() : null;
        if (player != null && isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof LobbyPlayer || gamePlayer instanceof SpectatorPlayer) {
                e.setCancelled(true);
            }
        }
    }

    // Returns true if they are in an arena, and false if they aren't, and also sends the player the error message if they are in the arena
    private boolean stopAction(Player player, String message) {
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (a != null) {
            Message.getMessenger().msg(player, true, ChatColor.RED, message);
            return true;
        }
        return false;
    }

    private boolean isInArena(Player player) {
        return getArena(player) != null;
    }

    private Arena getArena(Player player) {
        return ArenaManager.getArenaManager().getArena(player);
    }
}