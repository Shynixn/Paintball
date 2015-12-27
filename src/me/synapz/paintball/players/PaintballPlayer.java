package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

    /*
    -----
    Constructor that all the PaintballPlayer call when they are created (super();), each will perform this on create
    -----
     */
    public PaintballPlayer(Arena a, Team t, Player p) {
        this.arena = a;
        this.team = t;
        this.player = p;

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
        if (Team.getPluginScoreboard().getTeam(team.getTitleName()) != null)
            Team.getPluginScoreboard().getTeam(team.getTitleName()).removePlayer(player);
        arena.removePlayer(this);
        Settings.getSettings().getCache().restorePlayerInformation(player);

        // check to see if there is only one player left, if there is everyone else left
        if (arena.getAllArenaPlayers().size() == 1) {
            arena.getAllArenaPlayers().get(0).leaveArena(); // get the last final player and make them leave (can't play alone)
            arena.setState(Arena.ArenaState.WAITING);
        } else if (arena.getAllArenaPlayers().size() <= 0) {
            arena.setState(Arena.ArenaState.WAITING);
        }
    }
}