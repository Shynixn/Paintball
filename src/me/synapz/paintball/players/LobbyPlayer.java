package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.countdowns.ChangeTeamCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;
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
                .addTeams(true)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName())
                .addLine(ScoreboardLine.STATUS, arena.getStateAsString())
                .addLine(ScoreboardLine.MODE, arena.getArenaType().getShortName().toUpperCase())
                .addLine(ScoreboardLine.PLAYERS, arena.getLobbyPlayers().size() + Settings.SECONDARY + "/" + Settings.THEME + arena.getMax())
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
        pbSb.reloadTeams(true)
                .reloadLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName(), size+2)
                .reloadLine(ScoreboardLine.PLAYERS, arena.getLobbyPlayers().size() + Settings.SECONDARY + "/" + Settings.THEME + arena.getMax(), size+5);

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
        arena.broadcastMessage(new MessageBuilder(Messages.ARENA_JOIN_MESSAGE).replace(Tag.TEAM_COLOR, team.getChatColor().toString()).replace(Tag.SENDER, player.getName()).replace(Tag.AMOUNT, arena.getLobbyPlayers().size() + "").replace(Tag.MAX, arena.getMax() + "").build());
        arena.broadcastTitle(Messages.ARENA_JOINED.getString(), new MessageBuilder(Messages.ARENA_SIZE).replace(Tag.AMOUNT, arena.getLobbyPlayers().size() + "").replace(Tag.MAX, arena.getMax() + "").build(), 20, 20, 20);
        Messenger.titleMsg(player, true, Messages.ARENA_YOU_JOINED.getString());
    }

    /**
     * Gives the player join items including
     * Team Switcher (If true in config), armour, and wool helmet
     */
    @Override
    protected void giveItems() {
        player.getInventory().clear();

        player.getInventory().setItem(8, Utils.makeItem(Material.BED, Messages.ITEM_LEAVE_ARENA.getString(), 1));

        if (!arena.GIVE_TEAM_SWITCHER)
            return;

        // For if the amount of teams are larger than 9 slots (how would they click the 10th or 11th? The -1 is because the player is on 1 team, we don't show that team
        if (arena.getArenaTeamList().size()-1 > 8) {
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

        if (arena.LOBBY_WOOL_HELMET)
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
        Messenger.titleMsg(player, true, new MessageBuilder(Messages.ARENA_TEAM_CHANGE).replace(Tag.TEAM_COLOR, team.getChatColor().toString()).replace(Tag.TEAM, team.getTitleName()).build());
        updateDisplayName();

        if (arena.TELEPORT_TEAM_SWITCH)
            player.teleport(arena.getLocation(TeamLocation.TeamLocations.LOBBY, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.LOBBY))));

        pbSb.updateNametags();
        giveItems();

        if (arena.LOBBY_WOOL_HELMET)
            giveWoolHelmet();
        new ChangeTeamCountdown(arena.TEAM_SWITCH_COOLDOWN, player);
    }
}
