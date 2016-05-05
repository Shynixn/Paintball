package me.synapz.paintball.countdowns;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.utils.ActionBar;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangeTeamCountdown extends PaintballCountdown {

    public static Map<String, ChangeTeamCountdown> teamPlayers = new HashMap<>();

    private String name;
    private Player player;

    public ChangeTeamCountdown(int counter, Player player) {
        super(counter);
        this.name = player.getName();
        this.player = player;

        if (teamPlayers.keySet().contains(name)) {
            teamPlayers.remove(name, teamPlayers.get(name));
        }
        teamPlayers.put(name, this);
    }

    public void onFinish() {
        teamPlayers.remove(name, this);
        ActionBar.sendActionBar(this.player, Messenger.TEAM_SWITCH_END);
    }

    // Called every iteration of run()
    public void onIteration() {
        ActionBar.sendActionBar(this.player, Messenger.TEAM_SWITCH_TIME.replace("%time%", String.valueOf((int) counter)));
    }

    public boolean stop() {
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        return (player == null || arena == null || arena.getState() != Arena.ArenaState.WAITING);
    }

    public boolean intervalCheck() {
        return true;
    }

    public void cancel() {
        super.cancel();
        teamPlayers.remove(name, this);
    }
}
