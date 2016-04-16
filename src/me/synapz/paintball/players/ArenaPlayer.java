package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.countdowns.*;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.coin.CoinItemHandler;
import me.synapz.paintball.coin.CoinItems;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.*;

public class ArenaPlayer extends PaintballPlayer {

    private Map<String, CoinItem> coinItems = new HashMap<>();

    private int heightKillStreak;
    private int killStreak;
    private int coins;
    private int deaths;
    private int kills;
    private double money;
    private int health;
    private int hits;
    private int shots;
    protected int lives;
    private int multiplier;

    private Location lastLocation;

    private boolean isWinner;
    private boolean isTie;

    /**
     * Creates a new ArenaPlayer
     * @param a Arena for them to be added in
     * @param t Team they are on
     * @param p Player they are connected to
     */
    public ArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    /**
     * Teleports the player to an arena spawn point
     * Gives the player a wool helmet
     */
    @Override
    protected void initPlayer() {
        player.getInventory().clear();
        player.updateInventory();
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));

        if (arena.ARENA_WOOL_HELMET)
            giveWoolHelmet();
        giveItems = false;
        health = arena.HITS_TO_KILL;
        lives = arena.LIVES;
    }

    @Override
    protected void showMessages() {

    }

    @Override
    public PaintballScoreboard createScoreboard() {
        double bal = Settings.VAULT ? Settings.ECONOMY.getBalance(player) : 0;

        PaintballScoreboard sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(false)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, arena.CURRENCY + bal, Settings.VAULT)
                .addLine(ScoreboardLine.KD, "0.00")
                .addLine(ScoreboardLine.COIN, 0, arena.COIN_SHOP)
                .addLine(ScoreboardLine.KILL_STREAK, 0)
                .addLine(ScoreboardLine.KILLS, 0)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.HEALTH, Utils.makeHealth(arena.HITS_TO_KILL));
        if (arena.LIVES > 0)
            sb.addLine(ScoreboardLine.LIVES, Utils.makeHealth(arena.LIVES));
        return sb.build();
    }



    @Override
    public void updateScoreboard() {
        if (pbSb == null)
            return;

        double bal = Settings.VAULT ? Settings.ECONOMY.getBalance(player) : 0;

        int size = arena.getArenaTeamList().size()-1;
        pbSb.reloadTeams(false)
                .reloadLine(ScoreboardLine.MONEY, arena.CURRENCY + bal, size+2)
                .reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.COIN, String.valueOf(getCoins()), size+4)
                .reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6)
                .reloadLine(ScoreboardLine.HEALTH, Utils.makeHealth(health), size+8)
                .reloadLine(ScoreboardLine.LIVES, Utils.makeHealth(lives), size+9, arena.LIVES > 0);
    }

    @Override
    public void leave() {
        super.leave();
        team.playerLeaveTeam();

        if (Settings.VAULT) {
            if (isWinner)
                Settings.ECONOMY.depositPlayer(player, arena.MONEY_PER_WIN);
            else
                Settings.ECONOMY.withdrawPlayer(player, arena.MONEY_PER_DEFEAT);
        }

        PaintballCountdown countdown = GameCountdown.tasks.get(arena);
        int timePlayed;

        if (countdown instanceof GameFinishCountdown) {
            timePlayed = arena.TIME;
        } else if (countdown instanceof ArenaStartCountdown) {
            timePlayed = 0;
        } else if (countdown instanceof GameCountdown) {
            timePlayed = arena.TIME-(int)countdown.getCounter();
        } else {
            timePlayed = 0;
        }

        if (countdown != null && timePlayed != 0) {
            Settings.PLAYERDATA.addToStat(StatType.TIME_PLAYED, this, timePlayed);
        }

        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);
        Settings.PLAYERDATA.addToStat(StatType.HITS, this, hits);
        Settings.PLAYERDATA.addToStat(StatType.SHOTS, this, shots);
        Settings.PLAYERDATA.addToStat(StatType.KILLS, this, kills);
        Settings.PLAYERDATA.addToStat(StatType.DEATHS, this, deaths);

        // killstreak is less than past killstreak, return
        if (Settings.PLAYERDATA.getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath(player.getUniqueId())) < heightKillStreak)
            Settings.PLAYERDATA.setStat(StatType.HIGEST_KILL_STREAK, this, heightKillStreak);


        int left = 0;
        // There must be at least one team
        for (Team team : arena.getArenaTeamList()) {
            if (team.getSize() >= 1) {
                left++;
            }
        }

        // If there is less than one team with a player, end the game
        if (left <= 1 && arena.getAllPlayers().keySet().size() >= 1)
            arena.win(Arrays.asList(arena.getAllArenaPlayers().get(0).getTeam()));
    }

    public void incrementHits() {
        hits++;
    }

    public void incrementShots() {
        shots++;
    }

    /**
     * Gives player a Paintball stack and Coin Shop (if it is true)
     * This must be explicitly called since it is not overriding the subclass method
     */
    public void giveItems() {
        PlayerInventory inv = player.getInventory();

        inv.setArmorContents(Utils.colorLeatherItems(team, new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
        CoinItems.getCoinItems().getMainItem().giveItemToPlayer(this);

        if (arena.COIN_SHOP)
            inv.setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "Coin Shop", 1));

        if (arena.ARENA_WOOL_HELMET)
            giveWoolHelmet();

        player.updateInventory();
    }

    /**
     * When a player is hit their health will go down one
     * If their health is less than 1 or equal to one,  kill them and update all scoreboard with new score
     * Otherwise just call setHealth() to do other stuff
     * @return If they player should die (0 health) or just subtract their health
     */
    public boolean hit(Team fromTeam, int damage) {
        int newHealth = health -= damage;

        if (newHealth > 0) {
            updateScoreboard();
            return false;
        } else {
            setHealth(fromTeam, newHealth);
            return true;
        }
    }

    /**
     * When this ArenaPlayer kills another ArenaPlayer.
     * @param arenaPlayer ArenaPlayer who was killed
     */
    public void kill(ArenaPlayer arenaPlayer, String action) {
        // The game is already over and they won so just do not do anything
        if (arena.getTeamScore(team) == arena.MAX_SCORE)
            return;
        kills++;
        killStreak++;

        if (killStreak > heightKillStreak)
            heightKillStreak = killStreak;

        arenaPlayer.withdraw(arena.MONEY_PER_DEATH);
        arenaPlayer.withdrawCoin(arena.COIN_PER_DEATH);
        deposit(arena.MONEY_PER_KILL);
        depositCoin(arena.COIN_PER_KILL);

        arena.incrementTeamScore(team);
        arena.broadcastMessage(THEME + player.getName() + SECONDARY + " " + action + " " + THEME + arenaPlayer.getPlayer().getName());

        // Takes player off horse or whatever they are in
        Entity vehicle = arenaPlayer.getPlayer().getVehicle();

        if (vehicle != null) {
            vehicle.eject();
            vehicle.setTicksLived(-1);
        }

        arena.updateAllScoreboard();

        // If the max score was reached set them to win
        if (reachedGoal()) {
            arena.win(Arrays.asList(team));
        }
    }

    /**
     * Adds a CoinItem to a player's inventory
     * @param item CoinItem to be added to the inventory
     */
    public void addItem(CoinItem item) {
        this.getPlayer().getInventory().addItem(item.getItemStack(this, false));
        if (item.hasExpirationTime()) {
            new ExpirationCountdown(item, this, item.getExpirationTime());
        }

        if (item.requiresMoney()) {
            withdraw(item.getMoney());
            Settings.ECONOMY.withdrawPlayer(player, item.getMoney());
        }

        if (item.requiresCoins()) {
            withdrawCoin(item.getCoins());
        }

        coinItems.put(item.getItemName(true), item);
    }

    /**
     * When ever someone changes the player's health do all this stuff
     * @param newHealth Health to be set to
     */
    public void setHealth(Team fromTeam, int newHealth) {
        health = newHealth;
        lastLocation = player.getLocation();

        // This means they died, it just changes all the values
        if (health <= 0) {
            deaths++;
            lives--;

            if (arena.FIREWORK_ON_DEATH) {
                // Shots a firework from the team who killed them
                final Firework firework = player.getWorld().spawn(player.getLocation().add(0, 4, 0), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffects(FireworkEffect.builder().withColor(fromTeam.getColor(), team.getColor()).withTrail().build());
                firework.setVelocity(firework.getVelocity().multiply(10));
                firework.setFireworkMeta(meta);
            }

            // If they have no more lives turn them into a spectator player until the game ends
            if (arena.LIVES > 0 && lives == 0) {
                arena.removePlayer(this, false);
                team.playerLeaveTeam();
                Utils.stripValues(player);
                new SpectatorPlayer(this);
                return;
            } else {
                // Reloads their settings for them to go back... Sets their health, kill streak, location, protection and updates their scoreboard
                health = arena.HITS_TO_KILL;
                killStreak = 0;
                player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
                new ProtectionCountdown(arena.SAFE_TIME, this);
                updateScoreboard();
            }
        }
    }

    /**
     * Gets the KD as a String
     * @return Correctly formatted KD
     */
    public String getKd() {
        return String.format("%.2f", Utils.divide(kills, deaths));
    }

    /**
     * Gives a player a Coin Shop
     */
    public void giveShop() {
        CoinItemHandler.getHandler().showInventory(this);
    }

    /**
     * Called whenever someone shoots a Paintball and increments their score
     * @param event The event when someone clicks an item
     */
    public void shoot(PlayerInteractEvent event) {
        incrementShots();
    }

    /**
     * Gets a CoinItem item from a display name
     * @param displayName Name of the CoinItem
     * @return CoinItem which was found
     */
    public CoinItem getItemWithName(String displayName) {
        return coinItems.get(displayName);
    }

    /**
     * Sets that a player has one the game
     */
    public void setWon() {
        isWinner = true;
    }

    public void setTie() {
        isTie = true;
    }

    /**
     * Adds money to the player's balance and to their gained money
     * @param amount Amount to be added to player's balance
     */
    public void deposit(double amount){
        if (!VAULT)
            return;

        money += amount;
        Settings.ECONOMY.depositPlayer(player, amount);
    }

    public void withdraw(double amount) {
        if (!VAULT)
            return;

        money -= amount;
        Settings.ECONOMY.withdrawPlayer(player, amount);
    }

    public void depositCoin(double amount){
        if (!arena.COIN_SHOP)
            return;

        amount *= multiplier;
        coins += amount;
    }

    public void withdrawCoin(double amount) {
        if (!arena.COIN_SHOP)
            return;

        coins -= amount;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    /*
    Getters
     */
    public int getCoins() {
        return coins;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getHealth() {
        return health;
    }

    public int getKills() {
        return kills;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public double getMoney() {
        return money;
    }

    public int getLives() {
        return lives;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public boolean isTie() {
        return isTie;
    }

    public Location getLastLocation() {
        if (lastLocation == null)
            return player.getLocation();
        return lastLocation;
    }

    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
    }


}