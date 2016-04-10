package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.Team;
import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.ARENA_FILE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class CTFArena extends FlagArena {

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
            Utils.createFlag(team, loc);
        }
    }

    @Override
    public void resetFlags() {
        // Turns all start flag locations to air
        for (Team team : getArenaTeamList()) {
            Location loc;

            if (Settings.ARENA_FILE.getString(team.getPath()) != null) {
                loc = new FlagLocation(this, team).getLocation();

                loc.getBlock().setType(Material.AIR);
            }
        }

        // Turns all pickedup flag locations to air
        for (Location loc : getDropedFlagLocations().keySet())
            loc.getBlock().setType(Material.AIR);
    }

    public Map<Location, Team> getDropedFlagLocations() {
        return dropedFlagLocations;
    }

    public void addFlagLocation(Location loc, Team team) {
        loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        dropedFlagLocations.put(loc, team);
    }

    public void remFlagLocation(Location loc) {
        loc = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        dropedFlagLocations.remove(loc);
    }
}
