package me.synapz.paintball.locations;

import me.synapz.paintball.Arena;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public final class SignLocation extends PaintballLocation {

    public enum SignLocations {
        LEADERBOARD,
        JOIN,
        AUTOJOIN;

        @Override
        public String toString() {
            return super.toString().toLowerCase().replace(super.toString().toLowerCase().toCharArray()[0], super.toString().toUpperCase().toCharArray()[0]);
        }
    }

    private final SignLocations type;

    public SignLocation(Arena a, Location loc, SignLocations type) {
        super(a, loc);
        this.type = type;

        setLocation();
    }



    public SignLocation(Arena arena, String rawLocation) {
        super(arena, rawLocation);
        this.type = SignLocations.JOIN;
    }

    public SignLocation(SignLocations type, String rawLocation) {
        super(null, rawLocation);
        this.type = type;
    }

    // Just for autojoinin and leaderboard signs, they have NO arena set to them
    public SignLocation(Location loc, SignLocations type) {
        super(null, loc);
        this.type = type;

        setLocation();
    }

    public SignLocations getType() {
        return type;
    }

    // Remove a sign location from arenas.yml
    public void removeSign() {
        String path = type == SignLocations.LEADERBOARD || type == SignLocations.AUTOJOIN ? "Signs." + type.toString() : arena.getPath() + type.toString();
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList(path);

        if (signsList == null || !(signsList.contains(super.toString()))) {
            return;
        }
        signsList.remove(super.toString());
        Settings.getSettings().getArenaFile().set(path, signsList);
        Settings.getSettings().saveArenaFile();
    }

    protected void setLocation() {
        String path = type == SignLocations.LEADERBOARD || type == SignLocations.AUTOJOIN ? "Signs." + type.toString() : arena.getPath() + type.toString();
        List<String> signsList = Settings.getSettings().getArenaFile().getStringList(path);

        if (signsList == null)
            signsList = new ArrayList<String>();
        if (signsList.contains(super.toString()))
            return;

        signsList.add(super.toString());
        Settings.getSettings().getArenaFile().set(path, signsList);
        Settings.getSettings().saveArenaFile();
    }
}
