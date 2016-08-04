package me.synapz.paintball.coin;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.events.ArenaBuyItemEvent;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public class CoinItemListener implements Listener {

    private Map<String, Long> timeSinceLastShot;

    public CoinItemListener() {
        timeSinceLastShot = new HashMap<>();
    }

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
        if (!e.getInventory().getName().contains(Messages.ARENA_SHOP_NAME.getString())) {
            return;
        }
        if (coinItem.hasError(arenaPlayer)) {
            Messenger.error(player, new String[]{(String) coinItem.getError(arenaPlayer).get(0)});
            return;
        } else {

            coinItem.giveItemToPlayer(arenaPlayer);
            if ((coinItem.requiresMoney()) || (coinItem.requiresCoins())) {
                arena.updateAllScoreboard();
            }

            ArenaBuyItemEvent event = new ArenaBuyItemEvent(arenaPlayer, coinItem);
            Bukkit.getServer().getPluginManager().callEvent(event);
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

        if (player.isInsideVehicle()) {
            Messenger.error(player, Messages.CANNOT_USE_WHILE_RIDING);
            return;
        }

        if (arena.getState() != Arena.ArenaState.IN_PROGRESS) {
            Messenger.error(player, Messages.ARENA_IS_FINISHED);
            return;
        }

        long delay = clickedItem.getDelay();

        if (System.currentTimeMillis() - timeSinceLastShot.getOrDefault(player.getName(), delay) < delay) return;
        timeSinceLastShot.put(player.getName(), System.currentTimeMillis());

        if (clickedItem.hasSound()) {
            player.playSound(player.getLocation(), clickedItem.getSound(), 1.0F, 1.0F);
        }

        if (clickedItem.getSpeed() != player.getWalkSpeed())
            player.setWalkSpeed(clickedItem.getSpeed());

        e.setCancelled(true);

        ArenaClickItemEvent event = new ArenaClickItemEvent(arenaPlayer, clickedItem, e.getAction());
        Bukkit.getServer().getPluginManager().callEvent(event);
        CoinItemHandler.getHandler().getAllItems().get(ChatColor.RESET + clickedItem.getItemName(false).replace(ChatColor.RESET + "", "")).onClickItem(event);

        arenaPlayer.setLastClickedItem(clickedItem);
    }
}
