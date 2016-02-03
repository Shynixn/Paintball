package me.synapz.paintball.players;

import me.synapz.paintball.*;
import me.synapz.paintball.Team;
import me.synapz.paintball.countdowns.GameCountdown;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.killcoin.KillCoinItemHandler;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.synapz.paintball.storage.Settings.*;


public final class ArenaPlayer extends PaintballPlayer {

    private Map<String, Integer> oldScores;
    private int killStreak = 0;
    private int killCoins = 0;
    private int kills = 0;
    private int deaths = 0;
    private int moneyEarned;
    private boolean won = false;

    private String killCoinsName = Settings.THEME + "KillCoins: ";
    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    public boolean won() {
        return won;
    }

    @Override
    protected String getChatLayout() {
        return arena.ARENA_CHAT;
    }

    @Override
    protected void initPlayer() {
        oldScores = new HashMap<>();
        arena.addPlayer(this);
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team));
        player.getInventory().clear();
        giveArmour();
        giveWoolHelmet();

        oldScores.put(Settings.THEME + "KillCoins: ", 0);
    }

    @Override
    public void forceLeaveArena() {
        team.playerLeaveTeam();
        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);
        super.forceLeaveArena();
    }

    public void setWon() {
        won = true;
    }

    public void die() {
        killStreak = 0;

        // TODO: Add config.yml option for negative killcoins
        if (killCoins - arena.KILLCOIN_PER_DEATH > 0)
            killCoins = killCoins - arena.KILLCOIN_PER_DEATH;

        moneyEarned--; // TODO: check per death and subtract
        deaths++;
        Settings.PLAYERDATA.incrementStat(StatType.DEATHS, this);
    }

    public void kill(ArenaPlayer target) {
        killStreak++;
        killCoins = killCoins + arena.KILLCOIN_PER_KILL;
        moneyEarned++; // TODO: check arena settings for per kill money
        kills++;
        arena.incrementTeamScore(team);
        Settings.PLAYERDATA.incrementStat(StatType.KILLS, this);
        Settings.PLAYERDATA.incrementStat(StatType.HIGEST_KILL_STREAK, this);
        arena.broadcastMessage(ChatColor.GREEN, THEME + player.getName() + SECONDARY + " shot " + THEME + target.getPlayer().getName(), "");
        if (reachedGoal()) {
            arena.win(Arrays.asList(team));
        }
    }

    public void shoot(PlayerInteractEvent event) {
        Settings.PLAYERDATA.incrementStat(StatType.SHOTS, this);
    }

    public void giveShop() {
        KillCoinItemHandler.getHandler().showInventory(this);
    }

    public int getKillCoins() {
        return killCoins;
    }
    // This will look into config.yml for the arena, if the time or kills is reached, they reahced the goal
    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
    }

    public int getMoneyEarned() {
        return moneyEarned;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public String getKd() {
        float kd = deaths == 0 ? kills : (float)kills/ (float)deaths;
        return String.format("%.2f", kd);
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void respawn() {
        giveItems();
        giveArmour();
        giveWoolHelmet();
    }

    public void giveItems() {
        player.getInventory().clear();
        player.getInventory().setItem(0, Utils.makeItem(Material.SNOW_BALL, Settings.THEME + "Paintball", 64));
        player.getInventory().setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "KillCoin Shop", 1));
        player.updateInventory();
    }

    public void updateScoreboard() {
        int size = arena.getArenaTeamList().size()-1;
        Objective objective = sb.getObjective(DisplaySlot.SIDEBAR);

        objective.setDisplayName(Settings.THEME + "     Paintball " + Settings.SECONDARY + convertToNumberFormat() + "     ");

        int oldKills = oldScores.get(killCoinsName);
        sb.resetScores(Bukkit.getOfflinePlayer(killCoinsName + oldKills));

        Score killCoins = objective.getScore(Settings.THEME + "KillCoins: " + getKillCoins());

        killCoins.setScore(size+7);
    }

    @Override
    protected void loadScoreboard() {
        super.loadScoreboard();
        int size = 0;

        Objective objective = sb.registerNewObjective(arena.getName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score killCoins = objective.getScore(Settings.THEME + "KillCoins: 0");
        Score killStreak = objective.getScore(Settings.THEME +"KillStreak: 0");
        Score kills = objective.getScore(Settings.THEME +     "Kills: 0");
        Score kd = objective.getScore(Settings.THEME +        "K/D: 0");
        Score money = objective.getScore(Settings.THEME +     "Money: 0");
        Score line = objective.getScore(Settings.SECONDARY +  ChatColor.STRIKETHROUGH + "                         ");

        objective.setDisplayName(Settings.THEME + "     Paintball " + "     ");

        for (int i = 0; i < arena.getArenaTeamList().size(); i++, size++) {
            Team t = (Team) arena.getArenaTeamList().toArray()[i];
            Score teamScore = objective.getScore(t.getChatColor() + t.getTitleName() + ": " + Settings.SECONDARY + arena.getTeamScore(t));
            teamScore.setScore(size);
        }

        // TODO: set money here
        killCoins.setScore(size+6);
        killStreak.setScore(size+5);
        kills.setScore(size+4);
        kd.setScore(size+3);
        money.setScore(size+2);
        line.setScore(size+1);
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

    private String convertToNumberFormat() {
        int timeLeft = (int) GameCountdown.gameCountdowns.get(arena).getCounter();
        int minutes = timeLeft/60;
        int seconds = timeLeft%60;
        return String.format("%d:" + (seconds < 10 ? "0" : "") + "%d", minutes, seconds);
    }
}