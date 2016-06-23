package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DOMArena;
import me.synapz.paintball.arenas.FlagArena;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.ActionBar;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ArenaStartCountdown extends PaintballCountdown {

    private Map<Player, Location> startLocations = new HashMap<>();

    public ArenaStartCountdown(int counter, Arena a, HashMap<Player, Location> startLocations) {
        super(a, counter); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0

        this.startLocations = startLocations;

        sendGameInfo();
    }

    public void onFinish() {

        if (arena instanceof FlagArena)
            ((FlagArena) arena).loadFlags();

        arena.broadcastMessage(Messages.ARENA_START_MESSAGE);
        arena.setState(Arena.ArenaState.IN_PROGRESS);
        arena.broadcastTitle(Messages.PREFIX, Messages.ARENA_START_MESSAGE, 0, 30, 20);
        tpAllPlayersBack();

        if (arena instanceof DOMArena) {
            new DomGameCountdown(arena);
            return;
        }

        new GameCountdown(arena);
    }

    // Called every iteration of run()
    public void onIteration() {
        String prefix = Messages.ARENA_START_COUNTDOWN_HEADER.getString();
        String suffix = new MessageBuilder(Messages.ARENA_START_COUNTDOWN_FOOTER).replace(Tag.TIME, ((int) counter) + "").build();

        arena.broadcastMessage(prefix + " " + suffix);
        arena.broadcastTitle(prefix, suffix, 0, 30, 20);
    }

    public boolean stop() {
        return arena.getState() != Arena.ArenaState.STARTING || arena.getAllArenaPlayers().size() == 0;
    }

    public boolean intervalCheck() {
        arena.updateAllScoreboardTimes();
        sendGameInfo();
        arena.updateSigns();
        tpAllPlayersBack();
        return counter <= arena.ARENA_NO_INTERVAL || counter % arena.ARENA_INTERVAL == 0;
    }

    @Override
    public void cancel() {
        super.cancel();
        tasks.remove(arena, this);
        arena.updateSigns();
    }

    private void tpAllPlayersBack() {
        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            Location playerLoc = arenaPlayer.getPlayer().getLocation();
            Location spawnLoc = startLocations.get(arenaPlayer.getPlayer());

            int playerX = playerLoc.getBlockX();
            int playerZ = playerLoc.getBlockZ();
            int playerY = playerLoc.getBlockY();
            int spawnX = spawnLoc.getBlockX();
            int spawnY = spawnLoc.getBlockY();
            int spawnZ = spawnLoc.getBlockZ();

            if (playerX != spawnX || playerY != spawnY || playerZ != spawnZ) {
                arenaPlayer.getPlayer().teleport(spawnLoc);
            }
        }
    }

    private void sendGameInfo() {
        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            ActionBar.sendActionBar(arenaPlayer.getPlayer(), Settings.THEME + ChatColor.BOLD + arena.getArenaType().getShortName().toUpperCase() + Messenger.SUFFIX + arena.getArenaType().getGameInfo());
        }
    }
}
