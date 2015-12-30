package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class SignLocation extends PaintballLocation {

    public enum SignLocations {
        LEADERBOARD,
        JOIN,
        AUTOJOIN;
    }

    private final SignLocations type;

    // Syntax for sign location is type,arena,world,x,y,z,yaw,pitch
    public SignLocation(Arena a, Location loc, SignLocations type) {
        super(a, loc);
        this.type = type;

        setSignLocation();
    }

    // Just for autojoining signs, they have NO arena set to them
    public SignLocation(Location loc) {
        super(null, loc);
        this.type = SignLocations.AUTOJOIN;
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public SignLocation(Arena arena, String rawLocation) {
        super(arena, rawLocation);
        String rawType = rawLocation.split(",")[0];
        this.type = rawType.equals("join") ? SignLocations.JOIN : SignLocations.LEADERBOARD;
    }



    public SignLocations getType() {
        return type;
    }

    // Remove a sign location from arenas.yml
    public void removeSign() {
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList(arena.getPath() + "Sign-Locs");
        String locString = type.toString() + "," + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getPitch() + "," + loc.getYaw();
        if (signsList == null || !(signsList.contains(locString))) {
            return;
        }
        signsList.remove(locString);
        Settings.getSettings().getArenaFile().set(arena.getPath() + "Sign-Locs", signsList);
        Settings.getSettings().saveArenaFile();
    }

    private void setSignLocation() {
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList("Sign-Locs");
        String locString = type.toString().toLowerCase() + "," + loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "," + loc.getPitch() + "," + loc.getYaw();
        if (signsList == null)
            signsList = new ArrayList<String>();
        if (signsList.contains(locString)) return;
        signsList.add(locString);
        Settings.getSettings().getArenaFile().set("Sign-Locs", signsList);
        Settings.getSettings().saveArenaFile();
    }


}
