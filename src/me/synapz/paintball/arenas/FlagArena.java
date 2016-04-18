package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import static me.synapz.paintball.storage.Settings.ARENA_FILE;
import static org.bukkit.ChatColor.*;

public abstract class FlagArena extends Arena {

    public FlagArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    // Loads flag points. A CTFArena sets banners while a DomArena sets stained glass every 5 blocks
    public abstract void loadFlags();

    // Resets the flag points. A CTFArena turns all flags to AIR while DomArena resets changed blocks
    public abstract void resetFlags();

    public void setFlagLocation(Team team, Location loc) {
        new FlagLocation(this, team, loc);
    }

    public Location getFlagLocation(Team team) {
        return new FlagLocation(this, team).getLocation();
    }

    // Overridden because it adds set flags to the list
    @Override
    public String getSteps() {
        ChatColor done = STRIKETHROUGH;
        String end = RESET + "" + GRAY;
        StringBuilder steps = new StringBuilder(super.getSteps());

        // If the arena is already done, there is nothing to append
        if (isSetup() && isEnabled())
            return steps.toString();

        for (Team t : getArenaTeamList()) {
            String lobbyName = t.getTitleName().toLowerCase().replace(" ", "") + " (flag)";

            steps.append(", ");
            steps.append(Settings.ARENA_FILE.getString(t.getPath()) != null ? done + lobbyName + end : lobbyName);
        }

        return steps.toString();
    }

    // Adds the fact that a flag must be set in getSteps
    @Override
    public boolean isSetup() {
        boolean flagsSet = true;

        for (Team t : getArenaTeamList()) {
            if (ARENA_FILE.getString(t.getPath()) == null)
                flagsSet = false;
        }

        return super.isSetup() && flagsSet;
    }

    // Turns all flags to air whatever to do in resetFlags
    @Override
    public void forceLeaveArena() {
        super.forceLeaveArena();

        resetFlags();
    }
}
