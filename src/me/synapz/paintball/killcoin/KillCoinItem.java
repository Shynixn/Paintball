package me.synapz.paintball.killcoin;

import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class KillCoinItem extends ItemStack {

    // Display name
    // Description
    // Cost
    // KillCoins
    // Expiration Time
    // Permission

    private final String name;
    private final String description;
    private final double money;
    private final int killcoins;
    private final int expirationTime;
    private final String permission;
    private final boolean showItem;

    public KillCoinItem(Material material, String name, String description, double money, int killcoins, int expirationTime, String permission, int amount, boolean showItem) {
        super(material, amount);
        this.name = name;
        this.description = description;
        this.money = money;
        this.killcoins = killcoins;
        this.expirationTime = expirationTime;
        this.permission = permission;
        this.showItem = showItem;

        KillCoinItemHandler.getHandler().addItem(this);
    }

    // Creates a KillCoinItem from config.yml based on a rawItem
    public KillCoinItem(String path, FileConfiguration file) {
        super(Material.valueOf("STICK"), file.getInt(path + ".amount")); // TODO: make material come from config.yml
        this.name = file.getString(path + ".name");
        this.description = file.getString(path + ".description");
        this.money = file.getDouble(path + ".money");
        this.killcoins = file.getInt(path + ".killcoins");
        this.expirationTime = file.getInt(path + ".expiration-time");
        this.permission = file.getString(path + ".permission-required");
        this.showItem = file.getBoolean(path + ".shown");

        KillCoinItemHandler.getHandler().addItem(this);
    }

    public String getItemName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getMoney() {
        return money;
    }

    public int getKillCoins() {
        return killcoins;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public String getPermission() {
        return permission;
    }

    public boolean showItem() {
        return showItem;
    }

    public boolean hasDescription() {
        return !(description.isEmpty());
    }

    public boolean hasExpirationTime() {
        return !(expirationTime <= 0);
    }

    public boolean requiresMoney() {
        return !(money <= 0);
    }

    public boolean requiresKillCoins() {
        return !(killcoins <= 0);
    }

    public boolean hasPermission() {
        return !(permission.equals("none"));
    }
}
