package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class KCArena extends Arena {

    public KCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.KC;
    }
}
