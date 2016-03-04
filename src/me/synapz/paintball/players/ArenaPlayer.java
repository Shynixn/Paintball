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
    private int money;
    private boolean won = false;
    private int health = arena.HITS_TO_KILL;
    private int lives = arena.LIVES;

    private PaintballScoreboard sb;

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
        // TODO: dont make random!
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
        player.getInventory().clear();
        giveArmour();
        giveWoolHelmet();
    }

    @Override
    public void leaveArena() {
        super.forceLeaveArena();
        team.playerLeaveTeam();

        // TODO: instead of check
        for (Team team : arena.getArenaTeamList()) {
            if (team.getSize() == 0) {
                // TODO: get winner
                arena.win(Arrays.asList(arena.getAllArenaPlayers().get(0).getTeam()));
                break;
            }
        }

        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);
    }

    @Override
    public void updateScoreboard() {
        if (sb == null)
            return;

        int size = arena.getArenaTeamList().size()-1;
        sb.reloadTeams(true)
                .reloadLine(ScoreboardLine.MONEY, "1", size+2)
                .reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.KILL_COIN, String.valueOf(getKillCoins()), size+4)
                .reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6)
                .reloadLine(ScoreboardLine.HEALTH, Utils.makeHealth(health), size+8)
                .reloadLine(ScoreboardLine.LIVES, Utils.makeHealth(lives), size+9, arena.LIVES > 0);
    }

    @Override
    public void updateDisplayName() {
        if (sb != null)
            sb.setDisplayNameCounter(Utils.getCurrentCounter(arena));
    }

    @Override
    public void createScoreboard() {
        sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(true)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, 0) // TODO: vault.getMoney(Player) ?
                .addLine(ScoreboardLine.KD, "0.00")
                .addLine(ScoreboardLine.KILL_COIN, 0)
                .addLine(ScoreboardLine.KILL_STREAK, 0)
                .addLine(ScoreboardLine.KILLS, 0)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.HEALTH, Utils.makeHealth(arena.HITS_TO_KILL));
        if (arena.LIVES > 0)
            sb.addLine(ScoreboardLine.LIVES, Utils.makeHealth(arena.LIVES));

        sb.addLine(ScoreboardLine.TEAM, team.getChatColor() + team.getTitleName());
        sb.build();
    }

    public void setWon() {
        won = true;
    }

    public boolean die() {
        int newHealth = health--;
        sb.updateNametags(false);
        if (newHealth != 1) {
            arena.updateAllScoreboard();
            return false;
        } else {
            setHealth(newHealth);
            return true;
        }
    }

    public void kill(ArenaPlayer target) {
        if (arena.getTeamScore(team) == arena.MAX_SCORE)
            return;

        killStreak++;
        killCoins = killCoins + arena.KILLCOIN_PER_KILL;
        money++; // TODO: check arena settings for per kill money
        kills++;
        arena.incrementTeamScore(team);
        Settings.PLAYERDATA.incrementStat(StatType.KILLS, this);
        Settings.PLAYERDATA.incrementStat(StatType.HIGEST_KILL_STREAK, this);
        arena.broadcastMessage(THEME + player.getName() + SECONDARY + " shot " + THEME + target.getPlayer().getName());

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

    public void setHealth(int newHealth) {
        health = newHealth;
        sb.updateNametags(false);

        if (health == 1) {
            deaths++;
            lives--;

            if (killCoins - arena.KILLCOIN_PER_DEATH > 0 || killCoins - arena.KILLCOIN_PER_DEATH < 0 && arena.KILL_COINS_NEGATIVE)
                killCoins = killCoins - arena.KILLCOIN_PER_DEATH;
            else
                killCoins = 0;

            if (money - arena.KILLCOIN_PER_DEATH > 0 || killCoins - arena.KILLCOIN_PER_DEATH < 0 && arena.KILL_COINS_NEGATIVE)
                killCoins = killCoins - arena.KILLCOIN_PER_DEATH;
            else
                killCoins = 0;

            Settings.PLAYERDATA.incrementStat(StatType.DEATHS, this);

            if (arena.LIVES > 0 && lives == 0) {
                this.forceLeaveArena();
                new SpectatorPlayer(this);
                return;
            } else {
                health = arena.HITS_TO_KILL;
                killStreak = 0;
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
                new ProtectionCountdown(arena.SAFE_TIME, this);
                updateScoreboard();
            }
        }
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

    public int getMoney() {
        return money;
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