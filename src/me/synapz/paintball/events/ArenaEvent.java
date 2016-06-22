package me.synapz.paintball.events;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;

/**
 * Created by Jeremy Lugo on 6/21/2016.
 */
public class ArenaEvent extends PaintballEvent {

    private PaintballPlayer paintballPlayer;
    private Arena arena;

    public ArenaEvent(PaintballPlayer paintballPlayer, Arena arena) {
        this.paintballPlayer = paintballPlayer;
        this.arena = arena;
    }

    public PaintballPlayer getPaintballPlayer() {
        return paintballPlayer;
    }

    public Arena getArena() {
        return arena;
    }
}