package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.locations.TeamLocation;
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
        stripValues();
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

    private void stripValues() {
        // todo: exp saves
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
    }

    private void giveItems() {
        // TODO: give item to leave and teleport
    }

    private void displayMessages() {
        Message.getMessenger().msg(player, true, true, GREEN + "You are now spectating!");
    }
}

