package me.synapz.paintball;


import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class PbPlayer {

    Player player;
    ArenaManager.Team team;
    Arena arena;

    /**
     * This will get called everytime a player is added into arena, we create a PbPlayer and
     * add checks on their join based on config settings and values.
     * @param p Player to make a PbPlayer
     * @param t Team of the Player
     * @param a Arena they are joining
     */
    public PbPlayer(Player p, ArenaManager.Team t, Arena a) {
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

        DyeColor color = team == ArenaManager.Team.BLUE ? DyeColor.BLUE : DyeColor.RED;
        String name = color == DyeColor.BLUE ? ChatColor.BLUE + "Blue Helmet": ChatColor.RED + "Red Helmet";

        // Set the name of the helmet
        ItemStack wool = new Wool(color).toItemStack();
        ItemMeta woolMeta = wool.getItemMeta();
        woolMeta.setDisplayName(name);
        wool.setItemMeta(woolMeta);

        player.getInventory().setHelmet(wool);
    }

    public void giveItems() {
        // todo: give custom config items
    }

    // todo: make a list of projectiles to pick from, rile, snowball etc and put paramters here
    public void launchProjectile() {
        // todo:make spawn faster
        player.launchProjectile(Snowball.class, player.getVelocity());
    }

    private void initPlayer() {
        addHelmet();
        giveItems();
    }

    private void colorPlayerTitle() {
        if (!Settings.COLOR_PLAYER_TITLE) {
            return;
        }

        // todo: color their hud
    }
}
