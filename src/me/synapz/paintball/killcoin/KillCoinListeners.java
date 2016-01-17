package me.synapz.paintball.killcoin;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Message;
import me.synapz.paintball.enums.KillcoinTypes;
import me.synapz.paintball.players.ArenaPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

        if (!e.getInventory().getName().contains("KillCoin Shop"))
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

    @EventHandler
    public void onClickKillCoinItem(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

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

        if (killCoinItem.hasType()) {
            KillcoinTypes type = KillcoinTypes.valueOf(killCoinItem.getItemType());

            // TODO: check if null

            switch (type) {
                case DOUBLE_SHOOTER:
                    System.out.println("test");
                    break;
            }
        } else {
            // TODO: if item has a " " in it for distinguisihing, it doesnt get valled normally
            killCoinItem.onClickItem(e);
        }
    }
}