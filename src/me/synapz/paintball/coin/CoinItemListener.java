package me.synapz.paintball.coin;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.players.ArenaPlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public class CoinItemListener implements Listener {

    @EventHandler
    public void onClicKItemWhileInGame(InventoryClickEvent e) {
        Player player = (e.getWhoClicked() instanceof Player) ? (Player) e.getWhoClicked() : null;
        ItemStack item = e.getCurrentItem();
        if (player == null) {
            return;
        }
        if ((item == null) || (!item.hasItemMeta()) || (!item.getItemMeta().hasDisplayName())) {
            return;
        }
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena == null) {
            return;
        }
        ArenaPlayer arenaPlayer = (arena.getPaintballPlayer(player) instanceof ArenaPlayer) ? (ArenaPlayer) arena.getPaintballPlayer(player) : null;
        if (arenaPlayer == null) {
            return;
        }
        CoinItem coinItem = CoinItemHandler.getHandler().getAllItems().get(item.getItemMeta().getDisplayName());
        if (coinItem == null) {
            return;
        }
        if (!e.getInventory().getName().contains("Coin Shop")) {
            return;
        }
        if (coinItem.hasError(arenaPlayer)) {
            Messenger.error(player, new String[]{(String) coinItem.getError(arenaPlayer).get(0)});
        } else {
            if ((coinItem.requiresMoney()) || (coinItem.requiresCoins())) {
                arena.updateAllScoreboard();
            }
            coinItem.giveItemToPlayer(arenaPlayer);
        }
        e.setCancelled(true);
        player.closeInventory();
    }

    @EventHandler
    public void onClickCoinItem(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        if (player == null) {
            return;
        }
        if ((item == null) || (!item.hasItemMeta()) || (!item.getItemMeta().hasDisplayName())) {
            return;
        }
        Arena arena = ArenaManager.getArenaManager().getArena(player);
        if (arena == null) {
            return;
        }
        ArenaPlayer arenaPlayer = (arena.getPaintballPlayer(player) instanceof ArenaPlayer) ? (ArenaPlayer) arena.getPaintballPlayer(player) : null;
        if (arenaPlayer == null) {
            return;
        }
        CoinItem clickedItem = arenaPlayer.getItemWithName(item.getItemMeta().getDisplayName());

        if (clickedItem == null) {
            return;
        }
        if (clickedItem.hasSound()) {
            player.playSound(player.getLocation(), clickedItem.getSound(), 1.0F, 1.0F);
        }
        ArenaClickItemEvent event = new ArenaClickItemEvent(arenaPlayer, clickedItem);
        Bukkit.getServer().getPluginManager().callEvent(event);
        CoinItemHandler.getHandler().getAllItems().get(ChatColor.RESET + clickedItem.getItemName(false).replace(ChatColor.RESET + "", "")).onClickItem(event);
        e.setCancelled(true);
    }
}
