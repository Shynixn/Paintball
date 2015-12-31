package me.synapz.paintball.killcoin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KillCoinItem extends ItemStack {

    private final String permission;
    private final String name;
    private boolean isToShow = true;
    private boolean requiresPermission = false;

    public KillCoinItem(Material material, int amount, String permission, String name) {
        super(material, amount);

        this.permission = permission;
        this.name = name;
        KillCoinItemHandler.getHandler().addItem(this);
    }

    // Creates a KillCoinItem from config.yml based on a rawItem
    public KillCoinItem(String rawItem) {
        KillCoinItemHandler.getHandler().addItem(this);

        this.permission = "";
        this.name = "";
        this.requiresPermission = true; // todo: check if item requires permission
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public boolean requiresPermission() {
        return getPermission() != null || getPermission().isEmpty() && requiresPermission;
    }

    public boolean isToShow() {
        return isToShow;
    }

    public boolean hasExpirationTime() {
        return true;
    }

    public double requiredMoney() {
        return 20.0;
    }

    public int requiredKillCoins() {
        return 2;
    }

    public int getExpirationTime() {
        return 3;
    }

    public String getDescription() {
        return "Shoots double paintballs";
    }


}
