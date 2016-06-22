package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.players.ArenaPlayer;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class ArenaEvent extends PaintballEvent{

    private ArenaPlayer arenaPlayer;
    private Arena arena;

    public ArenaEvent(ArenaPlayer arenaPlayer, Arena arena) {
        this.arenaPlayer = arenaPlayer;
        this.arena = arena;
    }

    public ArenaPlayer getArenaPlayer() {
        return arenaPlayer;
    }

    public Arena getArena() {
        return arena;
    }
}
