package me.synapz.paintball.players;

import me.synapz.paintball.*;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

public class LobbyPlayer extends PaintballPlayer {

    public LobbyPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    protected void initPlayer() {
        player.teleport(arena.getLobbySpawn(team));
        stripValues();
        storeInformation();
        giveItems();
        displayMessages();
        giveWoolHelmet();

        if (arena.canStartTimer()) {
            Utils.countdown(Settings.LOBBY_COUNTDOWN, Settings.LOBBY_INTERVAL, Settings.LOBBY_NO_INTERVAL, arena, GREEN + "Auto-teleporting in " + GRAY + "%time%" + GREEN + " seconds!", GREEN + "Teleporting" + GRAY + "\n%time%" + GREEN + " seconds", ChatColor.GREEN + "Teleporting into arena...", true);
        }
    }

    public void chat(String message) {
        String chat = Settings.ARENA_CHAT;

        chat = chat.replace("%TEAMNAME%", team.getTitleName());
        chat = chat.replace("%TEAMCOLOR%", team.getChatColor() + "");
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", Settings.getSettings().getPrefix());
        chat = chat.replace("%PLAYER%", player.getName());
        for (PaintballPlayer pbPlayer : arena.getAllPlayers().values()) {
            pbPlayer.getPlayer().sendMessage(chat);
        }
    }

    public void leaveArena() {
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());
    }

    protected void giveItems() {
        player.getInventory().clear();
        List<ItemStack> items = new ArrayList<ItemStack>() {{
            for (Team team : arena.getArenaTeamList()) {
                if (!team.getTitleName().equals(team.getTitleName())) {
                    // quick check to block spawning the wool the player is on
                    add(Utils.makeWool(team.getChatColor() + "Join " + team.getTitleName(), team.getDyeColor()));
                }
            }
        }};

        if (items.size() > 9) {
            String theme = Settings.getSettings().getTheme();
            String sec = Settings.getSettings().getSecondaryColor();
            player.getInventory().setItem(0, Utils.makeWool(sec + ">> " + theme + "Click to change team" + sec + " <<", team.getDyeColor()));
            return;
        }

        for (ItemStack item : items) {
            int spot = items.indexOf(item);
            player.getInventory().setItem(spot, items.get(spot));
        }
    }

    private void displayMessages() {
        arena.broadcastMessage(GREEN, team.getChatColor() + player.getName() + GREEN + " has joined the arena! " + GRAY + arena.getLobbyPlayers().size() + "/" + arena.getMax(), GREEN + "Joined arena " + GRAY + arena.getLobbyPlayers().size() + "/" + arena.getMax());
        Message.getMessenger().msg(player, true, true, GREEN + "You have joined the arena!");
    }

    protected void storeInformation() {
        Settings.getSettings().getCache().savePlayerInformation(player);
        arena.addLobbyPlayer(this);
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
}