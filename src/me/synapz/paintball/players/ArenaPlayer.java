package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;

import static me.synapz.paintball.storage.Settings.*;


public class ArenaPlayer extends PaintballPlayer {

    int killStreak;
    int killCoins;

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);

        initPlayer();
    }

    public boolean ifWon() {
        return true;
    }

    public int getKillstreak() {
        return 10;
    }

    protected String getChatLayout() {
        return ARENA_CHAT;
    }

    protected void initPlayer() {
        arena.addPlayer(this);
        player.teleport(arena.getSpawn(team));
        giveArmour();
        colorPlayerTitle();
        // TODO: openKit menu, stop from being able to move
    }

    private void colorPlayerTitle() {
        if (!Settings.COLOR_PLAYER_TITLE)
            return;
        Scoreboard sb = Team.getPluginScoreboard();
        final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(team.getTitleName());
        playerTeam.addPlayer(player);
        player.setScoreboard(sb);
    }

    private void giveArmour() {
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
    }

    private ItemStack[] colorLeatherItems(ItemStack... items) {
        int location = 0;
        ItemStack[] editedItems = new ItemStack[items.length];
        for (ItemStack item : items) {
            ItemStack armour = new ItemStack(item.getType(), 1);
            LeatherArmorMeta lam = (LeatherArmorMeta)armour.getItemMeta();
            lam.setColor(team.getColor());
            lam.setDisplayName(team.getChatColor() + team.getTitleName() + " Team");
            armour.setItemMeta(lam);
            editedItems[location] = armour; location++;
        }
        return editedItems;
    }
}