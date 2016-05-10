package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.DOMArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Title;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DOMArenaPlayer extends ArenaPlayer {

    private boolean isSecuring;
    private int timeSecuring;
    private boolean messageSent;
    private Team beingSecured;

    private DOMArena domArena = (DOMArena) arena;

    public DOMArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
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

    public void setSecuring(boolean securing, Team beingSecured) {
        if (!securing) {
            new Title("", "", 0, 21, 0).clear(player);
            timeSecuring = 0;
            messageSent = false;
            this.beingSecured = null;
            updateScoreboard();
        } else {
            if (timeSecuring >= domArena.SECURE_TIME+1) {
                new Title("", Settings.THEME + ChatColor.BOLD + "Position Secured!", 0, 21, 0).send(player);
                player.getWorld().playSound(player.getLocation(), domArena.SECURE, 5, 5);
            } else {
                new Title("", makeBar(), 0, 21, 0).send(player);
                this.beingSecured = beingSecured;
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
                new Title("", Settings.THEME + ChatColor.BOLD + "Position Secured!", 0, 21, 0).send(player);
                player.getWorld().playSound(player.getLocation(), domArena.SECURE, 5, 5);
            }
        } else {
            if (timeSecuring == 5 && messageSent == false && beingSecured != null) {
                arena.broadcastMessage(Settings.THEME + beingSecured.getTitleName() + Settings.SECONDARY + " is being secured by " + Settings.THEME + team.getTitleName());
                messageSent = true;
            }

            new Title("", makeBar(), 0, 21, 0).send(player);
        }
    }

    private String makeBar() {
        String bar = "";

        if (timeSecuring == 1)
            player.getWorld().playSound(player.getLocation(), domArena.START_SECURE, 5, 5);

        for (int i = 0; i < domArena.SECURE_TIME; i++) {
            if (timeSecuring <= i)
                bar += ChatColor.DARK_GRAY + "█";
            else
                bar += ChatColor.GREEN + "█";
        }

        return bar;
    }
}