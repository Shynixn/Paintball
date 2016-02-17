package me.synapz.paintball;

import com.connorlinfoot.actionbarapi.ActionBarAPI;
import me.synapz.paintball.countdowns.ArenaCountdown;
import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;

public class Utils {

    // Gets the inventory list of a player. Specifically filters out all null items
    public static List<ItemStack> getInventoryList(Player p, boolean isArmourList) {
        ItemStack[] list = isArmourList ? p.getInventory().getArmorContents() : p.getInventory().getContents();
        List<ItemStack> returnList = new ArrayList<ItemStack>();
        for (ItemStack item : list) {
            if (item != null)
                returnList.add(item);
        }
        return returnList;
    }

    // Checks to see if the arena is null
    public static boolean nullCheck(String arenaName, Arena arena, Player sender) {
        if (arena == null) {
            String error = arenaName + " is an invalid arena.";
            if (arenaName.isEmpty()) {
                error = "Enter an arena on line 3.";
            }
            Message.getMessenger().msg(sender, false, ChatColor.RED, error);
            return false;
        } else {
            return true;
        }
    }

    // Gets the game mode from an int
    public static GameMode getLastGameMode(int gamemodeValue) {
        switch (gamemodeValue) {
            case 0:
                return GameMode.SURVIVAL;
            case 1:
                return GameMode.CREATIVE;
            case 2:
                return GameMode.ADVENTURE;
            case 3:
                return GameMode.SPECTATOR;
            default: // in case Minecraft adds new GameMode, don't want errors in console.
                return GameMode.SURVIVAL;
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

    public static String makeHealth(int health, int maxHealth) {
        StringBuilder builder = new StringBuilder();
        for (int i = health; i < maxHealth; i++)
            builder.append("◯");
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

    // Divides two numbers safely
    // TODO: negative KD?
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
            Message.getMessenger().msg(Bukkit.getConsoleSender(), true, ChatColor.RED, "Error starting SQL. Falling back to storing values in playerdata.yml. Check config.yml's SQL settings.");
            e.printStackTrace();
        }
    }

    public static String makeSpaces(int spaces) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String makeSpaces(String fromString) {
        return makeSpaces(fromString.length());
    }

    public static void removeActionBar(Player player) {
        ActionBarAPI.sendActionBar(player, "");
    }

    public static boolean canJoin(Player player, Arena arena) {
        String error = "";
        switch (arena.getState()) {
            case IN_PROGRESS:
                error = arena.toString() + ChatColor.RED + " is currently in progress.";
                break;
            case STOPPING:
                error = arena.toString() + ChatColor.RED + " is currently in progress.";
                break;
            case STARTING:
                error = arena.toString() + ChatColor.RED + " is currently in progress.";
                break;
            case NOT_SETUP:
                error = arena.toString() + ChatColor.RED + " has not been fully setup.";
                break;
            case DISABLED:
                error = arena.toString() + ChatColor.RED + " is disabled.";
                break;
            default:
                break;
        }
        if (error.isEmpty()) {
            return true;
        } else {
            Message.getMessenger().msg(player, false, ChatColor.RED, arena.toString() + ChatColor.RED + " is currently in progress.");
            return false;
        }
    }
}