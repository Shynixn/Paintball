package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.ARENA;

public class CTFArena extends FlagArena {

    public Sound FLAG_PICKUP;
    public Sound FLAG_DROP;
    public Sound FLAG_SCORE;

    private Map<Team, Location> startFlagLocations = new HashMap<>();
    private Map<Location, Team> dropedFlagLocations = new HashMap<Location, Team>();

    public CTFArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.CTF;
    }

    @Override
    public void loadFlags() {
        for (Team team : this.getArenaTeamList()) {
            Location loc = new FlagLocation(this, team).getLocation();

            startFlagLocations.put(team, Utils.createFlag(team, loc));
        }
    }

    @Override
    public void resetFlags() {
        // Turns all start locations to air
        for (Location loc : getStartFlagLocations().values())
                loc.getBlock().setType(Material.AIR);

        // Turns all pickedup flag locations to air
        for (Location loc : getDropedFlagLocations().keySet())
            loc.getBlock().setType(Material.AIR);

        startFlagLocations = new HashMap<>();
        dropedFlagLocations = new HashMap<>();
    }

    @Override
    public void loadConfigValues() {
        super.loadConfigValues();

        FLAG_PICKUP             = (ARENA.loadString("CTF.Flag-Pickup", this).equals("")) ? null : Utils.strToSound(ARENA.loadString("CTF.Flag-Pickup", this));
        FLAG_DROP               = (ARENA.loadString("CTF.Flag-Drop", this).equals("")) ? null : Utils.strToSound(ARENA.loadString("CTF.Flag-Pickup", this));
        FLAG_SCORE              = (ARENA.loadString("CTF.Flag-Score", this).equals("")) ? null : Utils.strToSound(ARENA.loadString("CTF.Flag-Pickup", this));
    }

    public Map<Location, Team> getDropedFlagLocations() {
        return dropedFlagLocations;
    }

    public Map<Team, Location> getStartFlagLocations() {
        return startFlagLocations;
    }

    public void addFlagLocation(Location loc, Team team) {
        loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        dropedFlagLocations.put(loc, team);
    }

    public void remFlagLocation(Location loc) {
        Team team = dropedFlagLocations.get(loc);

        loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        dropedFlagLocations.remove(loc, team);
    }
}
