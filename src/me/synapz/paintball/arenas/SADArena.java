package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class SADArena extends Arena {

    public SADArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.SAD;
    }
}
