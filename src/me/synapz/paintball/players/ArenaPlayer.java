package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.*;


public class ArenaPlayer extends PaintballPlayer {

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);

        initPlayer();
    }

    protected String getChatLayout() {
        return ARENA_CHAT;
    }

    protected void initPlayer() {
        arena.addPlayer(this);
        player.teleport(arena.getSpawn(team));
        // TODO: openKit menu, stop from being able to move
    }
}