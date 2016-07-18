package me.synapz.paintball.storage;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.players.PaintballPlayer;
import me.synapz.paintball.utils.ExperienceManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class PlayerData {

    private final ExperienceManager experienceManager;
    private final Player player;

    private final Location location;
    private final int food;
    private final GameMode gamemode;
    private final boolean allowFlight;
    private final boolean flying;
    private final double speed;
    private final int exp;
    private final double health;
    private final double healthScale;
    private final ItemStack[] inventory;
    private final ItemStack[] armmour;

    public PlayerData(PaintballPlayer paintballPlayer) {
        this.player = paintballPlayer.getPlayer();
        this.experienceManager = new ExperienceManager(player);
        this.location = player.getLocation();
        this.food = player.getFoodLevel();
        this.gamemode = player.getGameMode();
        this.allowFlight = player.getAllowFlight();
        this.flying = player.isFlying();
        this.speed = player.getWalkSpeed();
        this.exp = experienceManager.getCurrentExp();
        this.health = player.getHealth();
        this.healthScale = player.getHealthScale();

        if (Paintball.IS_1_9) {
            this.inventory = player.getInventory().getStorageContents();
        } else {
            this.inventory = player.getInventory().getContents();
        }

        this.armmour = player.getInventory().getArmorContents();
    }

    public void restore() {
        player.teleport(location);
        player.setFoodLevel(food);
        player.setGameMode(gamemode);
        player.setAllowFlight(allowFlight);
        player.setFlying(flying);
        player.setWalkSpeed((float) speed);
        experienceManager.setExp(exp);

        if (health > 20d || health < 0) {
            player.setHealth(20);
        } else {
            player.setHealth(health);
        }

        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armmour);
        player.updateInventory();
    }
}
