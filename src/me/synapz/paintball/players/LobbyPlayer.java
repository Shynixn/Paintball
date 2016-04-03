package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import me.synapz.paintball.countdowns.ChangeTeamCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;
import static me.synapz.paintball.storage.Settings.THEME;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

public class LobbyPlayer extends PaintballPlayer {

    // TODO: Deal with 16 teams on a scoreboard

    /**
     * Creates a new arena
     * @param arena Arena the player is in
     * @param team Team the player is in
     * @param player The player the LobbyPlayer is
     */
    public LobbyPlayer(Arena arena, Team team, Player player) {
        super(arena, team, player);
    }

    /**
     * Initialize the player
     * Teleports them to a random lobby location, increments the team count, and checks to start the timer
     */
    @Override
    protected void initPlayer() {
        PLAYERDATA.savePlayerInformation(player);

        player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY))));
        team.playerJoinTeam();

        // If the arena can start (enough players) AND the lobby does not already have one started
        if (arena.canStartTimer() && LobbyCountdown.tasks.get(arena) == null) {
            new LobbyCountdown(arena.LOBBY_COUNTDOWN, arena);
        }
    }

    /**
     * Creates a PaintballScoreboard to be easily manipulated
     * @return The new PaintballScoreboard that was made
     */
    @Override
    public PaintballScoreboard createScoreboard() {
        return new PaintballScoreboard(this, arena.LOBBY_COUNTDOWN, "Lobby:")
                .addTeams(false)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName())
                .addLine(ScoreboardLine.STATUS, arena.getStateAsString())
                .build();
    }

    /**
     * Manually updates the scoreboard with the new values
     */
    @Override
    public void updateScoreboard() {
        if (pbSb == null)
            return;

        int size = arena.getArenaTeamList().size()-1;
        pbSb.reloadTeams(false)
                .reloadLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName(), size+2);

        if (arena.GIVE_TEAM_SWITCHER)
            giveItems();
    }

    /**
     * Calls super leave and removes a player from the team count
     */
    @Override
    public void leave() {
        super.leave();
        team.playerLeaveTeam(); // Also want to decrement the team size
    }

    /**
     * Sends the player join messages that include a title and chat message
     */
    @Override
    protected void showMessages() {
        arena.broadcastMessage(team.getChatColor() + player.getName() + GREEN + " has joined the arena! " + GRAY + arena.getLobbyPlayers().size() + "/" + arena.getMax());
        arena.broadcastTitle(GREEN + "Joined arena", GRAY + "" + arena.getLobbyPlayers().size() + "/" + arena.getMax(), 20, 20, 20);
        Messenger.titleMsg(player, true, GREEN + "You have joined the arena!");
    }

    /**
     * Gives the player join items including
     * Team Switcher (If true in config), armour, and wool helmet
     */
    @Override
    protected void giveItems() {
        player.getInventory().clear();

        if (!arena.GIVE_TEAM_SWITCHER)
            return;

        // For if the amount of teams are larger than 9 slots (how would they click the 10th or 11th? The -1 is because the player is on 1 team, we don't show that team
        if (arena.getArenaTeamList().size()-1 > 9) {
            // Just creates a wool item, which when you click will open a change menu
            player.getInventory().setItem(0, Utils.makeWool(team.getChatColor() + "" + ChatColor.BOLD + "Click" + Messenger.SUFFIX + ChatColor.RESET + team.getChatColor() + "Change Team", team.getDyeColor()));
            return;
        }


        List<ItemStack> items = new ArrayList<ItemStack>() {{
            for (Team t : arena.getArenaTeamList()) {
                // quick check to make sure we don't give them wool for their own team
                if (!team.getTitleName().equals(t.getTitleName())) {
                    add(Utils.makeWool(t.getChatColor() + "" + ChatColor.BOLD + "Click" + Messenger.SUFFIX + ChatColor.RESET + t.getChatColor() + "Join " + t.getTitleName(), t.getDyeColor(), t));
                }
            }
        }};

        for (ItemStack item : items) {
            int spot = items.indexOf(item);
            player.getInventory().setItem(spot, items.get(spot));
        }
        giveWoolHelmet();
        player.updateInventory();
    }

    /**
     * Changes the player's team, teleports them to new team lobby location, sends them messages
     * Refreshes their inventory, and creates a new ChangeTeamCountdown to block fast team switching
     * @param newTeam Team to switch to
     */
    public void setTeam(Team newTeam) {
        if (ChangeTeamCountdown.teamPlayers.containsKey(player.getName())) {
            Messenger.msg(player, Messenger.TEAM_SWITCH_ERROR);
            return;
        }
        team.playerLeaveTeam();
        team = newTeam;
        team.playerJoinTeam();
        Messenger.titleMsg(player, true, GREEN + "You are now on the " + team.getChatColor() + team.getTitleName() + " Team!");

        if (arena.TELEPORT_TEAM_SWITCH)
            player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY))));

        pbSb.updateNametags();
        giveItems();
        giveWoolHelmet();
        new ChangeTeamCountdown(arena.TEAM_SWITCH_COOLDOWN, player);
    }
}
