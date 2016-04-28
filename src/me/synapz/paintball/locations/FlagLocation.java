package me.synapz.paintball.locations;

import me.synapz.paintball.arenas.FlagArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

public class FlagLocation extends PaintballLocation {

    private final String path;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public FlagLocation(FlagArena arena, Team team, Location location) {
        super(arena, location);

        path = team == null ? "Arenas." + arena.getDefaultName() + ".Neutral.Flag" : team.getPath();

        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public FlagLocation(FlagArena arena, Team team) {
        super(arena, Settings.ARENA_FILE.getString(team == null ? "Arenas." + arena.getDefaultName() + ".Neutral.Flag" : team.getPath()));

        path = team == null ? "Arenas." + arena.getDefaultName() + ".Neutral.Flag" : team.getPath();
    }

    public void removeLocation() {
        Settings.ARENA_FILE.set(path, null);
        arena.advSave();
    }

    protected void setLocation() {

        Settings.ARENA_FILE.set(path, super.toString());
        arena.advSave();
    }

}
