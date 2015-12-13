package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;
import static me.synapz.paintball.storage.Settings.*;

public class SpectatorPlayer extends PaintballPlayer {

    public SpectatorPlayer(Arena a, Player p) {
        super(a, null, p);

        getSettings().getCache().savePlayerInformation(player);
        initPlayer();
    }

    public void initPlayer() {
        // TODO: set to arena spectate? invisable? idk
        arena.addPlayer(this);
        player.teleport(arena.getSpectateSpawn());
        stripValues();
        giveItems();
        displayMessages();
    }

    public void chat(String message) {
        String chat = SPEC_CHAT;

        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());

        for (Player player : arena.getAllPlayers().keySet()) {
            player.sendMessage(chat);
        }
    }

    protected String getChatLayout() {
        return SPEC_CHAT;
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

