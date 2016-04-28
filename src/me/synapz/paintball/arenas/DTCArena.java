package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;
import java.util.Map;

public class DTCArena extends FlagArena implements Listener {

    private Map<Location, Team> coreLocations = new HashMap<>();

    public DTCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public void loadFlags() {
        for (Team team : getArenaTeamList()) {
            Location flagLoc = Utils.simplifyLocation(getFlagLocation(team));

            coreLocations.put(flagLoc, team);
            flagLoc.getBlock().setType(Material.CLAY);
            flagLoc.getBlock().setData(team.getDyeColor().getData());
        }
    }

    @Override
    public void resetFlags() {
        for (Team team : getArenaTeamList()) {
            Location flagLoc = Utils.simplifyLocation(getFlagLocation(team));

            flagLoc.getBlock().setType(Material.AIR);
        }
    }

    public Map<Location, Team> getCoreLocations() {
        return coreLocations;
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.DTC;
    }
}
