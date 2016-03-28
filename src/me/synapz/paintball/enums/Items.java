package me.synapz.paintball.enums;

import me.synapz.paintball.storage.Settings;
import org.bukkit.Material;
import org.bukkit.Sound;

public enum Items {

    TIME_WARP("Time Warp", Material.GHAST_TEAR, true, 1, "Teleports you to your\nlast death location", 0, 2, 0, "", Sound.UI_BUTTON_CLICK),
    SUGAR_OVERDOSE("Sugar Overdose", Material.SUGAR, true, 1, "Speeds up movement by 2x", 0, 3, 0, "", Sound.ENTITY_PLAYER_BURP),
    AK_47("AK-47", Material.GOLD_BARDING, true, 1, "Shoot with 100% accuracy\nout of an AK-47", 0, 5, 120, "", Sound.UI_BUTTON_CLICK),
    ROCKET_LAUNCHER("Rocket Launcher",  Material.DIAMOND_BARDING, true, 1, "Shoot a giant wave of Paintballs", 0, 7, 0, "", Sound.UI_BUTTON_CLICK),
    MINI_GUN("Mini-Gun", Material.IRON_HOE, true, 1, "High precision fast shotting gun", 0, 8, 10, "", Sound.UI_BUTTON_CLICK),
    SPRAY_N_PRAY("Spray n' Pray", Material.IRON_BARDING, true, 1, "Spray tons of Paintballs\ntowards your enemies!", 0, 10, 10, "", Sound.UI_BUTTON_CLICK),
    PAINTBALL_SHOWER("Paintball Shower", Material.GOLD_NUGGET, true, 1, "Launch 200 Paintballs\ninto the air to fall on the enemies!", 0, 15, 0, "", Sound.UI_BUTTON_CLICK),
    NUKE("Nuke", Material.TNT, true, 1, "Click to kill everyone\non the other teams", 0, 24, 0, "", Sound.UI_BUTTON_CLICK);

    @Override
    public String toString() {
        return super.toString().replace("_", "-").toLowerCase();
    }

    private final String name;
    private final Material material;
    private final int amount;
    private final boolean shown;
    private final String description;
    private final double money;
    private final int coins;
    private final int time;
    private final String permission;
    private final Sound sound;

    Items(String n, Material mat, boolean s, int a, String desc, double m, int c, int t, String p, Sound sound) {
        this.name = n;
        this.material = mat;
        this.amount = a;
        this.shown = s;
        this.description = desc;
        this.money = m;
        this.coins = c;
        this.time = t;
        this.permission = p;
        this.sound = sound;
    }

    public String getName() {
        return Settings.ITEMS.getName(this);
    }

    public Material getMaterial() {
        return Settings.ITEMS.getMaterial(this);
    }

    public int getAmount() {
        return Settings.ITEMS.getAmount(this);
    }

    public boolean isShown() {
        return Settings.ITEMS.isShown(this);
    }

    public String getDescription() {
        return Settings.ITEMS.getDescription(this);
    }

    public double getMoney() {
        return Settings.ITEMS.getMoney(this);
    }

    public int getCoins() {
        return Settings.ITEMS.getCoins(this);
    }

    public int getTime() {
        return Settings.ITEMS.getTime(this);
    }

    public String getPermission() {
        return Settings.ITEMS.getPermission(this);
    }

    public Sound getSound() {
        return Settings.ITEMS.getSound(this);
    }

    public String getDefaultName() {
        return name;
    }

    public Material getDefaultMaterial() {
        return material;
    }

    public int getDefaultAmount() {
        return amount;
    }

    public boolean getDefaultShown() {
        return shown;
    }

    public String getDefaultDescription() {
        return description;
    }

    public double getDefaultMoney() {
        return money;
    }

    public int getDefaultCoins() {
        return coins;
    }

    public int getDefaultTime() {
        return time;
    }

    public String getDefaultPermission() {
        return permission;
    }

    public Sound getDefaultSound() {
        return sound;
    }
}
