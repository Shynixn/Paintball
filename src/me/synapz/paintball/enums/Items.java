package me.synapz.paintball.enums;

import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Sounds;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;

public enum Items {

    TIME_WARP("Time Warp", Material.GHAST_TEAR, true, 1, "Teleports you to your\nlast death location", 0, 2, 0, "", "", Sounds.WOOD_CLICK.bukkitSound(), 0),
    SUGAR_OVERDOSE("Sugar Overdose", Material.SUGAR, true, 1, "Speeds up movement by 2x", 0, 3, 0, "", "", Sounds.BURP.bukkitSound(), 0),
    COWBOY("Cowboy", Material.LEASH, true, 1, "Puts you onto a horse to ride", 0, 3, 0, "", "", Sounds.FALL_BIG.bukkitSound(), 0),
    DUEL_WIELD("Duel Wield", Material.IRON_HOE, true, 1, "Lets you shoot from both hands", 0, 4, 120, "", "", Sounds.ARROW_HIT.bukkitSound(), 1),
    RAPID_FIRE("Rapid Fire", Material.DIAMOND_HOE, true, 1, "Allows you to left or right click\nto shoot", 0, 4, 0, "", "", Sounds.ARROW_HIT.bukkitSound(), 1),
    AK_47("AK-47", Material.GOLD_BARDING, true, 1, "Shoot with 100% accuracy\nout of an AK-47", 0, 5, 120, "", "plowed",  Utils.DEFAULT_SOUND, 1),
    FLY("Fly", Material.FLINT, false, 1, "Activates fly on click", 0, 6, 0, "", "", Sounds.ZOMBIE_PIG_HURT.bukkitSound(), 0),
    ROCKET_LAUNCHER("Rocket Launcher",  Material.DIAMOND_BARDING, true, 1, "Shoot a giant wave of Paintballs", 0, 7, 0, "", "blasted",  Sounds.BLAZE_HIT.bukkitSound(), 3),
    MINI_GUN("Mini-Gun", Material.IRON_HOE, true, 1, "High precision fast shotting gun", 0, 8, 10, "", "gunned down",  Sounds.WOOD_CLICK.bukkitSound(), 3),
    DOUBLE("2x Coins", Material.GOLD_INGOT, true, 1, "Earn 2x Coins for the rest of the game", 0, 9, 0, "", "", Sounds.BURP.bukkitSound(), 0),
    SPRAY_N_PRAY("Spray n' Pray", Material.IRON_BARDING, true, 1, "Spray tons of Paintballs\ntowards your enemies!", 0, 10, 10, "", "sprayed",  Sounds.CHICKEN_EGG_POP.bukkitSound(), 3),
    CYCLONE("Cyclone", Material.HOPPER, true, 1, "Turns you into a Paintball shooting Cyclone", 0, 11, 0, "", "winded", Sounds.ANVIL_LAND.bukkitSound(), 3),
    PAINTBALL_SHOWER("Paintball Shower", Material.GOLD_NUGGET, true, 1, "Launch 300 Paintballs\ninto the air to fall on the enemies!", 0, 15, 0, "", "ended",  Sounds.BAT_TAKEOFF.bukkitSound(), 3),
    NUKE("Nuke", Material.TNT, true, 1, "Click to kill everyone\non the other teams", 0, 24, 0, "", "nuked", Sounds.ENDERDRAGON_GROWL.bukkitSound(), 0);

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
    private final String action;
    private final Sound sound;
    private final int damage;

    Items(String n, Material mat, boolean s, int a, String desc, double m, int c, int t, String p, String ac, Sound sound, int dmg) {
        this.name = n;
        this.material = mat;
        this.amount = a;
        this.shown = s;
        this.description = desc;
        this.money = m;
        this.coins = c;
        this.time = t;
        this.permission = p;
        this.action = ac;
        this.sound = sound;
        this.damage = dmg;
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

    public String getAction() {
        return Settings.ITEMS.getAction(this);
    }

    public Sound getSound() {
        return Settings.ITEMS.getSound(this);
    }

    public int getDamage() {
        return Settings.ITEMS.getDamage(this);
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

    public String getDefaultAction() {
        return action;
    }

    public Sound getDefaultSound() {
        return sound;
    }

    public int getDefaultDamage() {
        return damage;
    }
}
