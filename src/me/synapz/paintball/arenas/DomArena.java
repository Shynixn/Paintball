package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DomArena extends FlagArena {

    public int SECURE_TIME;

    // A running list of all changed blocks
    private Map<Location, Material> oldBlocks = new HashMap<>();
    private Map<Location, Team> secureLocations = new HashMap<>();
    private Map<Location, Location> centerLoc = new HashMap<>();
    private Map<Team, Integer> runningScore = new HashMap<>();

    public DomArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DOM;
    }

    @Override
    public void resetFlags() {
        for (Location loc : oldBlocks.keySet()) {
            if (oldBlocks.get(loc) != null && loc.getBlock() != null)
                loc.getBlock().setType(oldBlocks.get(loc));
        }

        oldBlocks = new HashMap<>();
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location center = getFlagLocation(team).subtract(0, 1, 0);

            List<Location> secLoc = makePlatform(center.clone(), Material.STAINED_GLASS, team.getDyeColor().getData(), 2, Material.STAINED_CLAY);
            makePlatform(center.clone().subtract(0, 1, 0), Material.STAINED_CLAY, team.getDyeColor().getData(), 2, null);
            removeAbove(center.clone());

            // Makes iron under beacon to turn it on
            makePlatform(center.clone().subtract(0, 2, 0), Material.IRON_BLOCK, (byte) 0, 1, null);

            // Turns the lower block to a beacon
            setBlock(center.clone().subtract(0, 1, 0), Material.BEACON, (byte) 0);

            for (Location loc : secLoc) {
                secureLocations.put(Utils.simplifyLocation(loc), team);
                centerLoc.put(Utils.simplifyLocation(loc), Utils.simplifyLocation(center));
            }

            runningScore.put(team, 1);
        }
    }

    @Override
    public void loadConfigValues() {
        super.loadConfigValues();

        SECURE_TIME         = Settings.ARENA.loadInt("DOM.secure-time", this);
    }

    public void teamSecured(Location loc, Team team) {
        Location center = centerLoc.get(loc);
        Team pastTeam = secureLocations.get(loc);

        List<Location> secLoc = makePlatform(center.clone(), Material.STAINED_GLASS, team.getDyeColor().getData(), 2, Material.STAINED_CLAY);
        makePlatform(center.clone().subtract(0, 1, 0), Material.STAINED_CLAY, team.getDyeColor().getData(), 2, null);
        removeAbove(center.clone());

        // Makes iron under beacon to turn it on
        makePlatform(center.clone().subtract(0, 2, 0), Material.IRON_BLOCK, (byte) 0, 1, null);

        // Turns the lower block to a beacon
        setBlock(center.clone().subtract(0, 1, 0), Material.BEACON, (byte) 0);

        for (Location locIt : secLoc) {
            secureLocations.replace(Utils.simplifyLocation(locIt), pastTeam, team);
        }

        int pastScore = runningScore.get(pastTeam);
        int newPastScore = runningScore.get(team);

        runningScore.remove(pastTeam);
        runningScore.remove(team);

        runningScore.put(pastTeam, --pastScore);
        runningScore.put(team, ++newPastScore);
    }

    public Map<Location, Team> getSecureLocations() {
        return secureLocations;
    }

    public Map<Team, Integer> getRunningScores() {
        return runningScore;
    }




    private void setBlock(Location loc, Material material, byte data) {
        loc = Utils.simplifyLocation(loc);

        if (!oldBlocks.containsKey(loc))
            oldBlocks.put(loc, loc.getBlock().getType());

        loc.getBlock().setType(material);

        if (data != 0)
            loc.getBlock().setData(data);
    }

    private List<Location> makePlatform(Location center, Material material, byte data, int radius, Material border) {
        List<Location> secLoc = new ArrayList<>();

        if (border != null)
            radius++;

        // Makes the square platform under the other one
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                Location point = center.clone().add(x, 0, z);

                if (border != null && (Math.abs(x) == radius || Math.abs(z) == radius)) {
                    setBlock(point, border, data);
                } else {
                    setBlock(point, material, data);
                    secLoc.add(point);
                    secLoc.add(point.add(0, 1, 0));
                }
            }
        }

        return secLoc;
    }

    private void removeAbove(Location center) {
        // Sets everything above the center to AIR so the beacon turns on
        for (int y = 1; y <= 240; ++y) {
            Block toChange = center.clone().add(0, y, 0).getBlock();
            if (toChange != null && toChange.getType() != Material.AIR);
                setBlock(toChange.getLocation(), Material.AIR, (byte) 0);
        }
    }
}