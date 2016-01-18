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

import static me.synapz.paintball.storage.Settings.*;


public final class ArenaPlayer extends PaintballPlayer {

    private int killStreak = 0;
    private int killCoins = 0;
    private int kills = 0;
    private int deaths = 0;
    private int moneyEarned;
    private boolean won = false;

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
        arena.addPlayer(this);
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team));
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
        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);
    }

    public void setWon() {
        won = true;
    }

    public void die() {
        killStreak = 0;
        killCoins--; // TODO: check arena settings for per death
        moneyEarned--; // TODO: check per death and subtract
        deaths++;
        Settings.PLAYERDATA.incrementStat(StatType.DEATHS, this);
    }

    public void kill(ArenaPlayer target) {
        killStreak++;
        killCoins++; // TODO: instead of just add one, check arena settings for per kill
        moneyEarned++; // TODO: check arena settings for per kill money
        kills++;
        arena.incrementTeamScore(team);
        Settings.PLAYERDATA.incrementStat(StatType.KILLS, this);
        Settings.PLAYERDATA.incrementStat(StatType.HIGEST_KILL_STREAK, this);
        arena.broadcastMessage(ChatColor.GREEN, Settings.THEME + player.getName() + " shot " + target.getPlayer().getName() + ". Team score now " + arena.getTeamScore(team) + "/" + arena.MAX_SCORE, "");
        // TODO: kill messages
        if (reachedGoal()) {
            arena.win(team);
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
        return arena.MAX_SCORE == arena.getTeamScore(team); // TODO: make sure when the game ends no one can be killed
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

    public void updateSideScoreboard() {
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = sb.registerNewObjective(team.getTitleName(), "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(Settings.THEME + "Paintball");

        Score timeLeft = objective.getScore(team.getChatColor() + "Time Left: ");
        timeLeft.setScore((int) GameCountdown.gameCountdowns.get(arena).getCounter());

        Score teamScore = objective.getScore(team.getChatColor() + "Score: "); //Get a fake offline player
        teamScore.setScore(arena.getTeamScore(team));

        player.setScoreboard(sb);
    }

    private void giveItems() {
        player.getInventory().clear();
        player.getInventory().setItem(0, Utils.makeItem(Material.SNOW_BALL, Settings.THEME + "Paintball", 64));
        player.getInventory().setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "KillCoin Shop", 1));

    }

    private void colorPlayerTitle() {
        //if (!Settings.COLOR_PLAYER_TITLE)
        //    return;
        Scoreboard sb = Team.getPluginScoreboard();
        final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(team.getTitleName());
        playerTeam.addPlayer(player);
        playerTeam.setAllowFriendlyFire(false); // TODO: does this work?
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