package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class SFGArena extends Arena {

    public SFGArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.SFG;
    }
}
