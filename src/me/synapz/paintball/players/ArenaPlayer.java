package me.synapz.paintball.players;

import me.synapz.paintball.*;
import me.synapz.paintball.Team;

import me.synapz.paintball.countdowns.*;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.killcoin.KillCoinItemHandler;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.*;


public final class ArenaPlayer extends PaintballPlayer implements ScoreboardPlayer {

    private Map<String, KillCoinItem> playersKillCoinItems = new HashMap<>();
    private int killStreak = 0;
    private int killCoins = 0;
    private int kills = 0;
    private int deaths = 0;
    private int moneyEarned;
    private boolean won = false;
    private int health = 0;

    private final PaintballScoreboard sb;

    // TODO: turn these into enums :D

    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
        sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(true)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, 0) // TODO: vault.getMoney(Player) ?
                .addLine(ScoreboardLine.KD, "0.00")
                .addLine(ScoreboardLine.KILL_COIN, 0)
                .addLine(ScoreboardLine.KILL_STREAK, 0)
                .addLine(ScoreboardLine.KILLS, 0)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.HEALTH, Utils.makeHealth(health, arena.HITS_TO_KILL))
                .addLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName())
                .build();
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
        // TODO: dont make random!
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
        player.getInventory().clear();
        giveArmour();
        giveWoolHelmet();
    }

    @Override
    public void forceLeaveArena() {
        team.playerLeaveTeam();
        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);
        super.forceLeaveArena();
    }

    public void updateScoreboard() {
        int size = arena.getArenaTeamList().size()-1;
        sb.reloadTeams(true)
                .reloadLine(ScoreboardLine.MONEY, "1", size+2)
                .reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.KILL_COIN, String.valueOf(getKillCoins()), size+4)
                .reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6);
    }

    public void updateDisplayName() {
        int time = (int) (ArenaCountdown.tasks.get(arena) == null ? GameCountdown.gameCountdowns.get(arena) == null ? GameFinishCountdown.arenasFinishing.get(arena).getCounter() : GameCountdown.gameCountdowns.get(arena).getCounter() : ArenaCountdown.tasks.get(arena).getCounter());
        sb.setDisplayNameCounter(time);
    }

    public void setWon() {
        won = true;
    }

    public boolean die() {
        health++;
        if (health == arena.HITS_TO_KILL) {
            // TODO: Add config.yml option for negative killcoins
            deaths++;
            health = 0;
            killStreak = 0;
            if (killCoins - arena.KILLCOIN_PER_DEATH > 0)
                killCoins = killCoins - arena.KILLCOIN_PER_DEATH;

            moneyEarned--; // TODO: check per death and subtract
            Settings.PLAYERDATA.incrementStat(StatType.DEATHS, this);
            player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
            new ProtectionCountdown(arena.SAFE_TIME, this);
            return true;
        } else {
            return false;
        }

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

        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            arenaPlayer.updateScoreboard();
        }
        if (reachedGoal()) {
            arena.win(Arrays.asList(team));
        }
    }

    public void addItem(KillCoinItem item) {
        this.getPlayer().getInventory().addItem(item.getItemStack(this, false));
        if (item.hasExpirationTime()) {
            new ExpirationCountdown(item, this, item.getExpirationTime());
        }
        playersKillCoinItems.put(item.getItemName(true), item);
    }

    public KillCoinItem getItemWithName(String displayName) {
        return playersKillCoinItems.get(displayName);
    }

    public int getHealth() {
        return health;
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
        return String.format("%.2f", Utils.divide(kills, deaths));
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void giveItems() {
        player.getInventory().clear();
        player.getInventory().setItem(0, Utils.makeItem(Material.SNOW_BALL, THEME + "Paintball", 64));
        player.getInventory().setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "KillCoin Shop", 1));
        player.updateInventory();
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