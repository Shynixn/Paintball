package me.synapz.paintball.events;


import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.PbPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PaintballShoot implements Listener {

    @EventHandler
    public void onPaintballShoot(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        PbPlayer pbPlayer;
        Material item = player.getItemInHand().getType();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // if e.getItem.equals("config item");
            // todo: add more itemchecks, rifle, potion etc
            // todo: check snowball name and set it later on
            if (item == Material.SNOW_BALL || item == Material.DIAMOND_AXE) {
                // cancel the regular snowball spawning, spawn a custom one
                try {
                    pbPlayer = ArenaManager.getArenaManager().getArena(player).getPbPlayer(player);
                    System.out.println(ArenaManager.getArenaManager().getArena(player) + ", " + pbPlayer);
                    pbPlayer.launchProjectile();
                    e.setCancelled(true);
                } catch (NullPointerException ex) {
                    e.setCancelled(false);
                    return;
                }
            }
        }

    }
}
