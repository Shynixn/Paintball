package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.ArenaPlayer;

import static me.synapz.paintball.storage.Settings.SECONDARY;

public class FFAArena extends Arena {

    public FFAArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    /*
    Max will be the amount of teams so there is only 1 person per team
    */
    @Override
    public int getMax() {
        return getArenaTeamList().size();
    }

    /*
    Since it is FFA, just put them into the arena with less players
     */
    @Override
    protected Team getTeamWithLessPlayers() {
        for (Team t : getArenaTeamList()) {
            if (t.getSize() == 0)
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
}