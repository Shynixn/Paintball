package me.synapz.paintball.countdowns;

import de.Herbystar.TTA.TTA_Methods;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.SpectatorPlayer;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProtectionCountdown extends PaintballCountdown {

    public static Map<String, ProtectionCountdown> godPlayers = new HashMap<>();

    private String name;
    private ArenaPlayer arenaPlayer;
    private Player player;
    private Arena arena;
    private Team team;

    public ProtectionCountdown(int counter, ArenaPlayer player) {
        super(counter+1); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0
        end = 1;
        this.name = player.getPlayer().getName();
        this.arenaPlayer = player;
        this.player = player.getPlayer();
        this.arena = player.getArena();
        this.team = player.getTeam();

        TTA_Methods.sendActionBar(this.player, Messenger.PROTECTION_TIME.replace("%time%", String.valueOf(counter)));
        if (!godPlayers.keySet().contains(name)) {
            godPlayers.put(name, this);
        }
    }

    public void onFinish() {
        godPlayers.remove(name, this);
        Messenger.msg(this.player, Messenger.PROTECTION_END);
        TTA_Methods.sendActionBar(this.player, Messenger.PROTECTION_END);
    }

    // Called every iteration of run()
    public void onIteration() {
        String protectionMessage = Messenger.PROTECTION_TIME.replace("%time%", String.valueOf((int) counter-1));

        TTA_Methods.sendActionBar(this.player, protectionMessage);
    }

    public boolean stop() {
        return (player == null || arenaPlayer == null || arena == null || arena != null && arena.getState() != Arena.ArenaState.IN_PROGRESS || arena != null && arena.getPaintballPlayer(player) == null);
    }

    public boolean intervalCheck() {
        return true;
    }

    public void cancel() {
        super.cancel();
        godPlayers.remove(name, this);
    }
}
