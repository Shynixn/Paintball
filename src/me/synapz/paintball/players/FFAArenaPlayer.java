package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.SECONDARY;

public class FFAArenaPlayer extends ArenaPlayer {

    public FFAArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    // FFA will have 16 different colors, so we just want the normal colors here
    public void sendShotMessage(String action, ArenaPlayer died) {
        if (action == null) {
            action = "shot";
        }

        if (action.isEmpty()) {
            action = "shot";
        }

        arena.broadcastMessage(Settings.THEME + player.getName() + SECONDARY + " " + action + " " + Settings.THEME + died.getPlayer().getName());
    }
}