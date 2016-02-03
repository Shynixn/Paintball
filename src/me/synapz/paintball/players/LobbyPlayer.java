package me.synapz.paintball.players;

import me.synapz.paintball.*;
import me.synapz.paintball.countdowns.ArenaCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

public final class LobbyPlayer extends PaintballPlayer {

    private String pastToStartString;
    private String pastTeam;
    private Map<Team, Integer> pastScores = new HashMap<>();

    public LobbyPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    // TODO: what happens with 16 teams on scoreboard?

    protected void initPlayer() {
        new LobbyCountdown(1, arena);
        PLAYERDATA.savePlayerInformation(player);
        arena.addPlayer(this);

        player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team));
        stripValues();
        giveItems();
        displayMessages();
        giveWoolHelmet();
        team.playerJoinTeam();

        if (arena.canStartTimer() && ArenaCountdown.tasks.get(arena) == null) {
            new ArenaCountdown(arena.LOBBY_COUNTDOWN, arena.LOBBY_INTERVAL, arena.LOBBY_NO_INTERVAL, arena, GREEN + "Waiting for more players. " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Waiting for more players\n" + GRAY + "%time%" + GREEN + " seconds", ChatColor.GREEN + "Teleporting into arena...", null, true);
        }
    }

    @Override
    protected void loadScoreboard() {
        super.loadScoreboard();
        pastScores = new HashMap<>();
        int playersLeftOStart = (arena.getMin() - arena.getLobbyPlayers().size());
        Objective objective = sb.getObjective(DisplaySlot.SIDEBAR);
        String toStartString = (arena.getMin() - arena.getLobbyPlayers().size()) > 0 ? Settings.SECONDARY + playersLeftOStart + Settings.THEME + " more player" + (playersLeftOStart == 1 ? "" : "s") + " to start": Settings.THEME + "Starting in " + Settings.SECONDARY + (int)ArenaCountdown.tasks.get(arena).getCounter() + " seconds!";
        String teamString = Settings.THEME + "Team: " + Settings.SECONDARY + this.team.getChatColor() + this.team.getTitleName();
        String lineString = Settings.SECONDARY +  ChatColor.STRIKETHROUGH + Utils.makeSpaces(Settings.THEME + "     Paintball " + "     ");

        Score state = objective.getScore(Settings.THEME + "Waiting");
        Score toStart = objective.getScore(toStartString);
        Score team = objective.getScore(teamString);
        Score line = objective.getScore(lineString);

        objective.setDisplayName(Settings.THEME + "     Paintball " + "     ");

        int size = 0;
        // Sets all the scores
        for (int i = size; i < arena.getArenaTeamList().size(); i++, size++) {
            Team t = (Team) arena.getArenaTeamList().toArray()[i];
            int teamSize = t.getSize();
            Score teamScore = objective.getScore(t.getChatColor() + t.getTitleName() + ": " + Settings.SECONDARY + (t.isFull() ? "Full" : teamSize));
            teamScore.setScore(size);
            pastScores.put(t, teamSize);
        }

        // TODO: set money here
        state.setScore(size+3);
        toStart.setScore(size+2);
        team.setScore(size+1);
        line.setScore(size);

        pastToStartString = toStartString;
        pastTeam = teamString;

        player.setScoreboard(sb);
    }

    public void updateScoreboard() {
        if (ArenaCountdown.tasks.get(arena) == null)
            return;

        int playersLeftOStart = (arena.getMin() - arena.getLobbyPlayers().size());
        Objective objective = sb.getObjective(DisplaySlot.SIDEBAR);

        String toStartString = (playersLeftOStart) > 0 ? Settings.SECONDARY + playersLeftOStart + Settings.THEME + " more player" + (playersLeftOStart == 1 ? "" : "s") + " to start": Settings.THEME + "Starting in " + Settings.SECONDARY + (int)ArenaCountdown.tasks.get(arena).getCounter() + " seconds!";
        String teamString = Settings.THEME + "Team: " + Settings.SECONDARY + this.team.getChatColor() + this.team.getTitleName();

        int size = 0;
            for (int i = 0; i < arena.getArenaTeamList().size(); i++, size++) {
                Team t = (Team) arena.getArenaTeamList().toArray()[i];
                int teamSize = t.getSize();
                int pastTeamSize = pastScores.get(t) == null ? 0 : pastScores.get(t);

                sb.resetScores(Bukkit.getOfflinePlayer(t.getChatColor() + t.getTitleName() + ": " + Settings.SECONDARY + (t.isFull() ? "Full" : pastTeamSize)));
                Score teamScore = objective.getScore(t.getChatColor() + t.getTitleName() + ": " + Settings.SECONDARY + (t.isFull() ? "Full" : teamSize));
                teamScore.setScore(size);
                pastScores.replace(t, pastTeamSize, teamSize);
            }

        if (size == 0) {
            size = arena.getArenaTeamList().size();
        }

        if (!pastToStartString.equals(toStartString)) {
            sb.resetScores(Bukkit.getOfflinePlayer(pastToStartString));
            Score toStart = objective.getScore(toStartString);
            toStart.setScore(size+3);
            pastToStartString = toStartString;
        }

        if (!pastTeam.equals(teamString)) {
            sb.resetScores(Bukkit.getOfflinePlayer(pastTeam));
            Score team = objective.getScore(teamString);
            team.setScore(size+2);
            pastTeam = teamString;
        }

        player.setScoreboard(sb);
    }

    public void removeScoreboard() {
        sb.getObjective(DisplaySlot.SIDEBAR).unregister();
    }

































    protected String getChatLayout() {
        return arena.ARENA_CHAT;
    }


    public void setTeam(Team newTeam) {
        team.playerLeaveTeam();
        team = newTeam;
        team.playerJoinTeam();
        Message.getMessenger().msg(player, true, true, GREEN + "You are now on the " + team.getChatColor() + team.getTitleName() + " Team!");
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team));
        giveItems();
        giveWoolHelmet();

        final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(team.getTitleName());
        playerTeam.addPlayer(player);
        updateScoreboard();
        player.setScoreboard(sb);
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
        player.updateInventory();
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
        removeScoreboard();
        team.playerLeaveTeam();
        super.leaveArena();
    }
}