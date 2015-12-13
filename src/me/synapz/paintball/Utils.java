package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Utils {

    // Gets the inventory list of a player
    public static List<ItemStack> getInventoryList(Player p, boolean isArmourList) {
        ItemStack[] list = isArmourList ? p.getInventory().getArmorContents() : p.getInventory().getContents();
        List<ItemStack> returnList = new ArrayList<ItemStack>();
        for (ItemStack item : list) {
            if (item != null)
                returnList.add(item);
        }
        return returnList;
    }

    // Turns a string like 'red' in to a team
    public static Team stringToTeam(Arena a, String team) {
        for (Team t : a.getArenaTeamList()) {
            if (t.getTitleName().equalsIgnoreCase(team) || t.getTitleName().replace(" ", "").equalsIgnoreCase(team)) {
                return t;
            }
        }
        return null;
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

    // Starts a countdown
    public static void countdown(int counter, int interval, int noInterval, Arena a, String chatMessage, String screenMessage, String finishedMessage, boolean isLobbyCountDown) {
        if (!CountdownTask.arenasRunningTask.contains(a)) {
            final Plugin plugin = Bukkit.getPluginManager().getPlugin("Paintball");
            new CountdownTask(counter, interval, noInterval, a, chatMessage, screenMessage, finishedMessage, isLobbyCountDown).runTaskTimer(plugin, 0, 20);
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
}
