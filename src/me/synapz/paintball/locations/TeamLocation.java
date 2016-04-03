package me.synapz.paintball.locations;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
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
    public TeamLocation(Arena arena, Team team, TeamLocations type, int spawnNumber) {
        super(arena, Settings.ARENA_FILE.getString(team.getPath(type, spawnNumber)));
        this.team = team;
        this.type = type;
    }

    public void removeLocation() {
        Settings.ARENA_FILE.set(team.getPath(type, team.getSpawnPointsSize(type)), null);
        arena.advSave();
    }

    protected void setLocation() {
        Settings.ARENA_FILE.set(team.getPath(type, team.getSpawnPointsSize(type)+1), super.toString());
        arena.advSave();
    }
}
