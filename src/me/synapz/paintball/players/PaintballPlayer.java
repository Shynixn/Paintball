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

    protected Arena arena;
    protected Player player;
    protected Team team;

    public PaintballPlayer(Arena a, Team t, Player p) {
        this.arena = a;
        this.team = t;
        this.player = p;

        initPlayer();
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    protected abstract String getChatLayout();

    protected abstract void initPlayer();

    protected void giveWoolHelmet() {
        if (!Settings.WOOL_HELMET)
            return;
        player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + " Team", team.getDyeColor()));
    }

    public void chat(String message) {
        String chat = getChatLayout();

        chat = chat.replace("%TEAMNAME%", team.getTitleName());
        chat = chat.replace("%TEAMCOLOR%", team.getChatColor() + "");
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());
        for (PaintballPlayer pbPlayer : arena.getAllPlayers().values()) {
            pbPlayer.getPlayer().sendMessage(chat);
        }
    }

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
