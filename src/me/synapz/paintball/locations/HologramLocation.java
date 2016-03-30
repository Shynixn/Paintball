package me.synapz.paintball.locations;

import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Location;

import java.util.List;

public class HologramLocation extends PaintballLocation {

    private final int page;
    private final StatType type;
    private final boolean addToFile;

    // Creates a new TeamLocation AND sets the location in Arenas.yml
    public HologramLocation(Location location, StatType type, int page, boolean addToFile) {
        super(null, location);

        this.type = type;
        this.page = page;
        this.addToFile = addToFile;

        setLocation();
    }

    // Creates a new TeamLocation by looking inside of arenas.yml and grabbing it out
    public HologramLocation(String loc) {
        super(null, loc);

        String[] index = loc.split(",");
        String rawStatType = index[loc.split(",").length-2];
        String rawPage = index[loc.split(",").length-1];

        if (rawStatType.equals("all")) {
            this.type = null;
        } else {
            this.type = StatType.getStatType(null, rawStatType);
        }
        this.page = Integer.parseInt(rawPage);
        addToFile = false;
    }

    protected void setLocation() {
        if (addToFile) {
            List<String> locations = Settings.ARENA.getHologramList();
            locations.add(super.toString() + "," + (type == null ? "all" : type.getName()) + "," + page);
            Settings.ARENA_FILE.set("Hologram-Locations", locations);
            Settings.ARENA.saveFile();
        }
    }

    public void removeLocation() {
        List<String> locations = Settings.ARENA.getHologramList();
        locations.remove(super.toString() + "," + (type == null ? "all" : type.getName()) + "," + page);
        Settings.ARENA_FILE.set("Hologram-Locations", loc);
        Settings.ARENA.saveFile();
    }

    public StatType getType() {
        return type;
    }

    public int getPage() {
        return page;
    }
}
