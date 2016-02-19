package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.Utils;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

public final class SpectatorPlayer extends PaintballPlayer {

    public SpectatorPlayer(Arena a, Player p) {
        super(a, null, p);
    }

    protected void initPlayer() {
        // TODO: set to arena spectate? invisable? idk
        PLAYERDATA.savePlayerInformation(player);
        arena.addPlayer(this);
        player.teleport(arena.getSpectatorLocation());
        Utils.stripValues(player);
        giveItems();
        displayMessages();
    }

    protected String getChatLayout() {
        return arena.SPEC_CHAT;
    }

    public void chat(String message) {
        String chat = getChatLayout();

        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());

        for (Player player : arena.getAllPlayers().keySet()) {
            player.sendMessage(chat);
        }
    }

    @Override
    public void forceLeaveArena() {
        if (team != null)
            team.playerLeaveTeam();
        super.forceLeaveArena();
    }

    private void giveItems() {
        // TODO: give item to leave and teleport
    }

    private void displayMessages() {
        Message.getMessenger().msg(player, true, true, GREEN + "You are now spectating!");
    }
}

