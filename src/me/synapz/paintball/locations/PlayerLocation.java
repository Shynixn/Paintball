package me.synapz.paintball.locations;

import me.synapz.paintball.storage.files.UUIDFile;
import org.bukkit.Location;

public class PlayerLocation extends PaintballLocation {

    private final String path;
    private final UUIDFile uuidFile;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public PlayerLocation(UUIDFile uuidFile, Location location) {
        super(null, location);

        this.uuidFile = uuidFile;
        this.path = "Player-Data." + uuidFile.getUUID() + ".Location";

        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public PlayerLocation(UUIDFile uuidFile) {
        super(null, uuidFile.getFileConfig().getString("Player-Data." + uuidFile.getUUID() + ".Location"));

        this.uuidFile = uuidFile;
        this.path = "Player-Data." + uuidFile.getUUID() + ".Location";
    }

    protected void setLocation() {
        uuidFile.getFileConfig().set(path, super.toString());
    }
}
