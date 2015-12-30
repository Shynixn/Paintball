package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

import java.util.Arrays;

public class TeamLocation extends PaintballLocation {

    public enum TeamLocations {
        LOBBY,
        SPAWN,
        SPECTATOR;

        @Override
        public String toString() {
            // turns LOBBY into Lobby
            return super.toString().toLowerCase().replace(super.toString().toLowerCase().toCharArray()[0], super.toString().toUpperCase().toCharArray()[0]);
        }
    }

    private final Team team;
    private final TeamLocations type;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public TeamLocation(Arena arena, Team team, Location location, TeamLocations type) {
        super(arena, location);
        this.team = team;
        this.type = type;

        setTeamLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public TeamLocation(Arena arena, Team team, TeamLocations type) {
        super(arena, Settings.getSettings().getArenaFile().getString(arena.getPath() + type.toString() + "." + team.getTitleName()));
        this.team = team;
        this.type = type;
    }

    private void setTeamLocation() {
        String path = team == null ? "" : "." + team.getTitleName();
        FILE.set(arena.getPath() + type.toString() + path, super.toString());
        arena.advSave();
    }

}
