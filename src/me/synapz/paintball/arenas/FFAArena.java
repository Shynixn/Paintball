package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;

public class FFAArena extends Arena {

    public FFAArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);

        // Elimination players should have only one life
        LIVES = 1;
    }

    /*
    Max will be the amount of teams so there is only 1 person per team
    */
    @Override
    public int getMax() {
        return getArenaTeamList().size();
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.FFA;
    }

    /*
    This is always going to be true since max will never have to be set but will be calculated on team count
     */
    @Override
    public boolean isMaxSet() {
        return true;
    }
}