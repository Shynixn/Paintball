package me.synapz.paintball.arenas;

import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class BlockManager {

    private Map<Location, BlockState> blocks = new HashMap<>();
    private Map<Location, BlockState> upperBlocks = new HashMap<>();

    public void addBock(BlockState state) {
        Location bottom = Utils.simplifyLocation(state.getLocation());
        Location upper = bottom.clone().add(0, 1, 0);

        blocks.put(bottom, state);
        upperBlocks.put(upper, upper.getBlock().getState());
    }

    public void restore(Location location) {
        Location lowerLoc = Utils.simplifyLocation(location);
        Location upperLoc = Utils.simplifyLocation(location.clone().add(0, 1, 0));
        BlockState lowerState = blocks.get(lowerLoc);
        BlockState topState = upperBlocks.get(upperLoc);

        if (lowerState != null) {
            lowerState.update(true);

            blocks.remove(lowerLoc, lowerState);
        }

        if (topState != null) {
            topState.update(true);

            upperBlocks.remove(upperLoc, topState);
        }
    }
}