package me.synapz.paintball;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.ARENA_FILE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class CTFArena extends Arena {

    private Map<Location, Team> dropedFlagLocations = new HashMap<Location, Team>();

    public CTFArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.CTF;
    }

    public void setFlagLocation(Team team, Location loc) {
        new FlagLocation(this, team, loc);
    }

    public Location getFlagLocation(Team team) {
        return new FlagLocation(this, team).getLocation();
    }

    @Override
    public String getSteps() {
        ChatColor done = STRIKETHROUGH;
        String end = RESET + "" + GRAY;
        StringBuilder steps = new StringBuilder(super.getSteps());

        // If the arena is already done, there is nothing to append
        if (steps.toString().equals(Settings.PREFIX + GRAY + "Complete. Arena is open!"))
            return steps.toString();

        for (Team t : getArenaTeamList()) {
            String lobbyName = t.getTitleName().toLowerCase().replace(" ", "") + " (flag)";

            steps.append(", ");
            steps.append(Settings.ARENA_FILE.getString(t.getPath()) != null ? done + lobbyName + end : lobbyName);
        }

        return steps.toString();
    }

    @Override
    public void forceLeaveArena() {
        super.forceLeaveArena();

        // Turns all start flag locations to air
        for (Team team : getArenaTeamList()) {
            Location loc = new FlagLocation(this, team).getLocation();

            loc.getBlock().setType(Material.AIR);
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
