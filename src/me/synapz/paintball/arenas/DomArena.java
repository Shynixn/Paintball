package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class DomArena extends FlagArena {

    // A running list of all changed blocks
    Map<Location, Material> oldBlocks = new HashMap<>();

    public DomArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DOM;
    }

    @Override
    public void resetFlags() {
        for (Location loc : oldBlocks.keySet())
            loc.getBlock().setType(oldBlocks.get(loc));
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location center = getFlagLocation(team).subtract(0, 1, 0);

            for (int x = -2; x <= 2; ++x) {
                for (int z = -2; z <= 2; ++z) {
                    setGlass(center.clone().add(x, 0, z), team);
                }
            }
        }
    }

    private void setGlass(Location location, Team team) {
        Block toChange = location.getBlock();
        oldBlocks.put(location, toChange.getType());

        toChange.setType(Material.STAINED_GLASS);
        toChange.setData(team.getDyeColor().getData());
    }
}