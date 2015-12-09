package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.storage.Settings;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import static org.bukkit.ChatColor.*;

public class SpectatorPlayer extends PaintballPlayer {

    public SpectatorPlayer(Arena a, Player p) {
        super(a, null, p);
    }

    public void initPlayer() {
        // TODO: give some helmet?
        player.teleport(arena.getSpectateSpawn());
        stripValues();
        storeInformation();
        giveItems();
        displayMessages();
    }

    public void chat(String message) {
        String chat = Settings.SPEC_CHAT;

        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", Settings.getSettings().getPrefix());
        chat = chat.replace("%PLAYER%", player.getName());

        for (Player player : arena.getAllPlayers().keySet()) {
            player.sendMessage(chat);
        }
    }

    public void leaveArena() {
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());
    }

    protected void storeInformation() {
        Settings.getSettings().getCache().savePlayerInformation(player);
        arena.addSpectator(this);
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
        // give item to leave and teleport
    }

    private void displayMessages() {
        Message.getMessenger().msg(player, true, true, GREEN + "You are now spectating!");
    }
}

