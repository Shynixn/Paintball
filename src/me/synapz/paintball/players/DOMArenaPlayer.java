package me.synapz.paintball.players;

import de.Herbystar.TTA.TTA_Methods;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DOMArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DOMArenaPlayer extends ArenaPlayer {

    private boolean isSecuring;
    private int timeSecuring;

    private DOMArena domArena = (DOMArena) arena;

    public DOMArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        arena.decrementTeamScore(team);
        super.kill(arenaPlayer, action);
    }

    @Override
    public void leave() {
        super.leave();

        // TODO: Increment Points Secured
    }

    public void setSecuring(boolean securing) {
        if (!securing) {
            TTA_Methods.sendTitle(player, "", 0, 21, 0, "", 0, 21, 0);
            timeSecuring = 0;
        } else {
            if (timeSecuring >= domArena.SECURE_TIME+1) {
                TTA_Methods.sendTitle(player, null, 0, 21, 0, Settings.THEME + ChatColor.BOLD + "Position Secured!", 0, 21, 0);
                player.getWorld().playSound(player.getLocation(), domArena.SECURE, 5, 5);
            } else {
                if (timeSecuring == 1)
                    player.getWorld().playSound(player.getLocation(), domArena.START_SECURE, 5, 5);
                TTA_Methods.sendTitle(player, null, 0, 21, 0, makeBar(), 0, 21, 0);
            }
        }

        this.isSecuring = securing;
    }

    public boolean isSecuring() {
        return isSecuring;
    }

    public void incrementTimeSecuring() {
        timeSecuring++;
    }

    public void showTimeSecuring() {
        if (timeSecuring >= domArena.SECURE_TIME) {
            if (timeSecuring == domArena.SECURE_TIME) {
                domArena.teamSecured(Utils.simplifyLocation(player.getLocation()), team);
                TTA_Methods.sendTitle(player, null, 0, 21, 0, Settings.THEME + ChatColor.BOLD + "Position Secured!", 0, 21, 0);
                player.getWorld().playSound(player.getLocation(), domArena.SECURE, 5, 5);
            }
        } else {
            TTA_Methods.sendTitle(player, null, 0, 21, 0, makeBar(), 0, 21, 0);
        }
    }

    private String makeBar() {
        String bar = "";

        for (int i = 0; i < domArena.SECURE_TIME; i++) {
            if (timeSecuring <= i)
                bar += ChatColor.DARK_GRAY + "█";
            else
                bar += ChatColor.GREEN + "█";
        }

        return bar;
    }
}