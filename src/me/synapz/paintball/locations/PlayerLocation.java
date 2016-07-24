package me.synapz.paintball.locations;

import me.synapz.paintball.storage.files.UUIDPlayerDataFile;
import org.bukkit.Location;

public class PlayerLocation extends PaintballLocation {

    private final String path;
    private final UUIDPlayerDataFile uuidPlayerDataFile;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public PlayerLocation(UUIDPlayerDataFile uuidPlayerDataFile, Location location) {
        super(null, location);

        this.uuidPlayerDataFile = uuidPlayerDataFile;
        this.path = "Location";

        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public PlayerLocation(UUIDPlayerDataFile uuidPlayerDataFile) {
        super(null, uuidPlayerDataFile.getFileConfig().getString("Location"));

        this.uuidPlayerDataFile = uuidPlayerDataFile;
        this.path = "Location";
    }

    protected void setLocation() {
        uuidPlayerDataFile.getFileConfig().set(path, super.toString());
    }
}
