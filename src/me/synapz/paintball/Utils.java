package me.synapz.paintball;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.countdowns.ArenaStartCountdown;
import me.synapz.paintball.countdowns.GameCountdown;
import me.synapz.paintball.countdowns.GameFinishCountdown;
import me.synapz.paintball.countdowns.LobbyCountdown;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

import static org.bukkit.ChatColor.RED;

public class Utils {

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
    public static Team max(Arena a , HashMap<Team, Integer> size) {
        // Get all the sizes of each Team and assign it to numbers array
        // in case there aren't any players in the arena
        if (size.keySet().size() == 0){
            for (Team t : a.getArenaTeamList()) {
                return t;
            }
        }
        for (Team t : size.keySet()) {
            if (size.get(t) == 0) {
                return t;
            }
        }

        int[] numbers = new int[size.keySet().size()];
        int count = 0;
        for (Team t : size.keySet()) {
            numbers[count] = size.get(t);
            count++;
        }

        // calculate the largest number and assign it to largetst
        int smallest = numbers[0];
        int largetst = numbers[0];

        for(int i=1; i< numbers.length; i++)
        {
            if(numbers[i] > largetst)
                largetst = numbers[i];
            else if (numbers[i] < smallest)
                smallest = numbers[i];
        }
        // check each team in the keySet until you find the one that matches the largest, then return it as it is the greatest.
        for (Team t : size.keySet()) {
            for (Integer teamSize : size.values()) {
                if (size.get(t) == teamSize) {
                    return t;
                }
            }
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

    // TODO: for some reason this spams console like 6 times when it fails
    public static void executeQuery(String query) {
        Connection conn;
        try {
            conn = DriverManager.getConnection(Settings.HOST, Settings.USERNAME, Settings.PASSWORD);
            PreparedStatement statement = conn.prepareStatement(query);
            statement.executeQuery();
        } catch (Exception e) {
            Settings.SQL = false;
            Messenger.error(Bukkit.getConsoleSender(), "Error starting SQL. Falling back to storing values in playerdata.yml. Check config.yml's SQL settings.");
            e.printStackTrace();
        }
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
    Creates a specific amount of spaces of the length of a word
     */
    public static String makeSpaces(String fromString) {
        return makeSpaces(fromString.length());
    }

    /*
    Removes an action bar if it is in a player
     */
    public static void removeActionBar(Player player) {
        ActionBarAPI.sendActionBar(player, "");
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
                Messenger.error(player, "You are already in " + a.toString() + ChatColor.RED + ".");
                return false;
            }
        }

        // Checks to see if the arena is full
        if (arena.getLobbyPlayers().size() == arena.getMax() && arena.getMax() > 0) {
            Messenger.error(player, arena.toString() + RED + " is full!");
            return false;
        }

        if (state == Arena.ArenaState.WAITING) {
            return true;
        } else {
            Messenger.error(player, arena.toString() + ChatColor.RED + " is " + state.toString().toLowerCase() + ".");
            return false;
        }
    }

    public static int getCurrentCounter(Arena arena) {
        return (int) (ArenaStartCountdown.tasks.containsKey(arena) ? ArenaStartCountdown.tasks.get(arena).getCounter() : LobbyCountdown.tasks.containsKey(arena) ? LobbyCountdown.tasks.get(arena).getCounter() : GameCountdown.gameCountdowns.containsKey(arena) ? GameCountdown.gameCountdowns.get(arena).getCounter() : GameFinishCountdown.arenasFinishing.containsKey(arena) ? (int) GameFinishCountdown.arenasFinishing.get(arena).getCounter() : 0);

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
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }
}