package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;

public class FFAArena extends Arena {

    public FFAArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    /*
    Max will be the amount of teams so there is only 1 person per team
    */
    @Override
    public int getMax() {
        return getFullTeamList().size();
    }

    /*
    Since it is FFA, just put them into the arena with less players
     */
    @Override
    protected Team getTeamWithLessPlayers() {
        for (Team t : getActiveArenaTeamList()) {
            if (t.getSize() <= 0) // for some reason these numbers are negative, so it checks for under 0
                return t;
        }
        return null;
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

    @Override
    public void balanceTeams() {
        // balancing teams does not need to be performed on FFA
    }
}