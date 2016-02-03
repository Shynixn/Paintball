package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.PREFIX;
import static me.synapz.paintball.storage.Settings.getSettings;

public abstract class PaintballPlayer {

    /*
    -----
    Variables that will be created and set for all PaintballPlayer types
    -----
     */
    protected Arena arena;
    protected Player player;
    protected Team team;
    protected Scoreboard sb;

    /*
    -----
    Constructor that all the PaintballPlayer call when they are created (super();), each will perform this on create
    -----
     */
    public PaintballPlayer(Arena a, Team t, Player p) {
        this.arena = a;
        this.team = t;
        this.player = p;

        loadScoreboard();
        initPlayer();
    }

    /*
    -----
    Methods all PaintballPlayer objects must implement, each object type will perform different tasks in these methods
    -----
     */
    protected abstract String getChatLayout();

    protected abstract void initPlayer();

    /*
    -----
    Methods all the PaintballPlayer objects inherit
    -----
     */

    // Gets the team the player is on
    public Team getTeam() {
        return team;
    }

    // Gets the player the PaintballPlayer is connected to
    public Player getPlayer() {
        return player;
    }

    // Gets the arena the player is on
    public Arena getArena() {
        return arena;
    }
    // Gives the player a wool helmet based on their team
    protected void giveWoolHelmet() {
        // TODO: is this even a good setting?? ,-,
        //if (!Settings.WOOL_HELMET)
          //  return;
        player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + " Team", team.getDyeColor()));
    }

    // Formats a message with the config, then sends a chat message to all
    public void chat(String message) {
        // TODO: check if chat is enabled in Settings, and then return
        String chat = getChatLayout();

        chat = chat.replace("%TEAMNAME%", team.getTitleName());
        chat = chat.replace("%TEAMCOLOR%", team.getChatColor() + "");
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());
        for (Player player : arena.getAllPlayers().keySet()) {
            player.sendMessage(chat);
        }
    }

    // Leaves an arena (removes their color names, restores information, removes from lists, and checks to see if to force stop (1 player left)
    public void leaveArena() {
        forceLeaveArena();
        arena.removePlayer(this);

        // check to see if there is only one player left, if there is everyone else left
        if (arena.getAllArenaPlayers().size() == 1) {
            arena.getAllArenaPlayers().get(0).leaveArena(); // get the last final player and make them leave (can't play alone)
            arena.setState(Arena.ArenaState.WAITING);
            arena.resetTeamScores();
        } else if (arena.getAllArenaPlayers().size() <= 0) {
            arena.setState(Arena.ArenaState.WAITING);
            arena.resetTeamScores();
        }
    }

    public void forceLeaveArena() {
        if (Team.getPluginScoreboard().getTeam(team.getTitleName()) != null)
            Team.getPluginScoreboard().getTeam(team.getTitleName()).removePlayer(player);
        Settings.PLAYERDATA.restorePlayerInformation(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    protected void loadScoreboard() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();
        org.bukkit.scoreboard.Team playerTeam = null;

        // Registers all teams with their color. Used for changing color of name tag
        for (Team team : arena.getArenaTeamList()) {
            sb.registerNewTeam(team.getTitleName()).setPrefix(team.getChatColor() + "");
            playerTeam = sb.getTeam(team.getTitleName());
            playerTeam.setAllowFriendlyFire(false);
        }
        playerTeam.addPlayer(player);
        // Everything having to do with the SIDEBAR Objective
        StringBuilder objName = new StringBuilder(player.getName());

        if (this instanceof LobbyPlayer)
            objName.append("Lobby");
        else if (this instanceof ArenaPlayer)
            objName.append("Arena");
        else if (this instanceof SpectatorPlayer)
            objName.append("Spectator");

        Objective objective = sb.registerNewObjective(objName.toString(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(sb);
    }
}