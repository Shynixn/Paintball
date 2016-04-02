package me.synapz.paintball.locations;

import me.synapz.paintball.CTFArena;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

public class FlagLocation extends PaintballLocation {

    private final Team team;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public FlagLocation(CTFArena arena, Team team, Location location) {
        super(arena, location);
        this.team = team;

        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public FlagLocation(CTFArena arena, Team team) {
        super(arena, Settings.ARENA_FILE.getString(team.getPath()));

        this.team = team;
    }

    public void removeLocation() {
        Settings.ARENA_FILE.set(team.getPath(), null);
        arena.advSave();
    }

    protected void setLocation() {
        Settings.ARENA_FILE.set(team.getPath(), super.toString());
        arena.advSave();
    }

}
