package me.synapz.paintball.listeners;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.arenas.*;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.countdowns.ProtectionCountdown;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.enums.UpdateResult;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.players.*;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.files.UUIDFile;
import me.synapz.paintball.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Listeners extends BaseListener implements Listener {

    @EventHandler
    public void onHorseDismount(EntityDismountEvent e) {
        if (e.getDismounted() instanceof Horse && e.getEntity() instanceof Player && isInArena((Player) e.getEntity())) {
            Player player = (Player) e.getEntity();
            Arena arena = getArena(player);
            PaintballPlayer pbPlayer = arena.getPaintballPlayer(player);

            if (pbPlayer instanceof ArenaPlayer)
                ((ArenaPlayer) pbPlayer).killHorse();
        }
    }

    @EventHandler
    public void onPlayerJoinCheck(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UpdateResult result = Update.getUpdater().getResult();

        if (player.hasPermission("paintball.update") && result != UpdateResult.DISABLED && !result.getMessage().isEmpty())
            Messenger.success(player, result.getMessage().replace("%new%", Update.getUpdater().getNewVersion()));

        UUIDFile uuidFile = Settings.getSettings().getPlayerDataFolder().getPlayerFile(player.getUniqueId());

        if (uuidFile != null && uuidFile.getFileConfig().getConfigurationSection("Player-Data") != null) {
            uuidFile.restorePlayerInformation(false);
        }
    }

    //When a player joins, check if they are from a bungee server and send them to the arena if they ar
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        //detect if they have been called over by bungee
        if (Paintball.getInstance().getBungeeManager().getBungeePlayers().containsKey(player.getUniqueId())) {
            //if yes, send them to their arena
            Paintball.getInstance().getBungeeManager().getBungeePlayers().get(player.getUniqueId()).joinLobby(player, null);
        }
    }

    // When ever a player leaves the game, make them leave the arena so they get their stuff
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArenaQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena a = ArenaManager.getArenaManager().getArena(player);
        if (isInArena(player)) {
            a.getAllPlayers().get(player).leaveDontSave();
        }
    }

    // Don't let players break blocks in arena
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreakInArena(BlockBreakEvent e) {
        if (stopAction(e.getPlayer(), Messages.ARENA_CANNOT_BREAK_BLOCKS.getString()))
            e.setCancelled(true);
    }

    // Blocks commands in arena
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandSendInArena(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        String baseCommand = e.getMessage().split(" ")[0].toLowerCase();

        if (!e.getMessage().contains("/") || baseCommand == null)
            return;

        if (isInArena(player)) {
            Arena arena = getArena(player);

            // If the command is in blocked commands, block it. If the command is in allowed commands, return.
            if (arena.BLOCKED_COMMANDS.contains(baseCommand)) {
                e.setCancelled(true);
                Messenger.error(player, Messages.ARENA_COMMAND_DISABLED);
                return;
            } else if (arena.ALLOWED_COMMANDS.contains(baseCommand)) {
                return;
            } else if (arena.ALL_PAINTBALL_COMMANDS && baseCommand.equals("/pb") || baseCommand.equals("/paintball")) {
                return;
            } else if (arena.DISABLE_ALL_COMMANDS) {
                e.setCancelled(true);
                Messenger.error(player, Messages.ARENA_COMMAND_DISABLED);
                return;
            }
        }
    }

    // Don't let players place blocks in arena
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceInArena(BlockPlaceEvent e) {
        if (stopAction(e.getPlayer(), Messages.ARENA_CANNOT_BREAK_BLOCKS.getString()))
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

        if (!isInArena(player)) {
            return;
        }
        Arena a = getArena(player);
        PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

        if (gamePlayer instanceof LobbyPlayer) {
            LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (name.contains("Join")) { // check to make sure it is a team changing object
                    for (Team t : a.getArenaTeamList()) {
                        if (name.contains(t.getTitleName())) {
                            if (!t.isFull()) {
                                boolean teamsBalanced = (t.getSize() == 0);

                                // Loops through all teams
                                // If there is at least one team (other than the one they are switching to) that has more than 1 player
                                // then the game is playable because it is not a 2v0.
                                // Also, if the team they are switching to is already empty, they will not need to worry about this at all
                                for (Team team : a.getArenaTeamList()) {
                                    if ((team == lobbyPlayer.getTeam() ? team.getSize()-1 : team.getSize()) > 0 && team != t && !teamsBalanced) {
                                        teamsBalanced = true;
                                    }
                                }

                                if (teamsBalanced)
                                    lobbyPlayer.setTeam(t);
                                else
                                    Messenger.error(player, Messages.ARENA_TEAMS_NOT_BALANCED);
                            } else {
                                Messenger.titleMsg(player, true, new MessageBuilder(Messages.TEAM_FULL).replace(Tag.TEAM, t.getTitleName().toLowerCase()).build());
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
                } else if (name.equals(Messages.ITEM_LEAVE_ARENA.getString())) {
                    lobbyPlayer.leave();
                }
            }
            e.setCancelled(true);
        } else if (gamePlayer instanceof ArenaPlayer) {
            ArenaPlayer arenaPlayer = (ArenaPlayer) gamePlayer;

            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (name.contains(Messages.ARENA_SHOP_NAME.getString())) {
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
                if (name.equals(Messages.ITEM_LEAVE_ARENA.getString())) {
                    spectatorPlayer.leave();
                } else if (name.equals(SpectatorPlayer.TELEPORTER)) {
                    spectatorPlayer.openMenu();
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemMoveInArena(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR || clickedItem.hasItemMeta() && !clickedItem.getItemMeta().hasDisplayName() && !isInArena(player))
            return;

        String name = clickedItem.getItemMeta().getDisplayName();

        if (isInArena(player)) {
            Arena a = getArena(player);
            PaintballPlayer gamePlayer = a.getPaintballPlayer(player);

            if (gamePlayer instanceof LobbyPlayer) {
                LobbyPlayer lobbyPlayer = (LobbyPlayer) gamePlayer;
                if (e.getInventory().getName().contains("Team Switcher")) {
                    for (Team t : a.getArenaTeamList()) {
                        if (name.contains(t.getTitleName())) {
                            if (!t.isFull()) {
                                boolean teamsBalanced = (t.getSize() == 0);

                                // Loops through all teams
                                // If there is at least one team (other than the one they are switching to) that has more than 1 player
                                // then the game is playable because it is not a 2v0.
                                // Also, if the team they are switching to is already empty, they will not need to worry about this at all
                                for (Team team : a.getArenaTeamList()) {
                                    if ((team == lobbyPlayer.getTeam() ? team.getSize()-1 : team.getSize()) > 0 && team != t && !teamsBalanced) {
                                        teamsBalanced = true;
                                    }
                                }

                                if (teamsBalanced)
                                    lobbyPlayer.setTeam(t);
                                else
                                    Messenger.error(player, Messages.ARENA_TEAMS_NOT_BALANCED);
                            } else {
                                Messenger.titleMsg(player, true, new MessageBuilder(Messages.TEAM_FULL).replace(Tag.TEAM, t.getTitleName().toLowerCase()).build());
                                break;
                            }
                            player.closeInventory();
                        }
                    }
                } else {
                    e.setCancelled(true);
                    Messenger.error(player, Messages.ARENA_MOVE_ERROR);
                    player.closeInventory();
                }
                e.setCancelled(true);
            }else if (gamePlayer instanceof SpectatorPlayer) {
                if (clickedItem.getType() == Material.SKULL_ITEM) {
                    String targetName = ChatColor.stripColor(name.split(" ")[4]);
                    ArenaPlayer target = (ArenaPlayer) a.getPaintballPlayer(Bukkit.getPlayer(targetName));

                    ((SpectatorPlayer) gamePlayer).spectate(target);
                } else {
                    Messenger.error(player, Messages.ARENA_MOVE_ERROR);
                    e.setCancelled(true);
                    player.closeInventory();
                }
                e.setCancelled(true);
            } else if (gamePlayer instanceof ArenaPlayer) {
                if (player.getOpenInventory().getTitle().contains("Horse") || Utils.contains(clickedItem, "Team") || Utils.contains(clickedItem, "Flag") || Utils.equals(clickedItem, ChatColor.GOLD + Messages.ARENA_SHOP_NAME.getString()) || player.getOpenInventory().getTitle().contains(Messages.ARENA_SHOP_NAME.getString())) {
                    e.setCancelled(true);

                    if (!player.getOpenInventory().getTitle().contains(Messages.ARENA_SHOP_NAME.getString()))
                        Messenger.error(player, Messages.ARENA_MOVE_ERROR);
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onSnowballShootCore(ProjectileHitEvent e) {
        if ((e.getEntity()).getShooter() instanceof Player && e.getEntity() instanceof Snowball && isInArena((Player) e.getEntity().getShooter())) {
            Player player = (Player) e.getEntity().getShooter();
            Arena arena = getArena(player);

            if (arena instanceof DTCArena) {
                DTCArena dtcArena = (DTCArena) arena;

                PaintballPlayer pbPlayer = arena.getPaintballPlayer(player);
                Location hitLoc = Utils.simplifyLocation(e.getEntity().getLocation()); // turns the crazy decimal location into a block location
                Team hitTeam = null;

                for (Location loc : dtcArena.getCoreLocations().keySet()) {
                    if (hitLoc.distance(loc) <= 1) {
                        hitTeam = dtcArena.getCoreLocations().get(loc);
                    }
                }

                if (pbPlayer instanceof ArenaPlayer && hitTeam != null && arena.getState() == Arena.ArenaState.IN_PROGRESS) {
                    if (hitTeam == pbPlayer.getTeam()) {
                        Messenger.error(player, "You cannot attack your own Core!");
                    } else {
                        arena.incrementTeamScore(hitTeam, false);
                        int score = arena.getTeamScore(hitTeam);

                        if (score % 5 == 0 || score == arena.MAX_SCORE)
                            arena.updateAllScoreboard();

                        if (score % 10 == 0)
                            arena.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + hitTeam.getTitleName() + "'s Core is being attacked!");

                        // Turn the team into spectator mode since their core was destroyed & reset the core
                        if (score == arena.MAX_SCORE) {
                            for (ArenaPlayer arenaPlayer : new ArrayList<>(arena.getAllArenaPlayers())) {
                                if (arenaPlayer.getTeam() == hitTeam) {
                                    Messenger.error(arenaPlayer.getPlayer(), Messages.CORE_DESTROYED);
                                    arenaPlayer.turnToSpectator();
                                } else {
                                    Messenger.success(arenaPlayer.getPlayer(), "Team " + hitTeam.getTitleName() + "'s Core has been destroyed!");
                                }
                            }

                            dtcArena.resetFlagCore(hitTeam);

                            // Checks to see if there is 1 team left, if there is that team one
                            if (dtcArena.getCoreLocations().values().size() == 1) {
                                dtcArena.win(Arrays.asList((Team) dtcArena.getCoreLocations().values().toArray()[0]));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTryToDuelWield(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (isInArena(player) && e.getRawSlot() == 45) {
            if (stopAction(player, Messages.ARENA_NO_DUEL_WIELD.getString()))
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void lossFoodInArena(FoodLevelChangeEvent e) {
        e.setCancelled(e.getEntity() instanceof Player && isInArena((Player) e.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onShootItemFromInventoryInArena(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (isInArena(player)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathInArena(PlayerDeathEvent e) {
        Player target = e.getEntity();
        if (isInArena(target)) {
            e.setDeathMessage("");
            e.setKeepInventory(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Snowball snowball = event.getDamager() instanceof Snowball ? (Snowball) event.getDamager() : null;

        Player hitBySnowball = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;

        if (event.getEntity() instanceof Horse && event.getEntity().getPassenger() instanceof Player)
            hitBySnowball = (Player) event.getEntity().getPassenger();

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

        if (arenaPlayer.getTeam() == hitPlayer.getTeam()) // player hit themself or hit a player on the same team or hit a passenger riding horse on same team
            return;

        String hitPlayerName = hitPlayer.getPlayer().getName();
        String shooterPlayerName = arenaPlayer.getPlayer().getName();

        if (a.getState() == Arena.ArenaState.STOPPING) {
            Messenger.error(arenaPlayer.getPlayer(), Messages.ARENA_IS_FINISHED.getString());
            event.setCancelled(true);
            return;
        }

        if (ProtectionCountdown.godPlayers.keySet().contains(hitPlayerName)) {
            Messenger.error(arenaPlayer.getPlayer(), new MessageBuilder(Messages.THEY_ARE_PROTECTED)
                    .replace(Tag.TIME, (int) ProtectionCountdown.godPlayers.get(hitPlayerName).getCounter() + "").build());
            event.setCancelled(true);
            return;
        } else if (ProtectionCountdown.godPlayers.keySet().contains(shooterPlayerName)) {
            // If they can stop on hit, stop the timer so they can hit
            if (arenaPlayer.getArena().STOP_PROT_ON_HIT) {
                ActionBar.sendActionBar(arenaPlayer.getPlayer(), Messenger.createPrefix("Protection") + "Cancelled");
                ProtectionCountdown.godPlayers.get(shooterPlayerName).cancel();
            } else {
                if (!ProtectionCountdown.godPlayers.containsKey(hitPlayerName)) return;
                Messenger.error(arenaPlayer.getPlayer(), new MessageBuilder(Messages.YOU_ARE_PROTECTED)
                        .replace(Tag.TIME, (int) ProtectionCountdown.godPlayers.get(hitPlayerName).getCounter() + "").build());
                event.setCancelled(true);
                return;
            }
        }

        CoinItem clickedItem = null;

        if (arenaPlayer.getPlayer().getItemInHand() != null && arenaPlayer.getPlayer().getItemInHand().hasItemMeta()
                && arenaPlayer.getPlayer().getItemInHand().getItemMeta().hasDisplayName())
            clickedItem = arenaPlayer.getItemWithName(arenaPlayer.getPlayer().getItemInHand().getItemMeta().getDisplayName());

        if (clickedItem == null)
            clickedItem = arenaPlayer.getLastClickedItem();

        if (hitPlayer.hit(arenaPlayer.getTeam(), clickedItem == null ? 1 : clickedItem.getDamage())) {
            String action = Messages.ARENA_DEFAULT_ACTION.getString();

            if (clickedItem != null)
                action = clickedItem.getAction();

            arenaPlayer.kill(hitPlayer, action);
        } else {
            arenaPlayer.incrementHits();
            Messenger.error(arenaPlayer.getPlayer(), Settings.THEME + new MessageBuilder(Messages.HIT_PLAYER)
                    .replace(Tag.AMOUNT, hitPlayer.getHealth() + "")
                    .replace(Tag.MAX, arenaPlayer.getArena().HITS_TO_KILL + "").build());
        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPickupFlag(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = getArena(player);
        Location playerLocation = player.getLocation();
        Location loc = new Location(player.getWorld(),
                playerLocation.getBlockX(),
                playerLocation.getBlockY(),
                playerLocation.getBlockZ());

        if (!isInArena(player))
            return;

        if (arena == null)
            return;

        PaintballPlayer gamePlayer = arena.getPaintballPlayer(player);
        Team gamePlayerTeam = gamePlayer.getTeam();

        switch (arena.getArenaType()) {
            case CTF:
                if (gamePlayer instanceof CTFArenaPlayer) {
                    CTFArenaPlayer ctfPlayer = (CTFArenaPlayer) gamePlayer;

                    boolean inFile = ((CTFArena) arena).getDropedFlagLocations().containsKey(loc);
                    Team teamFlagPickedUp = null;

                    // If it is inside the dropFlagLocation, just get it out
                    if (inFile) {
                        teamFlagPickedUp = ((CTFArena) arena).getDropedFlagLocations().get(loc);
                    } else {
                        // Otherwise check if the banner is in one of the set flag locations
                        for (Team team : ((CTFArena) arena).getStartFlagLocations().keySet()) {
                            Location flagLoc = ((CTFArena) arena).getStartFlagLocations().get(team);

                            if (flagLoc.getBlockX() == loc.getBlockX()
                                    && flagLoc.getBlockY() == loc.getBlockY()
                                    && flagLoc.getBlockZ() == loc.getBlockZ()) {
                                inFile = true;
                                teamFlagPickedUp = team;
                            }
                        }
                    }

                    if (inFile) {
                        if (teamFlagPickedUp == ctfPlayer.getTeam()) {
                            if (((CTFArena) arena).getDropedFlagLocations().containsKey(loc)) {
                                loc.getBlock().setType(Material.AIR);

                                Location resetLoc = new FlagLocation((CTFArena) ctfPlayer.getArena(), teamFlagPickedUp).getLocation();

                                Utils.createFlag(ctfPlayer.getTeam(), resetLoc);

                                arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + ctfPlayer.getPlayer().getName()
                                        + " has reset " + ctfPlayer.getTeam().getTitleName() + "'s flag!");

                                ((CTFArena) arena).remFlagLocation(loc);
                            }
                            return;
                        } else {
                            // Loops through all arena players
                            for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
                                // If the player moving is equal to one of the looping player's team...
                                if (arenaPlayer.getTeam() == ctfPlayer.getTeam()) {
                                    // Check if they have a flag
                                    if (((CTFArenaPlayer) arenaPlayer).getHeldFlag() != null) {
                                        return;
                                    }
                                }
                            }

                            if (!ctfPlayer.isFlagHolder())
                                ctfPlayer.pickupFlag(loc, teamFlagPickedUp);
                        }
                    }
                }

                break;
            case RTF:
                if (arena instanceof RTFArena && gamePlayer instanceof RTFArenaPlayer) {
                    RTFArenaPlayer rtfPlayer = (RTFArenaPlayer) gamePlayer;
                    RTFArena rtfArena = (RTFArena) arena;
                    Location currentLocation = rtfArena.getCurrentFlagLocation();

                    // The location clicked was the neutral banner
                    if (Utils.locEquals(Utils.simplifyLocation(currentLocation), (Utils.simplifyLocation(loc)))) {
                        rtfPlayer.pickupFlag(Utils.simplifyLocation(loc), null);
                    }
                }

                break;
            default:
                return;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHealthRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player && isInArena((Player) e.getEntity()))
            e.setCancelled(true);
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
        Location loc = player.getLocation();

        if (arena == null)
            return;

        PaintballPlayer gamePlayer = arena.getPaintballPlayer(player);
        Team team = gamePlayer.getTeam();

        if (loc.getBlockY() <= -1) {
            if (gamePlayer instanceof LobbyPlayer) {
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team,
                        Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY))));
            } else if (gamePlayer instanceof ArenaPlayer) {
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team,
                        Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
            } else if (gamePlayer instanceof SpectatorPlayer) {
                player.teleport(arena.getSpectatorLocation());
            }
        } else {
            if (isInArena(player)) {
                // Check to see if they went over their area to drop the flag
                if (arena instanceof CTFArena && gamePlayer instanceof CTFArenaPlayer) {
                    CTFArenaPlayer ctfPlayer = (CTFArenaPlayer) arena.getPaintballPlayer(player);

                    if (ctfPlayer.isFlagHolder() && ((CTFArena) arena).getStartFlagLocations()
                            .get(ctfPlayer.getTeam()).distance(player.getLocation()) <= 2) {
                        boolean flagIsHeld = false;
                        boolean flagIsDropped = ((CTFArena) arena).getDropedFlagLocations()
                                .values().contains(ctfPlayer.getTeam());

                        for (ArenaPlayer player1 : arena.getAllArenaPlayers()) {
                            CTFArenaPlayer ctfArenaPlayer = (CTFArenaPlayer) player1;

                            if (ctfArenaPlayer.isFlagHolder() && ctfArenaPlayer.getHeldFlag() != null
                                    && ctfArenaPlayer.getHeldFlag() == ctfPlayer.getTeam()) {
                                flagIsHeld = true;
                                break;
                            }
                        }

                        // Checks to make sure the dropped flag location is contains the players team
                        if (flagIsDropped || flagIsHeld)
                            ActionBar.sendActionBar(ctfPlayer.getPlayer(), ChatColor.DARK_RED + ""
                                    + ChatColor.BOLD + "Error" + Messenger.SUFFIX + ChatColor.RED
                                    + "You are missing your team's flag!");
                        else
                            ctfPlayer.scoreFlag();
                    }
                } else if (arena instanceof RTFArena && gamePlayer instanceof RTFArenaPlayer) {
                    RTFArenaPlayer rtfPlayer = (RTFArenaPlayer) arena.getPaintballPlayer(player);

                    if (rtfPlayer.isFlagHolder() && rtfPlayer.getPlayer().getLocation().distance(
                            ((RTFArena) arena).getFlagLocation(team)) <= 2) {
                        rtfPlayer.scoreFlag();
                    }
                } else if (arena instanceof DOMArena && gamePlayer instanceof DOMArenaPlayer) {
                    DOMArenaPlayer domPlayer = (DOMArenaPlayer) gamePlayer;
                    DOMArena domArena = (DOMArena) arena;
                    Location xloc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

                    if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
                        return;

                    if (domArena.getSecureLocations().containsKey(xloc)
                            && domArena.getSecureLocations().get(xloc) != domPlayer.getTeam())
                        domPlayer.setSecuring(true, domArena.getSecureLocations().get(xloc));
                    else
                        domPlayer.setSecuring(false, domArena.getSecureLocations().get(xloc));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPickupItem(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        Arena arena = getArena(player);
        ItemStack item = e.getItem().getItemStack();

        if (arena != null) {
            PaintballPlayer paintballPlayer = arena.getPaintballPlayer(player);

            if (paintballPlayer instanceof KCArenaPlayer) {
                KCArenaPlayer kcArenaPlayer = (KCArenaPlayer) paintballPlayer;
                KCArena kcArena = (KCArena) arena;

                if (item.getType() == Material.WOOL && Utils.contains(item, "Dog Tag")) {
                    if (item.getData().getData() == kcArenaPlayer.getTeam().getDyeColor().getData()) {
                        // picked up their own teams one
                        Messenger.info(player, Messages.KILL_DENIED);
                    } else {
                        Messenger.info(player, Messages.KILL_CONFIRMED);
                        kcArenaPlayer.score();
                    }
                    kcArena.removeDogTag(e.getItem());
                    e.getItem().remove();
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleportInArena(PlayerTeleportEvent e) {
        Player whoTeleported = e.getPlayer();

        if (isInArena(whoTeleported)) {
            // Even though this allows someone to teleport to a player in the arena, it stops plugins like
            // WorldGuard from blocking the teleportation events like some people have reported to me
            e.setCancelled(false);
        }
    }
}