package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.*;

public class DTCArena extends FlagArena implements Listener {

    private Map<Location, Team> coreLocations = new HashMap<>();
    private List<Location> locationsToReset = new ArrayList<>();

    public DTCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public void loadFlags() {
        for (Team team : getActiveArenaTeamList()) {
            Location flagLoc = Utils.simplifyLocation(getFlagLocation(team));

            coreLocations.put(flagLoc, team);
            flagLoc.getBlock().setType(Material.STAINED_CLAY);
            flagLoc.getBlock().setData(team.getDyeColor().getData());

            surround(flagLoc, Material.STAINED_GLASS_PANE, team.getDyeColor().getData());
        }
    }

    @Override
    public void resetFlags() {
        for (Location loc : locationsToReset) {
            loc.getBlock().setType(Material.AIR);
        }
    }

    public void resetFlagCore(Team team) {
        Set<Location> copyLocations = new HashSet<>(coreLocations.keySet());
        for (Location loc : copyLocations) {
            if (coreLocations.get(loc) == team) {
                loc.getBlock().setType(Material.AIR);
                coreLocations.remove(loc, team);
            }
        }
    }

    public Map<Location, Team> getCoreLocations() {
        return coreLocations;
    }

    private void surround(Location center, Material type, byte data) {
        List<Location> surroundedLocations = new ArrayList<Location>() {{
            add(center.clone().subtract(1, 0, 0)); // left
            add(center.clone().add(1, 0, 0)); // right
            add(center.clone().add(0, 1, 0)); // top
            add(center.clone().subtract(0, 1, 0)); // bottom
            add(center.clone().add(0, 0, 1)); // back
            add(center.clone().subtract(0, 0, 1)); // front
        }};

        for (Location loc : surroundedLocations) {
            loc.getBlock().setType(type);
            loc.getBlock().setData(data);
            locationsToReset.add(loc);
        }
        locationsToReset.add(center);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DTC;
    }
}
