package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Message;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;

public class ArenaPlayer extends LobbyPlayer {

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);

        a.addArenaPlayer(this);
        initPlayer();
    }

    public void giveArmour() {}
    @Override
    protected void initPlayer() {
        player.teleport(arena.getSpawn(team));
        storeInformation();
        giveItems();
        giveWoolHelmet();
        giveArmour();
    }

    public void leaveArena() {

    }

    @Override
    protected void giveItems() {

    }

    @Override
    protected void storeInformation() {
        arena.addLobbyPlayer(this);
    }
}