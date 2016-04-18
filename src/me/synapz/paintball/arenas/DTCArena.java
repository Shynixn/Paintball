package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class DTCArena extends FlagArena {

    public DTCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public void loadFlags() {

    }

    @Override
    public void resetFlags() {

    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DTC;
    }
}
