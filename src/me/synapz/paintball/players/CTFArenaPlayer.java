package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;

public class CTFArenaPlayer extends FlagArenaPlayer {

    private CTFArena ctfArena = (CTFArena) arena;
    private Team heldFlag;

    public CTFArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    @Override
    public void pickupFlag(Location loc, Team pickedUp) {
        super.pickupFlag(loc, pickedUp);

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        heldFlag = pickedUp;

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_PICKUP, 5, 5);

        if (ctfArena.getDropedFlagLocations().containsKey(loc))
            ctfArena.remFlagLocation(loc);
    }

    @Override
    public void dropFlag() {
        super.dropFlag();

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        Location droppedLocation = Utils.createFlag(heldFlag, getLastLocation(), ctfArena.getBlockManager());
        ctfArena.addFlagLocation(droppedLocation, heldFlag);

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_DROP, 5, 5);

        heldFlag = null;
    }

    @Override
    public void scoreFlag() {
        super.scoreFlag();

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        Location toReset = ctfArena.getFlagLocation(heldFlag);
        Utils.createFlag(heldFlag, toReset, null);

        player.getWorld().playSound(player.getLocation(), ctfArena.FLAG_SCORE, 5, 5);

        heldFlag = null;
    }

    public Team getHeldFlag() {
        return heldFlag;
    }
}
