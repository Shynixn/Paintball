package me.synapz.paintball;

import me.synapz.paintball.locations.FlagLocation;
import org.bukkit.Location;

public class CTFArena extends Arena {

    public CTFArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    public void setFlagLocation(Team team, Location loc) {
        new FlagLocation(this, team, loc);
    }

    public Location getFlagLocation(Team team) {
        return new FlagLocation(this, team).getLocation();
    }
}
