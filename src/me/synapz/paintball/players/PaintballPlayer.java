package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;

import static me.synapz.paintball.storage.Settings.PREFIX;

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
        arena.addPlayer(this);
        initPlayer();
        loadScoreboard();

        // update the scoreboard now that they are added
        arena.updateSigns();
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
        player.updateInventory();
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
        forceLeaveArena(); // puts items back (no messages)
    }

    public void forceLeaveArena() {
        arena.removePlayer(this); // removes player from all array lists
        Settings.PLAYERDATA.restorePlayerInformation(player);
        arena.updateSigns();
    }

    protected void loadScoreboard() {
        sb = Bukkit.getScoreboardManager().getNewScoreboard();

        if (this instanceof ScoreboardPlayer)
            ((ScoreboardPlayer) this).createScoreboard();
    }
}