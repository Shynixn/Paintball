package me.synapz.paintball.players;

import me.synapz.paintball.*;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

public final class LobbyPlayer extends PaintballPlayer {

    public LobbyPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    public void setTeam(Team newTeam) {
        team.playerLeaveTeam();
        team = newTeam;
        team.playerJoinTeam();
        Message.getMessenger().msg(player, true, true, GREEN + "You are now on the " + team.getChatColor() + team.getTitleName() + " Team!");
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team));
        giveItems();
        giveWoolHelmet();
    }

    protected void initPlayer() {
        PLAYERDATA.savePlayerInformation(player);
        arena.addPlayer(this);

        player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team));
        stripValues();
        giveItems();
        displayMessages();
        giveWoolHelmet();
        team.playerJoinTeam();

        if (arena.canStartTimer()) {
            Utils.countdown(arena.LOBBY_COUNTDOWN, arena.LOBBY_INTERVAL, arena.LOBBY_NO_INTERVAL, arena, GREEN + "Waiting for more players. " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Waiting for more players\n" + GRAY + "%time%" + GREEN + " seconds", ChatColor.GREEN + "Teleporting into arena...", true);
        }
    }

    protected String getChatLayout() {
        return arena.ARENA_CHAT;
    }

    private void giveItems() {
        player.getInventory().clear();

        // For if the amount of teams are larger than 9 slots (how would they click the 10th or 11th? The -1 is because the player is on 1 team, we don't show that team
        if (arena.getArenaTeamList().size()-1 > 9) {
            // Just creates a wool item, which when you click will open a change menu
            // TODO: make inventory click events for this
            player.getInventory().setItem(0, Utils.makeWool(SECONDARY + ">> " + THEME + "Click to change team" + SECONDARY + " <<", team.getDyeColor()));
            return;
        }


        List<ItemStack> items = new ArrayList<ItemStack>() {{
            for (Team t : arena.getArenaTeamList()) {
                // quick check to make sure we don't give them wool for their own team
                if (!team.getTitleName().equals(t.getTitleName())) {
                    add(Utils.makeWool(t.getChatColor() + "Join " + t.getTitleName(), t.getDyeColor(), t));
                }
            }
        }};

        for (ItemStack item : items) {
            int spot = items.indexOf(item);
            player.getInventory().setItem(spot, items.get(spot));
        }
    }

    private void displayMessages() {
        arena.broadcastMessage(GREEN, team.getChatColor() + player.getName() + GREEN + " has joined the arena! " + GRAY + arena.getLobbyPlayers().size() + "/" + arena.getMax(), GREEN + "Joined arena " + GRAY + arena.getLobbyPlayers().size() + "/" + arena.getMax());
        Message.getMessenger().msg(player, true, true, GREEN + "You have joined the arena!");
    }

    private void stripValues() {
        // todo: exp saves
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
    }

    @Override
    public void leaveArena() {
        team.playerLeaveTeam();
        super.leaveArena();
    }
}