package me.synapz.paintball;

import org.bukkit.ChatColor;
import static org.bukkit.Color.*;

import org.bukkit.Color;
import org.bukkit.DyeColor;

public class Team {

    private static String[] list = new String[] {"§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f"};

    private String color;
    private Arena arena;

    public Team (Arena a, String color) {
        this.arena = a;
        this.color = color;
    }

    public static String[] availableColors() {
        return list;
    }

    public String getPath() {
        return "Arenas." + arena.getDefaultName() + ".Team-Locations." + this.getTitleName();
    }

    public String getChatColor() {
        return color;
    }

    public Color getColor() {
        Color[] colors = new Color[] {Color.NAVY, GREEN, TEAL, MAROON, PURPLE, ORANGE, SILVER, GRAY, BLUE, BLACK, LIME, AQUA, RED, FUCHSIA, YELLOW};
        Color color = null;
        int i = 0;
        for (String s : list) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = colors[i];
            }
            i++;
        }
        return color;
    }

    public String getTitleName() {
        String color = "error";
        String[] names = new String[] {"Navy", "Green", "Teal", "Red", "Purple", "Orange", "Silver", "Gray", "Blue", "Black", "Lime", "Aqua", "LightRed", "Magenta", "Yellow"};
        int i = 0;
        for (String s : list) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = names[i];
                break;
            }
            i++;
        }
        return color;
    }

    public DyeColor getDyeColor() {
        DyeColor[] colors = new DyeColor[] {DyeColor.BLUE, DyeColor.GREEN, DyeColor.CYAN, DyeColor.RED, DyeColor.PURPLE, DyeColor.ORANGE, DyeColor.SILVER, DyeColor.GRAY, DyeColor.LIGHT_BLUE, DyeColor.BLACK, DyeColor.LIME, null, DyeColor.RED, DyeColor.MAGENTA, DyeColor.YELLOW};
        DyeColor color = null;
        int i = 0;
        for (String s : list) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = colors[i];
            }
            i++;
        }
        return color;
    }
}
