package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import static me.synapz.paintball.storage.Settings.*;


public final class ArenaPlayer extends PaintballPlayer {

    private int killStreak = 0;
    private int killCoins = 0;
    private boolean won = false;

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    public boolean won() {
        return won;
    }

    public int getKillstreak() {
        return killStreak;
    }

    protected String getChatLayout() {
        return ARENA_CHAT;
    }

    protected void initPlayer() {
        arena.addPlayer(this);
        player.teleport(arena.getSpawn(team));
        giveItems();
        giveArmour();
        colorPlayerTitle();
        giveWoolHelmet();
        // TODO: openKit menu, stop from being able to move
    }

    @Override
    public void leaveArena() {
        super.leaveArena();
        team.playerLeaveTeam();
        Settings.getSettings().getCache().incrementStat(StatType.GAMES_PLAYED, this);
    }

    public void setWon() {
        won = true;
    }

    public void die() {
        killStreak = 0;
        player.teleport(arena.getSpawn(team));
        Settings.getSettings().getCache().incrementStat(StatType.DEATHS, this);
    }

    public void kill() {
        killStreak++;
        killCoins++;
        arena.incrementTeamScore(team);
        Settings.getSettings().getCache().incrementStat(StatType.KILLS, this);
        Settings.getSettings().getCache().incrementStat(StatType.HIGEST_KILL_STREAK, this);
        // TODO: kill messages
    }

    public void shoot(PlayerInteractEvent event) {
        // TODO: if they hit, increment HITS and add one to killstreak
        Settings.getSettings().getCache().incrementStat(StatType.SHOTS, this);
        // TODO: switch through each item, do something for each one
        if (reachedGoal()) {
            arena.win(team);
            // TODO: win messages
        }
    }

    public void giveShop() {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "KillCoin Shop");
        // TODO: add bunch of cool stuff with settings menu to disable, set cost, permission etc.
        // TODO: add permission to be able to access KillCoin Shop
        player.openInventory(inv);
    }

    // This will look into config.yml for the arena, if the time or kills is reached, they reahced the goal
    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
    }

    private void giveItems() {
        player.getInventory().clear();
        player.getInventory().setItem(0, Utils.makeItem(Material.SNOW_BALL, Settings.THEME + "Paintball", 64));
        player.getInventory().setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "KillCoin Shop", 1));

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