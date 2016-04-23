package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class DTCArena extends FlagArena {

    public DTCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location flagLoc = Utils.simplifyLocation(getFlagLocation(team));

            flagLoc.getBlock().setType(Material.CLAY);
            flagLoc.getBlock().setData(team.getDyeColor().getData());
        }
    }

    @Override
    public void resetFlags() {

    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DTC;
    }
}
