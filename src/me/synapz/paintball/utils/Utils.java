package me.synapz.paintball.utils;

import com.connorlinfoot.bountifulapi.BountifulAPI;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.ArenaManager;
import me.synapz.paintball.countdowns.PaintballCountdown;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.bukkit.ChatColor.RED;

public class Utils {

    public static Sound DEFAULT_SOUND;
    private static BlockFace[] radial = { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

    static {
        try {
            DEFAULT_SOUND = Sound.BLOCK_LEVER_CLICK;
        } catch (NoSuchFieldError exc) {
            // Sound did not load... try to load the 1.8 and lower sound
            DEFAULT_SOUND = Sound.valueOf("CLICK");
        }
    }

    // Checks to see if the arena is null
    public static boolean nullCheck(String arenaName, Arena arena, Player sender) {
        if (arena == null) {
            String error = arenaName + " is an invalid arena.";
            if (arenaName.isEmpty()) {
                error = "Enter an arena on line 3.";
            }
            Messenger.error(sender, error);
            return false;
        } else {
            return true;
        }
    }

    // Returns an Array[] of items to an ArrayList
    public static ArrayList<String> addItemsToArray(ArrayList<String> array, String... s) {
        for (String str : s) {
            if (str != "") {
                array.add(str);
            }
        }
        return array;
    }

    // Makes wool from DyeColor and adds a name
    public static ItemStack makeWool(String name, DyeColor color) {
        ItemStack wool = new Wool(color).toItemStack(1);
        ItemMeta woolMeta = wool.getItemMeta();

        woolMeta.setDisplayName(name);
        wool.setItemMeta(woolMeta);

        return wool;
    }

    // Makes wool from DyeColor and name, also adds lore. ex: 6/6 Full, so players can see if they can join
    public static ItemStack makeWool(String name, DyeColor color, Team team) {
        ItemStack wool = makeWool(name, color);
        ItemMeta meta = wool.getItemMeta();
        List<String> newLore = new ArrayList<String>(){{
            add(ChatColor.RESET + "" + ChatColor.GRAY + team.getSize() + "/" + team.getMax());
            if (team.isFull())
                add(ChatColor.RED + "Team is full");
        }};
        meta.setLore(newLore);
        wool.setItemMeta(meta);
        return wool;
    }

    public static ItemStack makeBanner(String name, DyeColor color) {
        ItemStack banner = new ItemStack(Material.BANNER, 1);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        meta.setDisplayName(name);
        meta.setBaseColor(color);
        banner.setItemMeta(meta);

        return banner;
    }

    public static void createFlag(Team team, Location location) {
        // Sets the location to a banner then updates the banner to the team color
        location.getBlock().setType(Material.STANDING_BANNER);
        Banner banner = (Banner) location.getBlock().getState();
        banner.setBaseColor(team.getDyeColor());

        // Turns the banner based on the yaw
        org.bukkit.material.Banner bannerData = (org.bukkit.material.Banner) banner.getData();
        bannerData.setFacingDirection(Utils.yawToFace(location.getYaw() + 180));
        banner.setData(bannerData);

        // Updates the banner
        banner.update();

    }

    public static BlockFace yawToFace(float yaw) {
        return radial[Math.round(yaw / 45f) & 0x7];
    }

    public static double secondsToMin(int seconds) {
        return seconds/60;
    }

    public static String makeHealth(int health) {
        StringBuilder builder = new StringBuilder();
        for (int i = health; i > 0; i--)
            builder.append("●");
        return builder.toString();
    }

    public static ItemStack makeItem(Material type, String name, int amount) {
        ItemStack item = new ItemStack(type, amount);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        return item;
    }

    // Gets the team with the least amount of players
    public static Team max(Arena a) {
        List<Integer> intList = new ArrayList<Integer>() {{
            for (Team team : a.getArenaTeamList())
                add(team.getSize());
        }};

        Collections.sort(intList);

        int maxSize = intList.get(0);

        for (Team team : a.getArenaTeamList()) {
            if (team.getSize() == maxSize)
                return team;
        }

        return null;
    }

    /*
    Safely divides two numbers since the denominator can never be 0
     */
    public static double divide(int numerator, int denominator) {
        if (denominator == 0)
            return numerator;

        float n = (float) numerator;
        float d = (float) denominator;
        return (n / d);
    }

    public static int randomNumber(int to) {
        Random generator = new Random();
        return 1+generator.nextInt(to);
    }

    /*
    Easily sees if an item is equal to the given string
     */
    public static boolean equals(ItemStack item, String name) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(name);
    }

    /*
    Easily sees if an item name contains the given string
     */
    public static boolean contains(ItemStack item, String name) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(name);
    }

    /*
    Creates a specific amount of spaces based on set amount of spaces
     */
    public static String makeSpaces(int spaces) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    /*
    Creates a helix out of particales, yay
     */
    public static void createHelix(Player player) {
        Location loc = player.getLocation();
        int radius = 1;
        for(double y = 0; y <= 3; y+=0.01) {
            double x = radius * Math.cos(y*10);
            double z = radius * Math.sin(y*10);

            loc.getWorld().playEffect(new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y, loc.getZ() + z), Effect.HAPPY_VILLAGER, 1);
        }
    }

    /*
    Creates a specific amount of spaces of the length of a word
     */
    public static String makeSpaces(String fromString) {
        return makeSpaces(fromString.length());
    }

    /*
    Removes an action bar if it is in a player
     */
    public static void removeActionBar(Player player) {
        BountifulAPI.sendActionBar(player, "");
    }

    /*
    Checks to see if a player can join if
    - Already in the arena
    - Arena is not full
    - The state is not waiting
     */
    public static boolean canJoin(Player player, Arena arena) {
        Arena.ArenaState state = arena.getState();

        for (Arena a : ArenaManager.getArenaManager().getArenas().values()) {
            if (a.containsPlayer(player)) {
                Messenger.error(player, "You are already in " + a.toString(RED) + ".");
                return false;
            }
        }

        // Checks to see if the arena is full
        if (arena.getLobbyPlayers().size() == arena.getMax() && arena.getMax() > 0) {
            Messenger.error(player, arena.toString(RED) + " is full!");
            return false;
        }

        if (state == Arena.ArenaState.WAITING) {
            return true;
        } else {
            Messenger.error(player, arena.toString(RED) + " is " + state.toString().toLowerCase() + ".");
            return false;
        }
    }

    public static int getCurrentCounter(Arena arena) {
        return (int) (PaintballCountdown.tasks.containsKey(arena) ? PaintballCountdown.tasks.get(arena).getCounter() : -1);
    }

    /*
    Removes all values from a player including
    - EXP Values
    - Inventory
    - Armour Contents
    - GameMode
    - Flying
    - Food
    - Fire ticks
    - Health
    - Potion effects
     */
    public static void stripValues(Player player) {
        ExperienceManager exp = new ExperienceManager(player);
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        exp.setExp(0);
        player.updateInventory();
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

    // Shoots a Snowball with the correct speed
    public static void shootSnowball(Player player, Arena arena, double accuracy) {
        Projectile pr = player.launchProjectile(Snowball.class);

        Vector v = player.getLocation().getDirection();
        v.add(new Vector(Math.random() * accuracy - accuracy,Math.random() * accuracy - accuracy,Math.random() * accuracy - accuracy));
        v.subtract(new Vector(Math.random() * accuracy - accuracy,Math.random() * accuracy - accuracy,Math.random() * accuracy - accuracy));
        v.multiply(arena.SPEED);
        pr.setVelocity(v);
    }
}