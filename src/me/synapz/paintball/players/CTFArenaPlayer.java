package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CTFArenaPlayer extends FlagArenaPlayer {

    private CTFArena ctfArena = (CTFArena) arena;
    private Team heldFlag;

    public CTFArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public void pickupFlag(Location loc, Team pickedUp) {
        super.pickupFlag(loc, pickedUp);

        heldFlag = pickedUp;

        if (ctfArena.getDropedFlagLocations().containsKey(loc))
            ctfArena.remFlagLocation(loc);
    }

    @Override
    public void dropFlag() {
        super.dropFlag();

        Utils.createFlag(heldFlag, getLastLocation());
        ctfArena.addFlagLocation(getLastLocation(), heldFlag);

        heldFlag = null;
    }

    @Override
    public void scoreFlag() {
        super.scoreFlag();

        Location toReset = ctfArena.getFlagLocation(heldFlag);
        Utils.createFlag(heldFlag, toReset);

        heldFlag = null;
    }

    public Team getHeldFlag() {
        return heldFlag;
    }
}
