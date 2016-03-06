package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.countdowns.ExpirationCountdown;
import me.synapz.paintball.countdowns.ProtectionCountdown;
import me.synapz.paintball.enums.ScoreboardLine;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.killcoin.KillCoinItem;
import me.synapz.paintball.killcoin.KillCoinItemHandler;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.scoreboards.PaintballScoreboard;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
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

import static me.synapz.paintball.storage.Settings.SECONDARY;
import static me.synapz.paintball.storage.Settings.THEME;

public class ArenaPlayer extends PaintballPlayer {

    private Map<String, KillCoinItem> coinItems = new HashMap<>();

    private int killStreak;
    private int coins;
    private int deaths;
    private int kills;
    private int money;
    private int health = arena.HITS_TO_KILL;
    private int lives = arena.LIVES;

    private boolean isWinner;

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
        // TODO: Don't make random but instead have a counter
        player.teleport(arena.getLocation(TeamLocation.TeamLocations.SPAWN, team, Utils.randomNumber(team.getSpawnPointsSize(TeamLocation.TeamLocations.SPAWN))));
        giveWoolHelmet();
    }

    @Override
    protected void showMessages() {

    }

    @Override
    protected void giveWoolHelmet() {
        super.giveWoolHelmet();
    }

    @Override
    public PaintballScoreboard createScoreboard() {
        PaintballScoreboard sb = new PaintballScoreboard(this, arena.TIME, "Arena:")
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
        return sb.build();
    }

    @Override
    public void updateScoreboard() {
        if (pbSb == null)
            return;

        int size = arena.getArenaTeamList().size()-1;
        pbSb.reloadTeams(true)
                .reloadLine(ScoreboardLine.MONEY, "1", size+2)
                .reloadLine(ScoreboardLine.KD, getKd(), size+3)
                .reloadLine(ScoreboardLine.KILL_COIN, String.valueOf(getCoins()), size+4)
                .reloadLine(ScoreboardLine.KILL_STREAK, String.valueOf(getKillStreak()), size+5)
                .reloadLine(ScoreboardLine.KILLS, String.valueOf(getKills()), size+6)
                .reloadLine(ScoreboardLine.HEALTH, Utils.makeHealth(health), size+8)
                .reloadLine(ScoreboardLine.LIVES, Utils.makeHealth(lives), size+9, arena.LIVES > 0);
    }

    @Override
    public void leave() {
        super.leave();
        team.playerLeaveTeam();
        Settings.PLAYERDATA.incrementStat(StatType.GAMES_PLAYED, this);

        // TODO: What if there is 5 teams with 1 with 0 players... Make it so it
        // TODO: So it also checks for arenas with players or if 1 player in the arena
        // Looks in each team to see if there are 0 players in it...?
        for (Team team : arena.getArenaTeamList()) {
            if (team.getSize() == 0) {
                // TODO: get winner
                // arena.win(Arrays.asList(arena.getAllArenaPlayers().get(0).getTeam()));
                break;
            }
        }
    }

    /**
     * Gives player a Paintball stack and Coin Shop (if it is true)
     * This must be explicitly called since it is not overriding the subclass method
     */
    public void giveItems() {
        PlayerInventory inv = player.getInventory();

        inv.setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
        inv.setItem(0, Utils.makeItem(Material.SNOW_BALL, THEME + "Paintball", 64));

        if (arena.KILL_COIN_SHOP)
            inv.setItem(8, Utils.makeItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "Coin Shop", 1));
        player.updateInventory();
    }

    /**
     * When a player is hit their health will go down one
     * If their health is less than 1 or equal to one,  kill them and update all scoreboard with new score
     * Otherwise just call setHealth() to do other stuff
     * @return If they player should die (0 health) or just subtract their health
     */
    public boolean hit() {
        int newHealth = health--;
        pbSb.updateNametags(false);
        if (newHealth != 1) {
            arena.updateAllScoreboard();
            return false;
        } else {
            setHealth(newHealth);
            return true;
        }
    }

    /**
     * When this ArenaPlayer kills another ArenaPlayer.
     * @param arenaPlayer ArenaPlayer who was killed
     */
    public void kill(ArenaPlayer arenaPlayer) {
        // The game is already over and they won so just do not do anything
        if (arena.getTeamScore(team) == arena.MAX_SCORE)
            return;

        killStreak++;
        coins = coins + arena.KILLCOIN_PER_KILL;
        money++; // TODO: check arena settings for per kill money
        kills++;
        arena.incrementTeamScore(team);
        Settings.PLAYERDATA.incrementStat(StatType.KILLS, this);
        Settings.PLAYERDATA.incrementStat(StatType.HIGEST_KILL_STREAK, this);
        arena.broadcastMessage(THEME + player.getName() + SECONDARY + " shot " + THEME + arenaPlayer.getPlayer().getName());

        // Updates all player's scoreboards
        for (ArenaPlayer player : arena.getAllArenaPlayers()) {
            player.updateScoreboard();
        }
        // If the max score was reached set them to win
        if (reachedGoal()) {
            arena.win(Arrays.asList(team));
        }
    }

    /**
     * Adds a CoinItem to a player's inventory
     * @param item CoinItem to be added to the inventory
     */
    public void addItem(KillCoinItem item) {
        this.getPlayer().getInventory().addItem(item.getItemStack(this, false));
        if (item.hasExpirationTime()) {
            new ExpirationCountdown(item, this, item.getExpirationTime());
        }
        coinItems.put(item.getItemName(true), item);
    }

    /**
     * When ever someone changes the player's health do all this stuff
     * @param newHealth Health to be set to
     */
    public void setHealth(int newHealth) {
        health = newHealth;

        // This will set the hearts to what they should be above their name
        arena.updateAllScoreboard();

        // This means they died, it just changes all the values
        if (health == 1) {
            deaths++;
            lives--;

            if (coins - arena.KILLCOIN_PER_DEATH > 0 || coins - arena.KILLCOIN_PER_DEATH < 0 && arena.KILL_COINS_NEGATIVE)
                coins = coins - arena.KILLCOIN_PER_DEATH;
            else
                coins = 0;

            if (money - arena.KILLCOIN_PER_DEATH > 0 || coins - arena.KILLCOIN_PER_DEATH < 0 && arena.KILL_COINS_NEGATIVE)
                coins = coins - arena.KILLCOIN_PER_DEATH;
            else
                coins = 0;

            Settings.PLAYERDATA.incrementStat(StatType.DEATHS, this);

            // If they have no more lives turn them into a spectator player until the game ends
            if (arena.LIVES > 0 && lives == 0) {
                arena.removePlayer(this, false);
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
        KillCoinItemHandler.getHandler().showInventory(this);
    }

    /**
     * Called whenever someone shoots a Paintball and increments their score
     * @param event The event when someone clicks an item
     */
    public void shoot(PlayerInteractEvent event) {
        Settings.PLAYERDATA.incrementStat(StatType.SHOTS, this);
    }

    /**
     * Gets a CoinItem item from a display name
     * @param displayName Name of the CoinItem
     * @return CoinItem which was found
     */
    public KillCoinItem getItemWithName(String displayName) {
        return coinItems.get(displayName);
    }

    /**
     * Sets that a player has one the game
     */
    public void setWon() {
        isWinner = true;
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

    public int getMoney() {
        return money;
    }

    public int getLives() {
        return lives;
    }

    public boolean isWinner() {
        return isWinner;
    }

    /**
     * Colors a list of armour
     *
     * @param items Items to be dyed
     * @return Edited items to be added
     */
    private ItemStack[] colorLeatherItems(ItemStack... items) {
        int location = 0;
        ItemStack[] editedItems = new ItemStack[items.length];
        for (ItemStack item : items) {
            ItemStack armour = new ItemStack(item.getType(), 1);
            LeatherArmorMeta lam = (LeatherArmorMeta) armour.getItemMeta();
            lam.setColor(team.getColor());
            lam.setDisplayName(team.getChatColor() + team.getTitleName() + " Team");
            armour.setItemMeta(lam);
            editedItems[location] = armour;
            location++;
        }
        return editedItems;
    }

    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
    }


}
