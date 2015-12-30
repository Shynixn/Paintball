package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PaintballLocation {

    // Important class to shorten files by shorting
    // Turns Locations into: world,x,y,z,yaw,pitch

    protected static final FileConfiguration FILE = Settings.getSettings().getArenaFile();

    protected final Arena arena;
    protected final Location loc;

    // Just creates a PaintballLocation
    public PaintballLocation(Arena a, Location loc) {
        this.arena = a;
        this.loc = loc;
    }

    // Creates a PaintballLocation from a path (Does not set anything in arenas.yml, just gets it)
    public PaintballLocation(Arena a, String locationFromFile) {
        String[] rawLocation = locationFromFile.split(",");
        // TODO: replace world with rawLocation[0]
        Location location = new Location(Bukkit.getWorld("world"), Double.parseDouble(rawLocation[1]), Double.parseDouble(rawLocation[2]), Double.parseDouble(rawLocation[3]), Float.parseFloat(rawLocation[4]), Float.parseFloat(rawLocation[5]));
        this.arena = a;
        this.loc = location;
    }


    public Location getLocation() {
        return loc;
    }

    public Arena getArena() {
        return arena;
    }

    @Override
    public String toString() {
        // Override for sign TODO
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
    }

    protected abstract void setLocation();
}