package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import static org.bukkit.ChatColor.*;

import org.bukkit.*;
<<<<<<< HEAD
import org.bukkit.entity.Entity;
=======
>>>>>>> master
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
<<<<<<< HEAD
=======
import org.bukkit.scoreboard.Team;
>>>>>>> master

public class PbPlayer {

    Player player;
    Team team;
    Arena arena;

    public PbPlayer(Player p, Team t, Arena a) {
        this.player = p;
        this.team = t;
        this.arena = a;

        p.sendMessage("Initializing...");
        initPlayer();
    }

    public String getName() {
        return player.getName();
    }

    public void addHelmet() {
        if (!Settings.WOOL_HELMET) {
            return;
        }

<<<<<<< HEAD
        ItemStack wool = new Wool(team.getDyeColor()).toItemStack(1);
=======
        player.sendMessage("Adding Wool Helmet...");
        DyeColor color = team == ArenaManager.Team.BLUE ? DyeColor.BLUE : DyeColor.RED;
        String name = color == DyeColor.BLUE ? BLUE + "Blue Helmet": RED + "Red Helmet";

        // Set the name of the helmet
        ItemStack wool = new Wool(color).toItemStack(1);
>>>>>>> master
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName(team.getChatColor() + team.getTitleName() + " Team");
        wool.setItemMeta(woolMeta);

        player.getInventory().setHelmet(wool);
    }

    public Player getPlayer() {
        return player;
    }
<<<<<<< HEAD

    public void giveItems() {
        // TODO add event for when hit to change the color based on how many times they were hit
        PlayerInventory inv = player.getInventory();
        inv.setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
=======
    public void giveItems() {
        // TODO add event for when hit to change the color based on how many times they were hit
        player.sendMessage("Adding Armour...");
        PlayerInventory inv = player.getInventory();
        // TODO add blue/red armour
        // helmet maybe overridden with a wool helmet
        inv.setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));
        // todo: give custom config items
>>>>>>> master
    }

    // todo: make a list of projectiles to pick from, rile, snowball etc and put paramters here
    public void launchProjectile() {
        // todo:make spawn faster
        player.launchProjectile(Snowball.class).setVelocity(player.getLocation().getDirection().multiply(3));
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

<<<<<<< HEAD
    private ItemStack[] colorLeatherItems(ItemStack... items) {
        int location = 0;
        ItemStack[] editedItems = new ItemStack[items.length];
        for (ItemStack item : items) {
            ItemStack armour = new ItemStack(item.getType(), 1);
            LeatherArmorMeta lam = (LeatherArmorMeta)armour.getItemMeta();
            lam.setColor(team.getColor());
            lam.setDisplayName(team.getChatColor() + team.getTitleName() + " Team");
=======
        ChatColor color = team == ArenaManager.Team.RED ? ChatColor.RED : ChatColor.BLUE;
        final Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        sb.registerNewTeam(ArenaManager.Team.RED.toString());
        sb.registerNewTeam(ArenaManager.Team.BLUE.toString());
        // Make the Scoreboard information for the player
        Objective ob = sb.registerNewObjective("Prefixs", "dummy");
        ob.setDisplaySlot(DisplaySlot.BELOW_NAME);
        sb.registerNewTeam(team.toString());
        final Team playerTeam = sb.getTeam(team.toString());
        playerTeam.setPrefix(color + "ttt");

    }

    private ItemStack[] colorLeatherItems(ItemStack... items) {
        int location = 0;
        ItemStack[] editedItems = new ItemStack[items.length];
        Color color = team == ArenaManager.Team.RED ? Color.RED : Color.BLUE;
        String itemName = team == ArenaManager.Team.RED ? ChatColor.RED + "Red Team" : ChatColor.BLUE + "Blue Team";
        for (ItemStack item : items) {
            ItemStack armour = new ItemStack(item.getType(), 1);
            LeatherArmorMeta lam = (LeatherArmorMeta)armour.getItemMeta();
            lam.setColor(color);
            lam.setDisplayName(itemName);
>>>>>>> master
            armour.setItemMeta(lam);
            editedItems[location] = armour; location++;
        }
        return editedItems;
    }
}