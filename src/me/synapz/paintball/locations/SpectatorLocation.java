package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

public class SpectatorLocation extends PaintballLocation {

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public SpectatorLocation(Arena arena, Location location) {
        super(arena, location);
        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public SpectatorLocation(Arena arena) {
        super(arena, Settings.ARENA_FILE.getString(arena.getPath() + "Spectator"));
    }

    protected void setLocation() {
        Settings.ARENA_FILE.set(arena.getPath() + "Spectator", super.toString());
        arena.advSave();
    }
}
