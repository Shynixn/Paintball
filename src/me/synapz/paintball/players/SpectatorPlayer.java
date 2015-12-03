package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

public class SpectatorPlayer extends PaintballPlayer {

    public SpectatorPlayer(Arena a, Player p) {
        super(a, null, p);
    }

    public void initPlayer() {
        /* player.teleport(arena.getLobbySpawn(team));
        stripValues();
        storeInformation();
        giveItems();
        displayMessages();

        if (arena.canStartTimer()) {
            Utils.countdown(Settings.LOBBY_COUNTDOWN, Settings.LOBBY_INTERVAL, Settings.LOBBY_NO_INTERVAL, arena, GREEN + "Auto-teleporting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Teleporting" + GRAY + "\n%time%" + GREEN + " seconds", ChatColor.GREEN + "Teleporting into arena...", true);
        } */
    }

    public void chat(String message) {
        String chat = Settings.SPEC_CHAT;

        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", Settings.getSettings().getPrefix());
        chat = chat.replace("%PLAYER%", player.getName());

        // TODO: for (arena.getAllPlayers())
        for (PaintballPlayer player : arena.getLobbyPlayers()) {
            player.getPlayer().sendMessage(chat);
        }
    }

    public void leaveArena() {

    }
}
