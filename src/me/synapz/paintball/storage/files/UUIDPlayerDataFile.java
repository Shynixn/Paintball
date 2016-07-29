package me.synapz.paintball.storage.files;

import me.synapz.paintball.Paintball;
import me.synapz.paintball.locations.PlayerLocation;
import me.synapz.paintball.storage.PlayerData;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.ExperienceManager;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Jeremy Lugo on 7/24/2016.
 */
public class UUIDPlayerDataFile extends PaintballFile{

    private final UUID uuid;
    private Scoreboard cachedScoreboard;

    public UUIDPlayerDataFile(UUID uuid) {
        super(Paintball.getInstance(), "/playerdata/" + uuid + ".yml");
        this.uuid = uuid;

        Settings.getSettings().getPlayerDataFolder().addPlayerFile(this);
    }

    // Saves player information to PlayerData file
    // Called when the player enters an arena
    public void savePlayerInformation() {
        Player player = Bukkit.getPlayer(uuid);
        ExperienceManager exp = new ExperienceManager(player);

        new PlayerLocation(this, player.getLocation());

        if (player.getScoreboard() != null)
            cachedScoreboard = player.getScoreboard();

        fileConfig.set("Gamemode", player.getGameMode().toString());
        fileConfig.set("Food", player.getFoodLevel());
        fileConfig.set("Health", player.getHealth());
        fileConfig.set("Health-Scale", player.getHealthScale());
        fileConfig.set("Exp", exp.getCurrentExp());
        fileConfig.set("Allow-Flight", player.getAllowFlight());
        fileConfig.set("Flying", player.isFlying());
        fileConfig.set("Speed", player.getWalkSpeed());

        if (Paintball.IS_1_9) {
            fileConfig.set("Inventory", Arrays.asList(player.getInventory().getStorageContents()));
        } else {
            fileConfig.set("Inventory", Arrays.asList(player.getInventory().getContents()));
        }

        fileConfig.set("Armour", Arrays.asList(player.getInventory().getArmorContents()));

        this.saveFile();
        Utils.stripValues(player);
    }

    // Restores all of the player's settings, then sets the info to null
    public void restorePlayerInformation(boolean stripValues) {
        Player player = Bukkit.getPlayer(uuid);

        ExperienceManager exp = new ExperienceManager(player);

        if (stripValues)
            Utils.stripValues(player);

        if (cachedScoreboard != null)
            player.setScoreboard(cachedScoreboard);
        else
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        if (!PlayerData.reset(player)) {
            player.teleport(new PlayerLocation(this).getLocation());
            player.setFoodLevel(fileConfig.getInt("Food"));
            player.setGameMode(GameMode.valueOf(fileConfig.getString("Gamemode")));
            player.setAllowFlight(fileConfig.getBoolean("Allow-Flight"));
            player.setFlying(fileConfig.getBoolean("Flying"));
            player.setWalkSpeed((float) fileConfig.getDouble("Speed"));
            exp.setExp(fileConfig.getInt("Exp"));
            double health = fileConfig.getDouble("Health");
            double scale = fileConfig.getDouble("Health-Scale");
            if (health > 20d || health < 0) {
                player.setHealth(20);
            } else {
                player.setHealth(health);
            }

            player.setHealthScale(scale);

            player.getInventory().setContents(getLastInventoryContents("Inventory"));
            player.getInventory().setArmorContents(getLastInventoryContents("Armour"));
            player.updateInventory();
        }
        Settings.getSettings().getPlayerDataFolder().removePlayerFile(uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    private ItemStack[] getLastInventoryContents(String path) {
        return (ItemStack[]) fileConfig.getList(path).toArray();
    }
}
