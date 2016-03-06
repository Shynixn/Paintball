package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static me.synapz.paintball.storage.Settings.PLAYERDATA;
import static org.bukkit.ChatColor.GREEN;

public class SpectatorPlayer extends PaintballPlayer {

    public SpectatorPlayer(Arena a, Player p) {
        super(a, new Team(a), p);
        player.teleport(arena.getSpectatorLocation());
        PLAYERDATA.savePlayerInformation(player);
    }

    public SpectatorPlayer(ArenaPlayer arenaPlayer) {
        super(arenaPlayer.getArena(), new Team(arenaPlayer.getArena()), arenaPlayer.getPlayer());

        player.setAllowFlight(true);
        player.setFlying(true);

        player.teleport(player.getLocation().add(0, 0.5, 0));
    }

    @Override
    protected void initPlayer() {
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    protected void giveItems() {
        // TODO: give item to leave and teleport
    }

    @Override
    protected void showMessages() {
        Messenger.titleMsg(player, true, GREEN + "You are now spectating!");
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        PaintballScoreboard scoreboard = new PaintballScoreboard(this, 1, "Spectator:")
                .addTeams(true)
                .build();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        arena.updateAllScoreboard();
        return scoreboard;
    }

    @Override
    public void updateScoreboard() {
        if (pbSb != null)
            pbSb.reloadTeams(true);
    }
}
