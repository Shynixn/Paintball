package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class LTSArena extends Arena {

    public LTSArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);

        // Last Team Standing has to include elimination game play, so there must always be lives
        if (LIVES <= 0)
            LIVES = 3;
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.LTS;
    }

    // TODO: Remove scores
}