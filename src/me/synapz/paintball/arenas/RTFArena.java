package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class RTFArena extends FlagArena {

    public RTFArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location loc = getFlagLocation(team).subtract(0, 1, 0);

            loc.getBlock().setType(Material.WOOL);
            Block block = loc.getBlock();

            block.setData(team.getDyeColor().getData());
        }
    }

    @Override
    public void resetFlags() {

    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.RTF;
    }
}
