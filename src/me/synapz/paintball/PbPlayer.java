package me.synapz.paintball;


import org.bukkit.entity.Player;

public class PbPlayer {

    Player player;
    ArenaManager.Team team;
    Arena arena;

    public PbPlayer(Player p, ArenaManager.Team t, Arena a) {
        this.player = p;
        this.team = t;
        this.arena = a;
    }

    public void addHelmet(ArenaManager.Team t) {
        // check in config if to give player helmet or not
    }

    public void slashOnShoot() {
        // check in config if to slash whenever they shoot
    }

    public void giveItems() {
        // give them snowballs/rifle that is properly named from config
    }
}
