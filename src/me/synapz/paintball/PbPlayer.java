package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;

public class PbPlayer {

    Player player;
    Team team;
    Arena arena;

    boolean won = false;
    int killstreak = 0;
    int killcoins = 0;

    public PbPlayer(Player p, Team t, Arena a) {
        this.player = p;
        this.team = t;
        this.arena = a;

        initPlayer();
    }

    public String getName() {
        return player.getName();
    }

    public void addHelmet() {
        if (!Settings.WOOL_HELMET) {
            return;
        }
        player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + "Team", team.getDyeColor()));
    }

    public Player getPlayer() {
        return player;
    }

    public void giveItems() {
        // TODO add event for when hit to change the color based on how many times they were hit
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
    }

    // todo: make a list of projectiles to pick from, rile, snowball etc and put paramters here
    public void launchProjectile() {
        // todo:make spawn faster
        // todo: if hit incriemtn hit
        player.launchProjectile(Snowball.class).setVelocity(player.getLocation().getDirection().multiply(3));
    }

    public void endMatch() {
        // todo: check if lost or soemthin
    }

    public int getKillstreak() {
        return killstreak;
    }

    public boolean ifWon() {
        return won;
    }

    private void initPlayer() {
        giveItems();
        addHelmet();
        colorPlayerTitle();
    }

    private void colorPlayerTitle() {
        if (!Settings.COLOR_PLAYER_TITLE) {
            return;
        }
        Scoreboard sb = Team.getPluginScoreboard();

        final org.bukkit.scoreboard.Team playerTeam = sb.getTeam(team.getTitleName());
        playerTeam.addPlayer(player); // TODO: try player.
        player.setScoreboard(sb);
    }

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
}