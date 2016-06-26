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
        BlockState state = blocks.get(Utils.simplifyLocation(location));
        BlockState topState = upperBlocks.get(Utils.simplifyLocation(location.clone().add(0, 1, 0)));

        if (state != null)
            state.update(true);

        if (topState != null)
            topState.update(true);
    }
}