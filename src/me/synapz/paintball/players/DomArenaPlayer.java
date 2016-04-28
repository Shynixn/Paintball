package me.synapz.paintball.players;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DomArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class DomArenaPlayer extends ArenaPlayer {

    private boolean isSecuring;
    private int timeSecuring;

    private DomArena domArena = (DomArena) arena;

    public DomArenaPlayer(Arena a, Team t, Player p) {
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
            BountifulAPI.sendTitle(player, 0, 21, 0, "", "");
            timeSecuring = 0;
        } else {
            if (timeSecuring >= domArena.SECURE_TIME+1)
                BountifulAPI.sendTitle(player, 0, 21, 0, "", Settings.THEME + ChatColor.BOLD + "Position Secured!");
            else
                BountifulAPI.sendTitle(player, 0, 21, 0, "", makeBar());
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
                BountifulAPI.sendTitle(player, 0, 21, 0, "", Settings.THEME + ChatColor.BOLD + "Position Secured!");
            }
        } else {
            BountifulAPI.sendTitle(player, 0, 21, 0, "", makeBar());
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