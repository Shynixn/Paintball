package me.synapz.paintball.countdowns;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.Arena;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProtectionCountdown extends PaintballCountdown {

    public static Map<String, ProtectionCountdown> godPlayers = new HashMap<>();

    private String name;
    private ArenaPlayer arenaPlayer;
    private Player player;
    private Arena arena;

    public ProtectionCountdown(int counter, ArenaPlayer player) {
        super(counter+1); // adds 1 so to human eyes it goes from 5 to 1 instead of 4 to 0
        end = 1;
        this.name = player.getPlayer().getName();
        this.arenaPlayer = player;
        this.player = player.getPlayer();
        this.arena = player.getArena();

        ActionBarAPI.sendActionBar(this.player, Messenger.PROTECTION_TIME.replace("%time%", String.valueOf(counter))); // TODO: last param in ticks or seconds?
        if (!godPlayers.keySet().contains(name)) {
            godPlayers.put(name, this);
        }
    }

    public void onFinish() {
        godPlayers.remove(name, this);
        Messenger.msg(this.player, Messenger.PROTECTION_END);
        ActionBarAPI.sendActionBar(this.player, Messenger.PROTECTION_END, 240);
    }

    // Called every iteration of run()
    public void onIteration() {
        String protectionMessage = Messenger.PROTECTION_TIME.replace("%time%", String.valueOf((int) counter-1));
        ActionBarAPI.sendActionBar(this.player, protectionMessage); // TODO: last param in ticks or seconds?
    }

    public boolean stop() {
        return (player == null || arenaPlayer == null || arena == null || arena != null && arena.getState() != Arena.ArenaState.IN_PROGRESS);
    }

    public boolean intervalCheck() {
        return true;
    }

    public void cancel() {
        super.cancel();
        godPlayers.remove(name, this);
    }
}
