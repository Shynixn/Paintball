package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import static me.synapz.paintball.storage.Settings.*;


public class ArenaPlayer extends PaintballPlayer {

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
        giveWoolHelmet();

        // TODO: remove, this is jsut for testing :)
        new BukkitRunnable() {
            // TODO: add counter timer
            int counter = Settings.MAX_SCORE;
            @Override
            public void run() {
                System.out.println(counter);
                if (counter == 0) {
                    shoot();
                    this.cancel();
                } else {
                    counter--;
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 1);
        // TODO: openKit menu, stop from being able to move
    }

    @Override
    public void leaveArena() {
        super.leaveArena();
        // TODO: set win to true or leave false
        Settings.getSettings().getCache().incrementStat(StatType.GAMES_PLAYED, this);
    }

    public void shoot() {
        // TODO: switch through each item, do something for each one

        if (reachedGoal()) {
            won = true;
            arena.win(team);
            // TODO: add timer and win messages
        }
    }

    // This will look into config.yml for the arena, if the time or kills is reached, they reahced the goal
    private boolean reachedGoal() {
        return arena.MAX_SCORE == arena.getTeamScore(team);
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