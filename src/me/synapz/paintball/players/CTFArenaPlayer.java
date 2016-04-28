package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_PICKUP, 5, 5);

        if (ctfArena.getDropedFlagLocations().containsKey(loc))
            ctfArena.remFlagLocation(loc);
    }

    @Override
    public void dropFlag() {
        super.dropFlag();

        Utils.createFlag(heldFlag, getLastLocation());
        ctfArena.addFlagLocation(getLastLocation(), heldFlag);

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_DROP, 5, 5);

        heldFlag = null;
    }

    @Override
    public void scoreFlag() {
        super.scoreFlag();

        Location toReset = ctfArena.getFlagLocation(heldFlag);
        Utils.createFlag(heldFlag, toReset);

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_SCORE, 5, 5);

        heldFlag = null;
    }

    public Team getHeldFlag() {
        return heldFlag;
    }
}
