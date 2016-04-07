package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.countdowns.ProtectionCountdown;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.*;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    // When ever a player leaves the game, make them leave the arena so they get their stuff
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (isInArena(player)) {
            a.getAllPlayers().get(player).leave();
        }
    }

    // Don't let players break blocks in arena
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakInArena(BlockBreakEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to break blocks while in the arena!"))
            e.setCancelled(true);
    }

    // Blocks commands in arena
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSendInArena(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String baseCommand = e.getMessage().split(" ")[0];

        if (!e.getMessage().contains("/") || baseCommand == null)
            return;

        if (isInArena(player)){
            Arena arena = getArena(player);

            // If the command is in blocked commands, block it. If the command is in allowed commands, return.
            if (arena.BLOCKED_COMMANDS.contains(baseCommand)){
                e.setCancelled(true);
                Messenger.error(player, "That command is disabled while in the arena.");
                return;
            } else if (arena.ALLOWED_COMMANDS.contains(baseCommand)){
                return;
            } else if (arena.ALL_PAINTBALL_COMMANDS && baseCommand.equals("/pb") || baseCommand.equals("/paintball")){
                return;
            } else if (arena.DISABLE_ALL_COMMANDS) {
                e.setCancelled(true);
                Messenger.error(player, "That command is disabled while in the arena.");
                return;
            }
        }
    }

    // Don't let players place blocks in arena
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceInArena(BlockPlaceEvent e) {
        if (stopAction(e.getPlayer(), "You are not allowed to place blocks while in the arena!"))
            e.setCancelled(true);
    }

    // Whenever a player clicks an item in an arena, handles snowballs, game switches, everything
    @EventHandler(priority = EventPriority.HIGHEST)
    public void clickItemInArena(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (!(item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()))
            return;

        String name = item.getItemMeta().getDisplayName();

        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof LobbyPlayer) {
                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (name.contains("Join")) { // check to make sure it is a team changing object
                        for (Team t : a.getArenaTeamList()) {
                            if (name.contains(t.getTitleName())) {
                                if (!t.isFull()) {
                                    lobbyPlayer.setTeam(t);
                                } else {
                                    Messenger.titleMsg(player, true, ChatColor.RED + "Team " + t.getTitleName().toLowerCase() + ChatColor.RED + " is full!");
                                    break;
                                }
                            }
                        }
                        player.closeInventory();
                    } else if (name.contains("Change Team")) {
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
                    if (name.contains("Coin Shop")) {
                        arenaPlayer.giveShop();
                        e.setCancelled(true);
                        return;
                    }
                    arenaPlayer.shoot(e);
                    e.setCancelled(true);
                }
            } else if (gamePlayer instanceof SpectatorPlayer) {
                SpectatorPlayer spectatorPlayer = (SpectatorPlayer) gamePlayer;

                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (name.equals(SpectatorPlayer.LEAVE_ARENA)) {
                        spectatorPlayer.leave();
                    } else if (name.equals(SpectatorPlayer.TELEPORTER)) {
                        spectatorPlayer.openMenu();
                    }
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemMoveInArena(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();


        if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.hasItemMeta() && !clickedItem.getItemMeta().hasDisplayName())
            return;

        String name = clickedItem.getItemMeta().getDisplayName();

        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof LobbyPlayer) {
                if (e.getInventory().getName().contains("Team Switcher")) {
                    for (Team t : a.getArenaTeamList()) {
                        if (name.contains(t.getTitleName())) {
                            if (!t.isFull()) {
                                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                                lobbyPlayer.setTeam(t);
                            } else {
                                Messenger.titleMsg(player, true, ChatColor.RED + "Team " + t.getTitleName().toLowerCase() + ChatColor.RED + " is full!");
                            }
                            break;
                        }
                    }
                    player.closeInventory();
                } else {
                    e.setCancelled(true);
                    Messenger.error(player, "You are not allowed to move items in your inventory!");
                    player.closeInventory();
                }
                e.setCancelled(true);
            } else if (gamePlayer instanceof SpectatorPlayer) {
                if (clickedItem.getType() == Material.SKULL_ITEM) {
                    String targetName = ChatColor.stripColor(name.split(" ")[4]);
                    ArenaPlayer target = (ArenaPlayer) a.getPaintballPlayer(Bukkit.getPlayer(targetName));

                    ((SpectatorPlayer) gamePlayer).spectate(target);
                } else {
                    Messenger.error(player, "You are not allowed to move items in your inventory!");
                    e.setCancelled(true);
                    player.closeInventory();
                }
                e.setCancelled(true);
            } else if (gamePlayer instanceof ArenaPlayer) {
                if (Utils.contains(clickedItem, "Team") || Utils.equals(clickedItem, ChatColor.GOLD + "Coin Shop") || player.getOpenInventory().getTitle().contains("Coin Shop")) {
                    e.setCancelled(true);
                    Messenger.error(player, "You are not allowed to move items in your inventory!");
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void lossFoodInArena(FoodLevelChangeEvent e) {
        e.setCancelled(e.getEntity() instanceof Player && isInArena((Player) e.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onShootItemFromInventoryInArena(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (isInArena(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeathInArena(PlayerDeathEvent e) {
        Player target = e.getEntity();
        if (isInArena(target)) {
            e.setDeathMessage("");
            e.setKeepInventory(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRespawnInArena(PlayerRespawnEvent e) {
        Player target = e.getPlayer();

        if (isInArena(target)) {
            Arena a = getArena(target);
            TeamLocation.TeamLocations type = a.getState() == Arena.ArenaState.WAITING ? TeamLocation.TeamLocations.LOBBY : TeamLocation.TeamLocations.SPAWN;
            Team team = a.getPaintballPlayer(target).getTeam();
            int spawnNumber = Utils.randomNumber(team.getSpawnPointsSize(type));

            Location spawnLoc = a.getLocation(type, team, spawnNumber);
            e.setRespawnLocation(spawnLoc);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
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

        if (source == null || hitBySnowball == null || !isInArena(source) || !isInArena(hitBySnowball)) // if the person who was hit by the snowball is null and the source is null and neither of them are in the arena, so cancel
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

        if (a.getState() == Arena.ArenaState.STOPPING) {
            Messenger.error(arenaPlayer.getPlayer(), "Game is already finished.");
            event.setCancelled(true);
            return;
        }

        if(ProtectionCountdown.godPlayers.keySet().contains(hitPlayerName)) {
            Messenger.error(arenaPlayer.getPlayer(), "That player is currently safe from Paintballs. Protection: " + (int) ProtectionCountdown.godPlayers.get(hitPlayerName).getCounter() + " seconds");
            event.setCancelled(true);
        } else if (ProtectionCountdown.godPlayers.keySet().contains(shooterPlayerName)) {
            Messenger.error(arenaPlayer.getPlayer(), "You cannot hit players while you are protected. Protection: " + (int) ProtectionCountdown.godPlayers.get(shooterPlayerName).getCounter() + " seconds");
            event.setCancelled(true);
        } else {
            if (hitPlayer.hit()) {
                String action = "shot";
                CoinItem clickedItem = null;

                if (arenaPlayer.getPlayer().getItemInHand() != null && !arenaPlayer.getPlayer().getItemInHand().hasItemMeta() && !arenaPlayer.getPlayer().getItemInHand().getItemMeta().hasDisplayName())
                    clickedItem = arenaPlayer.getItemWithName(arenaPlayer.getPlayer().getItemInHand().getItemMeta().getDisplayName());

                if (clickedItem != null)
                    action = clickedItem.getAction();
                arenaPlayer.kill(hitPlayer, action);
            } else {
                arenaPlayer.incrementHits();
                Messenger.error(arenaPlayer.getPlayer(), Settings.THEME + "Hit player! " + hitPlayer.getHealth() + "/" + arenaPlayer.getArena().HITS_TO_KILL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickBannerInArena(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!isInArena(player))
            return;

        Arena arena = getArena(player);

        if (arena == null)
            return;

        if (!(arena instanceof CTFArena))
            return;

        PaintballPlayer gamePlayer = arena.getPaintballPlayer(player);
        Block clicked = e.getClickedBlock();

        if (clicked == null || !(gamePlayer instanceof CTFArenaPlayer))
            return;

        CTFArenaPlayer ctfPlayer = (CTFArenaPlayer) arena.getPaintballPlayer(player);

        Location clickedLoc = new Location(clicked.getWorld(), clicked.getX(), clicked.getY(), clicked.getZ());

        boolean inFile = ((CTFArena) arena).getDropedFlagLocations().containsKey(clickedLoc);
        Team clickedFlag = null;

        // If it is inside the dropFlagLocation, just get it out
        if (inFile) {
            clickedFlag = ((CTFArena) arena).getDropedFlagLocations().get(clickedLoc);
        } else {
            // Otherwise check if the banner is in one of the set flag locations
            for (Team team : arena.getArenaTeamList()){
                Location flagLoc = ((CTFArena) arena).getFlagLocation(team);

                if (flagLoc.getBlockX() == clickedLoc.getBlockX()
                        && flagLoc.getBlockY() == clickedLoc.getBlockY()
                        && flagLoc.getBlockZ() == clickedLoc.getBlockZ()) {
                    inFile = true;
                    clickedFlag = team;
                }
            }
        }

        if (inFile) {
            if (clickedFlag == ctfPlayer.getTeam()) {
                if (((CTFArena) arena).getDropedFlagLocations().containsKey(clickedLoc)) {
                    clickedLoc.getBlock().setType(Material.AIR);

                    Location resetLoc = new FlagLocation((CTFArena) ctfPlayer.getArena(), clickedFlag).getLocation();

                    Utils.createFlag(ctfPlayer.getTeam(), resetLoc);

                    arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + ctfPlayer.getPlayer().getName() + " has reset " + ctfPlayer.getTeam().getTitleName() + "'s flag!");
                } else {
                    Messenger.error(player, "You cannot pickup your own team's flag!");
                }
                return;
            } else {
                if (ctfPlayer.isFlagHolder())
                    ctfPlayer.dropFlag();

                ctfPlayer.pickupFlag(clickedLoc, clickedFlag);
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageAsLobbyOrSpectator(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player))
            return;

        Player player = (Player) e.getEntity();

        if (isInArena(player)) {
            Arena arena = getArena(player);
            PaintballPlayer pbPlayer = arena.getPaintballPlayer(player);

            // If the player is a ArenaPlayer, and the damage was not from a snowball and the attacker is not a player, cancel.
            if (arena.getAllArenaPlayers().contains(pbPlayer) && e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE)
                e.setCancelled(true);

            // If the player is a LobbyPlayer or Spectator player, cancel all damage.
            if (arena.getLobbyPlayers().contains(pbPlayer) || arena.getSpectators().contains(pbPlayer))
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMoveInArena(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = getArena(player);

        if (arena == null)
            return;

        if (player.getLocation().getBlockY() <= -1 && isInArena(player)) {
            PaintballPlayer gamePlayer = arena.getPaintballPlayer(player);
            Team team = gamePlayer.getTeam();

            if (gamePlayer instanceof LobbyPlayer) {
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY))));
            } else if (gamePlayer instanceof ArenaPlayer) {
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
            } else if (gamePlayer instanceof SpectatorPlayer) {
                player.teleport(arena.getSpectatorLocation());
            }
        } else {
            if (isInArena(player)) {
                PaintballPlayer gamePlayer = arena.getPaintballPlayer(player);
                // Check to see if they went over their area to drop the flag
                if (arena instanceof CTFArena && gamePlayer instanceof CTFArenaPlayer) {
                    CTFArenaPlayer ctfPlayer = (CTFArenaPlayer) arena.getPaintballPlayer(player);

                    if (ctfPlayer.isFlagHolder() && ((CTFArena) arena).getFlagLocation(ctfPlayer.getTeam()).distance(player.getLocation()) <= 2)
                        ctfPlayer.scoreFlag();
                }
            }
        }
    }

    // Returns true if they are in an arena, and false if they aren't, and also sends the player the error message if they are in the arena
    private boolean stopAction(Player player, String message) {
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (a != null) {
            Messenger.error(player, message);
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