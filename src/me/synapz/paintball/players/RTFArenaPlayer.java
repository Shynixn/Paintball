package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.RTFArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RTFArenaPlayer extends FlagArenaPlayer {

    RTFArena rtfArena = (RTFArena) arena;

    public RTFArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public void pickupFlag(Location loc, Team pickedUp) {
        super.pickupFlag(loc, pickedUp);

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_PICKUP, 5, 5);

        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + player.getName() + " has stolen the flag!");

        rtfArena.setHolder(this);
        player.getInventory().setHelmet(Utils.makeBanner(ChatColor.WHITE + "Neutral Flag", DyeColor.WHITE));
        player.updateInventory();
    }

    @Override
    public void scoreFlag() {
        super.scoreFlag();

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_SCORE, 5, 5);

        Location toReset = rtfArena.getNuetralFlagLocation();
        Utils.createFlag(null, toReset);

        rtfArena.setCurrentFlagLocation(null);
    }


    @Override
    public void dropFlag() {
        super.dropFlag();

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_DROP, 5, 5);

        if (rtfArena.getHolder() != null) {
            rtfArena.setCurrentFlagLocation(getLastLocation());
        }

        Location toReset = rtfArena.getCurrentFlagLocation();
        Utils.createFlag(null, toReset);

        rtfArena.setHolder(null);
    }
}
