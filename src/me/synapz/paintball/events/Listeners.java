package me.synapz.paintball.events;

import me.synapz.paintball.*;
import me.synapz.paintball.countdowns.GameFinishCountdown;
import me.synapz.paintball.countdowns.NoHitCountdown;
import me.synapz.paintball.countdowns.PaintballCountdown;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.LobbyPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.Wool;
import org.bukkit.projectiles.ProjectileSource;

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
                    if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().contains("Join")) { // check to make sure it is a team changing object
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
                    } else if (e.getItem() != null && e.getItem().hasItemMeta() && e.getItem().getItemMeta().getDisplayName().contains("Click to change team")) {
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
                    if (item != null && item.getType() == Material.DOUBLE_PLANT && item.getItemMeta().getDisplayName().contains("KillCoin Shop")) {
                        arenaPlayer.giveShop();
                        e.setCancelled(true);
                        return;
                    }
                    arenaPlayer.shoot(e); // paintball item
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
                        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().contains(t.getTitleName())) {
                            if (!t.isFull()) {
                                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                                lobbyPlayer.setTeam(t);
                            } else {
                                Message.getMessenger().msg(player, true, true, ChatColor.RED + "Team " + t.getTitleName().toLowerCase() + ChatColor.RED + " is full!");
                            }
                            break;
                        }
                    }
                    player.closeInventory();
                    e.setCancelled(true);
                }
            } else if (gamePlayer instanceof SpectatorPlayer) {
                Message.getMessenger().msg(player, true, ChatColor.RED, "You are not allowed to move items in your inventory!");
                player.closeInventory();
                e.setCancelled(true);
            } else if (gamePlayer instanceof ArenaPlayer) {
                if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().getDisplayName().contains("KillCoin Shop")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onShootItemFromInventoryInArena(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (isInArena(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeathInArena(PlayerDeathEvent e) {
        Player target = e.getEntity();
        Player source = target.getKiller();
        if (isInArena(target) && isInArena(source)) {
                e.setDeathMessage("");
        }
    }


    @EventHandler
    public void onRespawnInArena(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        if (isInArena(player)) {
            Arena arena = getArena(player);
            ArenaPlayer arenaPlayer = arena.getPaintballPlayer(player) instanceof ArenaPlayer ? (ArenaPlayer) arena.getPaintballPlayer(player) : null;

            if (arenaPlayer == null)
                return;

            e.setRespawnLocation(arena.getLocation(TeamLocation.TeamLocations.SPAWN, arenaPlayer.getTeam()));
            new NoHitCountdown(arena.SAFE_TIME, arenaPlayer);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // TODO: add check to make sure to disallow friendly fire!
        Snowball snowball = event.getDamager() instanceof Snowball ? (Snowball) event.getDamager() : null;

        Player hitBySnowball = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;

        if (hitBySnowball == null)
            return;

        if (snowball == null || snowball.getShooter() == null) { // if they are hitting and in an arena cancel it
            if (isInArena(hitBySnowball)) {
                event.setCancelled(true);
            }
            return;
        }

        Player source = snowball.getShooter() instanceof Player ? (Player) snowball.getShooter() : null;

        if (source == null || !(isInArena(source) || !isInArena(hitBySnowball))) // if the person who was hit by the snowball is null and the source is null and neither of them are in the arena, so cancel
            return;

        Arena a = getArena(source);
        ArenaPlayer arenaPlayer = a.getPaintballPlayer(source) instanceof ArenaPlayer ? (ArenaPlayer) a.getPaintballPlayer(source) : null;
        ArenaPlayer hitPlayer = a.getPaintballPlayer(hitBySnowball) instanceof ArenaPlayer ? (ArenaPlayer) a.getPaintballPlayer(hitBySnowball) : null;

        if (arenaPlayer == null || hitPlayer == null)
            return;

        if (arenaPlayer == hitPlayer) // player hit themself
            return;

        String hitPlayerName = hitPlayer.getPlayer().getName();
        String shooterPlayerName = arenaPlayer.getPlayer().getName();

        if (GameFinishCountdown.arenasFinishing.contains(a)) {
            Message.getMessenger().msg(arenaPlayer.getPlayer(), false, ChatColor.RED, "Game is already finished.");
            event.setCancelled(true);
            return;
        }

        if(NoHitCountdown.godPlayers.keySet().contains(hitPlayerName)) {
            Message.getMessenger().msg(arenaPlayer.getPlayer(), false, ChatColor.RED, "That player is currently safe from Paintballs. Protection: " + (int) NoHitCountdown.godPlayers.get(hitPlayerName).getCounter() + " seconds");
            event.setCancelled(true);
        } else if (NoHitCountdown.godPlayers.keySet().contains(shooterPlayerName)) {
            Message.getMessenger().msg(arenaPlayer.getPlayer(), false, ChatColor.RED, "You cannot hit players while you are protected. Protection: " + (int) NoHitCountdown.godPlayers.get(shooterPlayerName).getCounter() + " seconds");
            event.setCancelled(true);
        } else {
            Settings.PLAYERDATA.incrementStat(StatType.HITS, arenaPlayer);
            hitBySnowball.setHealth(0);
            hitPlayer.die();
            arenaPlayer.kill(hitPlayer);
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