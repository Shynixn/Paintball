package me.synapz.paintball.locations;

import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;


public class SkullLocation extends SignLocation {

    private final StatType statType;
    private final int rank;

    public SkullLocation(Location loc, StatType statType, int rank) {
        super(Utils.simplifyLocation(loc), SignLocations.SKULL);
        this.statType = statType;
        this.rank = rank;

        setLocation();
        Settings.ARENA.addSign(this);
    }

    public SkullLocation(String rawLocation) {
        super(SignLocations.SKULL, rawLocation);

        String[] locs = rawLocation.split(",");

        statType = StatType.getStatType(null, locs[locs.length-2]);
        rank = Integer.parseInt(locs[locs.length-1]);

        Settings.ARENA.addSign(this);
    }

    public void makeSkullBlock(BlockFace face) {
        Block block = loc.getBlock();

        block.setType(Material.SKULL);

        BlockState state = block.getState();
        Skull skull = (Skull) state;

        skull.setRotation(face);
        skull.setSkullType(SkullType.PLAYER);
        skull.setOwner((String) Settings.getSettings().getStatsFolder().getPlayerAtRankMap(rank, statType).keySet().toArray()[0]);
        skull.update();
    }

    @Override
    public String toString() {
        return super.toString() + "," + statType.getSignName() + "," + rank;
    }

    public StatType getStatType() {
        return statType;
    }

    public int getRank() {
        return rank;
    }
}
