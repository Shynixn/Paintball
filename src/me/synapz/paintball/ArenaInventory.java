package me.synapz.paintball;

import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArenaInventory {

    private final PlayerInventory inv;
    private final ArenaPlayer arenaPlayer;
    private final Player player;

    public ArenaInventory(ArenaPlayer arenaPlayer) {
        this.arenaPlayer = arenaPlayer;
        this.player = arenaPlayer.getPlayer();
        this.inv = player.getInventory();
    }
}
