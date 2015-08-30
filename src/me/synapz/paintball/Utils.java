package me.synapz.paintball;

import me.synapz.paintball.storage.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class
        Utils {

    public static void removePlayerSettings(Player player) {
        // todo: exp saves
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setFoodLevel(20);
        player.setHealth(20);
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
            if (t.getTitleName().equalsIgnoreCase(team.toLowerCase())) {
                return t;
            }
        }
        return null;
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
        for (PbPlayer pb : pbPlayers) {
            setAllSpeeds(pb.getPlayer(), 0.0F);
        }
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int counter = seconds;
            public void run() {
                if (counter == 0) {
                    // TODO get better message
                    a.broadcastMessage(ChatColor.GREEN, "Paintball Arena Started!");
                    for (PbPlayer pb : pbPlayers) {
                        Utils.setAllSpeeds(pb.getPlayer(), 0.5F);
                    }
                } else if (counter < 0) {
                    Bukkit.getScheduler().cancelTasks(plugin);
                } else {
                    if (counter <= Settings.NO_INTERVAL || counter % Settings.INTERVAL == 0 || seconds == counter) {
                        a.broadcastMessage(ChatColor.GREEN, "Paintball starting in " + ChatColor.GRAY + counter + ChatColor.GREEN + " seconds!");
                    }
                }
                counter--;
            }
        }, 0L, 20L);
    }

    public static ArrayList<String> addItemsToArray(ArrayList<String> array, String... s) {
        for (String str : s) {
            if (str != "") {
                array.add(str);
            }
        }
        return array;
    }

    public static Team max(HashMap<Team, Integer> size) {
        // Get all the sizes of each Team and assign it to numbers array
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
