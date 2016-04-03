package me.synapz.paintball.locations;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

public class SpectatorLocation extends PaintballLocation {

    int spawnNumber;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public SpectatorLocation(Arena arena, Location location) {
        super(arena, location);
        this.spawnNumber = Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator") == null ? 1 : Settings.ARENA_FILE.getConfigurationSection(arena.getPath() + "Spectator").getValues(false).size()+1;
        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public SpectatorLocation(Arena arena, int spawnNumber) {
        super(arena, Settings.ARENA_FILE.getString(arena.getPath() + "Spectator." + spawnNumber));
        this.spawnNumber = spawnNumber;
    }

    protected void setLocation() {
        Settings.ARENA_FILE.set(arena.getPath() + "Spectator." + spawnNumber, super.toString());
        arena.advSave();
    }

    public void removeLocation() {
        Settings.ARENA_FILE.set(arena.getPath() + "Spectator." + spawnNumber, null);
        arena.advSave();
    }
}
