package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

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

    public abstract void chat(String message);

    protected abstract void initPlayer();

    public abstract void leaveArena();

}
