package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.coin.CoinItem;
import me.synapz.paintball.coin.CoinItemHandler;
import me.synapz.paintball.coin.CoinItems;
import me.synapz.paintball.countdowns.*;
import me.synapz.paintball.enums.*;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.storage.files.UUIDFile;
import me.synapz.paintball.utils.Title;
import me.synapz.paintball.utils.Utils;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.VAULT;

public class ArenaPlayer extends PaintballPlayer {

    protected UUIDFile uuidFile;

    private Map<Items, Integer> usesPerPlayer = new HashMap<>();
    private Map<String, CoinItem> coinItems = new HashMap<>();

    private Horse horse;
    private CoinItem horseItem;

    private CoinItem lastClickedItem;
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
    private int multiplier = 1;

    private Location lastLocation;

    private boolean isWinner;
    private boolean isTie;

    public ArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer.getArena(), lobbyPlayer.getTeam(), lobbyPlayer.getPlayer());

        for (Items item : Items.values()) {
            if (item.getUsesPerPlayer() > -1) {
                usesPerPlayer.put(item, 0);
            }
        }

        this.uuidFile = new UUIDFile(player.getUniqueId());
    }

    public ArenaPlayer(SpectatorPlayer sp, Team team) {
        super(sp.getArena(), team, sp.getPlayer(), true);
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

        // If it is in progress, it is a spectator player from a death event so give their items, otherwise dont give there items
        giveItems = arena.getState() == Arena.ArenaState.IN_PROGRESS ? true : false;
        health = arena.HITS_TO_KILL;
        lives = arena.LIVES;

        player.setHealthScale(arena.HITS_TO_KILL*2); // times two because one health is a half heart, we want full hearts
        player.setHealth(player.getMaxHealth());

        for (ArenaPlayer arenaPlayer : arena.getAllArenaPlayers()) {
            Player player = arenaPlayer.getPlayer();

            this.player.showPlayer(player);
        }
    }

    @Override
    protected void showMessages() {

    }

    @Override
    public PaintballScoreboard createScoreboard() {
        // Having a money amount being #.# is ugly, i like #.## better.
        DecimalFormat formatter = new DecimalFormat("#.##");
        formatter.setMinimumFractionDigits(2);

        double bal;

        if (Settings.VAULT)
            bal = Settings.ECONOMY.getBalance(player);
        else
            bal = 0;

        PaintballScoreboard sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
                .addTeams(false)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.MONEY, shortenMoney(bal), Settings.VAULT)
                .addLine(ScoreboardLine.KD, "0.00")
                .addLine(ScoreboardLine.COIN, 0, arena.COINS)
                .addLine(ScoreboardLine.KILL_STREAK, 0)
                .addLine(ScoreboardLine.KILLS, 0)
                .addLine(ScoreboardLine.LINE)
                .addLine(ScoreboardLine.HEALTH, Utils.makeHealth(arena.HITS_TO_KILL));
        if (arena.LIVES > 0)
            sb.addLine(ScoreboardLine.LIVES, Utils.makeHealth(arena.LIVES));
        sb.addLine(ScoreboardLine.WAGER, arena.CURRENCY + formatter.format(arena.getWagerManager().getWager()), Settings.VAULT);
        return sb.build();
    }

    @Override
    public void updateScoreboard() {
        if (pbSb == null)
            return;

        double bal = Settings.VAULT ? Settings.ECONOMY.getBalance(player) : 0;

        int size = arena.getArenaTeamList().size()-1;

        pbSb.reloadTeams(false);

        if (Settings.VAULT)
            pbSb.reloadLine(ScoreboardLine.MONEY, shortenMoney(bal), size+2, Settings.VAULT);
        else
            size--;

        pbSb.reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.COIN, String.valueOf(getCoins()), size+4, arena.COINS);

        if (!arena.COINS)
            size--;

        pbSb.reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6)
                .reloadLine(ScoreboardLine.HEALTH, Utils.makeHealth(health), size+8)
                .reloadLine(ScoreboardLine.LIVES, Utils.makeHealth(lives), size+9, arena.LIVES > 0)
                .reloadLine(ScoreboardLine.WAGER, arena.CURRENCY + arena.getWagerManager().getWager(), arena.LIVES > 0 ? size+10 : size+9);

    }

    @Override
    public void leave() {
        super.leave();
        team.playerLeaveTeam();

        arena.remakeSpectatorInventory();

        if (horse != null) {
            horse.getInventory().clear();
            horse.setHealth(0);
        }

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
            uuidFile.addToStat(StatType.TIME_PLAYED, timePlayed);
        }

        uuidFile.incrementStat(StatType.GAMES_PLAYED, this);
        uuidFile.addToStat(StatType.HITS, hits);
        uuidFile.addToStat(StatType.SHOTS, shots);
        uuidFile.addToStat(StatType.KILLS, kills);
        uuidFile.addToStat(StatType.DEATHS, deaths);

        // killstreak is less than past killstreak, return
        if (uuidFile.getFileConfig().getInt(StatType.HIGEST_KILL_STREAK.getPath()) < heightKillStreak)
            uuidFile.setStat(StatType.HIGEST_KILL_STREAK, heightKillStreak);

        if (stopGame())
            arena.win(Arrays.asList(arena.getAllArenaPlayers().get(0).getTeam()));

        uuidFile.saveFile();
    }

    public void incrementHits() {
        hits++;
    }

    public void incrementShots() {
        shots++;
    }

    public CoinItem getLastClickedItem() {
        return lastClickedItem;
    }

    public void setLastClickedItem(CoinItem lastClickedItem) {
        this.lastClickedItem = lastClickedItem;
    }

    public void setHorse(CoinItem item, Horse horse) {
        this.horse = horse;
        this.horseItem = item;
    }

    public void killHorse() {
        if (horse != null && horseItem != null) {
            horse.getInventory().clear();
            horse.setHealth(0);

            horseItem.remove(this);
            addItem(horseItem);
        }
    }

    /**
     * Gives player a Paintball stack and Coin Shop (if it is true)
     * This must be explicitly called since it is not overriding the subclass method
     */
    public void giveItems() {
        PlayerInventory inv = player.getInventory();

        inv.setArmorContents(Utils.colorLeatherItems(team, new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
        CoinItems.getCoinItems().getDefaultItem().giveItemToPlayer(this);

        if (arena.COIN_SHOP)
            inv.setItem(8, Utils.makeItem(arena.COIN_SHOP_TYPE, Messages.ARENA_SHOP_NAME.getString(), 1));

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
            double health = (20 / arena.HITS_TO_KILL) * newHealth;
;
            if (health > 0)
                player.setHealth(health);

            updateScoreboard();
            return false;
        } else {
            setHealth(fromTeam, newHealth);
            player.setHealth(player.getMaxHealth());
            return true;
        }
    }

    public Map<Items, Integer> getUsesPerPlayer() {
        return usesPerPlayer;
    }

    public void incrementCoinUsePerPlayer(CoinItem coinItem) {
        if (usesPerPlayer.containsKey(coinItem.getCoinEnumItem())) {
            Items items = coinItem.getCoinEnumItem();

            int pastUses = usesPerPlayer.get(items);
            int newUses = ++pastUses;

            usesPerPlayer.remove(items, pastUses);
            usesPerPlayer.put(items, newUses);
        }
    }

    /**
     * When this ArenaPlayer kills another ArenaPlayer.
     * @param arenaPlayer ArenaPlayer who was killed
     */
    public void kill(ArenaPlayer arenaPlayer, String action) {
        // The grame is already over and they won so just do not do anything
        if (arena.getTeamScore(team) == arena.MAX_SCORE)
            return;
        kills++;
        killStreak++;

        arena.sendCommands(this.getPlayer(), arena.KILL_COMMANDS);
        if (killStreak > heightKillStreak)
            heightKillStreak = killStreak;

        arenaPlayer.withdraw(arena.MONEY_PER_DEATH);
        arenaPlayer.withdrawCoin(arena.COIN_PER_DEATH);
        deposit(arena.MONEY_PER_KILL);
        depositCoin(arena.COIN_PER_KILL);

        arena.incrementTeamScore(team, true);
        sendShotMessage(action, arenaPlayer);

        arena.updateAllScoreboard();

        // If the max score was reached set them to win
        if (reachedGoal()) {
            arena.win(Arrays.asList(team));
        }
    }

    public void sendShotMessage(String action, ArenaPlayer died) {
        if (action == null) {
            action = "shot";
        }

        if (action.isEmpty()) {
            action = "shot";
        }

        arena.broadcastMessage(team.getChatColor() + player.getName() + SECONDARY + " " + action + " " + died.getTeam().getChatColor() + died.getPlayer().getName());
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

        if (coinItems == null)
            coinItems = new HashMap<String, CoinItem>() {{
                put(item.getItemName(true), item);
            }};

        if (!coinItems.containsValue(coinItems))
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
                turnToSpectator();

                if (stopGame())
                    arena.win(Arrays.asList(arena.getAllArenaPlayers().get(0).getTeam()));

                return;
            } else {
                // Reloads their settings for them to go back... Sets their health, kill streak, location, protection and updates their scoreboard
                health = arena.HITS_TO_KILL;
                killStreak = 0;
                updateScoreboard();
                killHorse();

                player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));

                new Title(Messages.ARENA_DIE_HEADER.getString(), Messages.ARENA_DIE_FOOTER.getString(), 10, 21, 10).send(player);

                new ProtectionCountdown(arena.SAFE_TIME, this);
            }
        }
    }

    public void turnToSpectator() {
        arena.removePlayer(this, false);
        team.playerLeaveTeam();
        Utils.stripValues(player);
        new SpectatorPlayer(this);

        if (arena.getAllArenaPlayers().size() <= 1)
            arena.win(Arrays.asList(((ArenaPlayer) arena.getAllArenaPlayers().toArray()[0]).getTeam()));
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
        amount *= multiplier;
        coins += amount;
    }

    public void withdrawCoin(double amount) {
        if (coins - amount >= 0)
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

    private boolean stopGame() {
        int left = 0;
        // There must be at least one team
        for (Team team : arena.getArenaTeamList()) {
            if (team.getSize() >= 1) {
                left++;
            }
        }

        // If there is less than one team with a player, end the game
        return left <= 1 && arena.getAllPlayers().keySet().size() >= 1;
    }

    private String shortenMoney(double money) {
        double calculatedMoney = money;
        String suffix = "";

        if (money >= 1000) {
            if (money >= 1000000) {
                calculatedMoney = money / 1000000;
                suffix = "M";
            } else {
                calculatedMoney = money / 1000;
                suffix = "K";
            }
        }

        return String.format("%s%.2f%s", arena.CURRENCY, calculatedMoney, suffix);
    }

    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
    }


}