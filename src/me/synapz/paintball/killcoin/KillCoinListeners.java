package me.synapz.paintball.killcoin;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.players.PaintballPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class KillCoinListeners implements Listener {

    @EventHandler
    public void onClicKItemWhileInGame(InventoryClickEvent e) {
        Player player = (e.getWhoClicked() instanceof Player) ? (Player) e.getWhoClicked() : null;
        ItemStack item = e.getCurrentItem();

        if (player == null)
            return;

        if (item == null || !item.hasItemMeta() || !(item.getItemMeta().hasDisplayName()))
            return;

        Arena arena = ArenaManager.getArenaManager().getArena(player);

        if (arena == null)
            return;

        ArenaPlayer arenaPlayer = arena.getPaintballPlayer(player) instanceof ArenaPlayer ? (ArenaPlayer) arena.getPaintballPlayer(player) : null;

        if (arenaPlayer == null)
            return;

        KillCoinItem killCoinItem = KillCoinItemHandler.getHandler().getAllItems().get(item.getItemMeta().getDisplayName());

        if (killCoinItem == null)
            return;

        if (killCoinItem.hasError(arenaPlayer)) {
            Message.getMessenger().msg(player, false, ChatColor.RED, killCoinItem.getError(arenaPlayer));
        } else {
            killCoinItem.giveItemToPlayer(arenaPlayer);
            // TODO: give item to player and remove their killcoins, money, start timer, etc
        }
        e.setCancelled(true);
        player.closeInventory();
    }

    // TODO: PlayerInteractEvent to access the items method to do stuff
    // TODO: switch through each one and perform their task, maybe make each KillCoinItem point to their own doStuff method like killCoinItem.doStuff(e)
}
