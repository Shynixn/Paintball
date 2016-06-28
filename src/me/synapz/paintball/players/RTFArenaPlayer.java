package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.RTFArena;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;

public class RTFArenaPlayer extends FlagArenaPlayer {

    private RTFArena rtfArena = (RTFArena) arena;

    public RTFArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    @Override
    public void pickupFlag(Location loc, Team pickedUp) {
        super.pickupFlag(loc, pickedUp);

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_PICKUP, 5, 5);

        arena.broadcastMessage(Settings.THEME + new MessageBuilder(Messages.ARENA_FLAG_STEAL).replace(Tag.SENDER, player.getName()).replace(Tag.TEAM, "Neutral").build());

        rtfArena.setHolder(this);
        player.getInventory().setHelmet(Utils.makeBanner(ChatColor.WHITE + "Neutral Flag", DyeColor.WHITE));
        player.updateInventory();
    }

    @Override
    public void scoreFlag() {
        super.scoreFlag();

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_SCORE, 5, 5);

        Location toReset = rtfArena.getNuetralFlagLocation();

        rtfArena.setCurrentFlagLocation(Utils.createFlag(null, toReset, null));

        rtfArena.getBlockManager().restore(toReset);
    }


    @Override
    public void dropFlag() {
        super.dropFlag();

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        player.getWorld().playSound(player.getLocation(), rtfArena.FLAG_DROP, 5, 5);

        if (rtfArena.getHolder() != null) {
            rtfArena.setCurrentFlagLocation(Utils.createFlag(null, getLastLocation(), rtfArena.getBlockManager()));
        }

        rtfArena.setHolder(null);
    }
}
