package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import static org.bukkit.ChatColor.*;

public class SpectatorPlayer extends PaintballPlayer {

    public static final String LEAVE_ARENA = RED + "" + BOLD + "Click" + Messenger.SUFFIX + RESET + DARK_RED + "Leave Arena";
    public static final String TELEPORTER = RED + "" + Settings.THEME + String.valueOf(BOLD) + "Click" + Messenger.SUFFIX + RESET + Settings.SECONDARY + "Teleporter";

    public SpectatorPlayer(Arena a, Player p) {
        super(a, new Team(a), p);

        player.teleport(arena.getSpectatorLocation());
    }

    public SpectatorPlayer(ArenaPlayer arenaPlayer) {
        super(arenaPlayer.getArena(), new Team(arenaPlayer.getArena()), arenaPlayer.getPlayer());

        player.teleport(player.getLocation().add(0, 0.5, 0));
    }

    @Override
    protected void initPlayer() {
        Settings.PLAYERDATA.savePlayerInformation(player);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    @Override
    protected void giveItems() {
        player.getInventory().setItem(0, Utils.getSkull(this.getPlayer(), TELEPORTER));
        player.getInventory().setItem(8, Utils.makeItem(Material.BED, LEAVE_ARENA, 1));
        player.updateInventory();
    }

    @Override
    protected void showMessages() {
        Messenger.titleMsg(player, true, GREEN + "You are now spectating!");
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        PaintballScoreboard scoreboard = new PaintballScoreboard(this, 1, "Spectator:")
                .addTeams(true)
                .build();
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        arena.updateAllScoreboard();
        return scoreboard;
    }

    @Override
    public void updateScoreboard() {
        if (pbSb != null)
            pbSb.reloadTeams(true);
    }

    @Override
    public void leave() {
        super.leave();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public void openMenu() {
        // TODO: add next page item for if there is more than 54 people
        int size = arena.getAllArenaPlayers().size();
        int factor = 9;

        for ( ; factor < size; factor += 9);

        Inventory inv = Bukkit.createInventory(null, factor, Settings.THEME + "Teleporter");

        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            Player player = arenaPlayer.getPlayer();
            inv.addItem(Utils.getSkull(player, Settings.THEME + BOLD + "Click" + Messenger.SUFFIX + RESET + Settings.SECONDARY + "Teleport to " + ITALIC + player.getName()));
        }

        player.openInventory(inv);
    }

    public void spectate(ArenaPlayer arenaPlayer) {
        Player target = arenaPlayer.getPlayer();
        player.teleport(target);
        Messenger.success(player, "Now spectating " + GRAY + target.getName() + GREEN + "!");
    }

    @Override
    public void updateDisplayName() {
        if (pbSb != null) {
            pbSb.setDisplayNameCounter("", Utils.getCurrentCounter(arena));
        }
    }
}
