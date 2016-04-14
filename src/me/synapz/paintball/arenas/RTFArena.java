package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import static me.synapz.paintball.storage.Settings.ARENA_FILE;
import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.STRIKETHROUGH;

public class RTFArena extends FlagArena {

    private Location dynamicFlagLocation;

    public RTFArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    // Overridden because it adds neutral flags to the list
    @Override
    public String getSteps() {
        ChatColor done = STRIKETHROUGH;
        String end = RESET + "" + GRAY;
        StringBuilder steps = new StringBuilder(super.getSteps());

        // If the arena is already done, there is nothing to append
        if (isSetup() && isEnabled())
            return steps.toString();

        if (Settings.ARENA_FILE.getString("Arenas." + getDefaultName() + ".Neutral.Flag") != null)
            steps.append(", " + done + "neutral (flag)" + end);
        else
            steps.append(", neutral (flag)");

        return steps.toString();
    }

    // Adds the fact that a flag must be set in getSteps
    @Override
    public boolean isSetup() {
        boolean flagsSet = true;

        if (ARENA_FILE.getString("Arenas." + getDefaultName() + ".Neutral.Flag") == null)
            flagsSet = false;

        return super.isSetup() && flagsSet;
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location loc = getFlagLocation(team).subtract(0, 1, 0);

            loc.getBlock().setType(Material.WOOL);
            Block block = loc.getBlock();

            block.setData(team.getDyeColor().getData());
        }

        Utils.createFlag(null, getNuetralFlagLocation());
    }

    @Override
    public void resetFlags() {
        for (Team team : getArenaTeamList()) {
            Location loc = getFlagLocation(team).subtract(0, 1, 0);

            loc.getBlock().setType(Material.AIR);
        }

        getNuetralFlagLocation().getBlock().setType(Material.AIR);
    }

    public Location getNuetralFlagLocation() {
        return new FlagLocation(this, null).getLocation();
    }

    public void setNuetralFlagLocation(Location loc) {
        new FlagLocation(this, null, loc);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.RTF;
    }
}
