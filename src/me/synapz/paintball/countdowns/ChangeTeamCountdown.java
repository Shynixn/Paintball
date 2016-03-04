package me.synapz.paintball.countdowns;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChangeTeamCountdown extends PaintballCountdown {

    public static Map<String, ChangeTeamCountdown> teamPlayers = new HashMap<>();

    private String name;
    private Player player;

    public ChangeTeamCountdown(int counter, Player player) {
        super(counter+1); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0
        end = 1;
        this.name = player.getName();
        this.player = player;

        if (teamPlayers.keySet().contains(name)) {
            teamPlayers.remove(name, teamPlayers.get(name));
        }
        teamPlayers.put(name, this);
    }

    public void onFinish() {
        teamPlayers.remove(name, this);
        // TODO: send message, or somehow package ActionBarAPI with plugin?
        ActionBarAPI.sendActionBar(this.player, Messenger.TEAM_SWITCH_END, 240);
    }

    // Called every iteration of run()
    public void onIteration() {
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
