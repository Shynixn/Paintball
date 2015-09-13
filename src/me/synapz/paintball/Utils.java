package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

    public static void removePlayerSettings(Player player) {
        // todo: exp saves
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
    }

    public static List<ItemStack> getInventoryList(Player p, boolean isArmourList) {
        ItemStack[] list = isArmourList ? p.getInventory().getArmorContents() : p.getInventory().getContents();
        List<ItemStack> returnList = new ArrayList<ItemStack>();
        for (ItemStack item : list) {
            if (item != null) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    public static Team stringToTeam(Arena a, String team) {
        for (Team t : a.getArenaTeamList()) {
            if (t.getTitleName().equalsIgnoreCase(team)) {
                return t;
            }
        }
        return null;
    }

    public static boolean nullCheck(String arenaName, Arena arena, Player sender) {
        if (arena == null) {
            Message.getMessenger().msg(sender, ChatColor.RED, arenaName + " is an invalid arena.");
            return false;
        } else {
            return true;
        }
    }

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

    public static void setAllSpeeds(Player p, float speed) {
        p.setWalkSpeed(speed);
        p.setFlySpeed(speed);
    }


    public static void countdown(final Arena a, final int seconds, final Set<PbPlayer> pbPlayers) {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Paintball");

        new CountdownTask(seconds, a, pbPlayers).runTaskTimer(plugin, 0, 20);

        for (PbPlayer pb : pbPlayers) {
            setAllSpeeds(pb.getPlayer(), 0.0F);
        }
    }

    public static ArrayList<String> addItemsToArray(ArrayList<String> array, String... s) {
        for (String str : s) {
            if (str != "") {
                array.add(str);
            }
        }
        return array;
    }

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
