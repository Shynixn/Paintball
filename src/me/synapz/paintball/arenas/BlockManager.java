package me.synapz.paintball.arenas;

import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockManager {

    private Map<Location, BlockState> blocks = new HashMap<>();

    public void addBock(BlockState state) {
        blocks.put(Utils.simplifyLocation(state.getLocation()), state);
    }

    public void restore(Location location) {
        BlockState state = blocks.get(Utils.simplifyLocation(location));

        if (state != null) {
            state.update(true);
        }
    }
}
