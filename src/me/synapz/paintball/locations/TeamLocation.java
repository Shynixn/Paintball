package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import org.bukkit.Location;

public class TeamLocation extends PaintballLocation {

    public enum TeamLocations {
        LOBBY,
        SPAWN;

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
        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public TeamLocation(Arena arena, Team team, TeamLocations type) {
        super(arena, FILE.getString(team.getPath(type)));
        this.team = team;
        this.type = type;
    }

    protected void setLocation() {
        FILE.set(team.getPath(type), super.toString());
        arena.advSave();
    }
}