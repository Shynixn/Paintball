package me.synapz.paintball.events;


import me.synapz.paintball.Arena;
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
            if (item == Material.SNOW_BALL || item == Material.DIAMOND_AXE) {
                // cancel the regular snowball spawning, spawn a custom one
                Arena arena = ArenaManager.getArenaManager().getArena(player);

                if (arena != null) {
                    // TODO add player.launchProjectile()
                    e.setCancelled(true);
                }
            }
        }

    }
}
