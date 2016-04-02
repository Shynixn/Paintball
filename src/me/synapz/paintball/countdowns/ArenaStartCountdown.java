package me.synapz.paintball.countdowns;

import me.synapz.paintball.Arena;
import me.synapz.paintball.CTFArena;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.locations.FlagLocation;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

public class ArenaStartCountdown extends PaintballCountdown {

    private Map<Player, Location> startLocations = new HashMap<>();

    public ArenaStartCountdown(int counter, Arena a, HashMap<Player, Location> startLocations) {
        super(a, counter); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0

        this.startLocations = startLocations;
    }

    public void onFinish() {

        if (arena instanceof CTFArena)
            createFlags();

        arena.broadcastMessage(GREEN + "Game started");
        arena.setState(Arena.ArenaState.IN_PROGRESS);
        arena.broadcastTitle(Settings.PREFIX, GREEN + "Game started", 0, 30, 20);
        tpAllPlayersBack();
        new GameCountdown(arena);
    }

    // Called every iteration of run()
    public void onIteration() {
        String prefix = GREEN + "Starting ";
        String suffix = GRAY + "" + ((int) counter) + GREEN + " seconds!";

        arena.broadcastMessage(prefix + suffix);
        arena.broadcastTitle(prefix, suffix, 0, 30, 20);
    }

    public boolean stop() {
        return arena.getState() != Arena.ArenaState.STARTING || arena.getAllArenaPlayers().size() == 0;
    }

    public boolean intervalCheck() {
        arena.updateAllScoreboardTimes();

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

    private void createFlags() {
        for (Team team : arena.getArenaTeamList()) {
            Location loc = new FlagLocation((CTFArena) arena, team).getLocation();

            loc.getBlock().setType(Material.STANDING_BANNER);
            Banner banner = (Banner) loc.getBlock().getState();
            banner.setBaseColor(team.getDyeColor());
            banner.update();
        }
    }
}
