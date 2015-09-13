package me.synapz.paintball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.Color.*;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.scoreboard.Scoreboard;

import java.util.EnumMap;
import java.util.Map;

public class Team {

    public static String[] LIST = new String[] {"§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f"};
    private static Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

    private String color;
    private Arena arena;

    public Team (Arena a, String color) {
        this.arena = a;
        this.color = color;

        if (sb.getTeam(this.getTitleName()) == null) {
            sb.registerNewTeam(this.getTitleName()).setPrefix(this.getChatColor());
        }
    }

    public static Scoreboard getPluginScoreboard() {
        return sb;
    }

    public String getPath() {
        return "Arenas." + arena.getDefaultName() + ".Team-Locations." + this.getTitleName();
    }

    public String getChatColor() {
        return color;
    }

    public Color getColor() {
        Color[] colors = new Color[] {Color.NAVY, GREEN, TEAL, MAROON, PURPLE, ORANGE, SILVER, GRAY, BLUE, BLACK, LIME, AQUA, RED, FUCHSIA, YELLOW, WHITE};
        Color color = null;
        int i = 0;
        for (String s : LIST) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = colors[i];
            }
            i++;
        }
        return color;
    }

    public String getTitleName() {
        String color = "error";
        String[] names = new String[] {"Navy", "Green", "Teal", "Red", "Purple", "Orange", "Silver", "Gray", "Blue", "Black", "Lime", "Aqua", "LightRed", "Magenta", "Yellow", "White"};
        int i = 0;
        for (String s : LIST) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = names[i];
                break;
            }
            i++;
        }
        return color;
    }

    public DyeColor getDyeColor() {
        DyeColor[] colors = new DyeColor[] {DyeColor.BLUE, DyeColor.GREEN, DyeColor.CYAN, DyeColor.RED, DyeColor.PURPLE, DyeColor.ORANGE, DyeColor.SILVER, DyeColor.GRAY, DyeColor.LIGHT_BLUE, DyeColor.BLACK, DyeColor.LIME, DyeColor.CYAN, DyeColor.RED, DyeColor.MAGENTA, DyeColor.YELLOW, DyeColor.WHITE};
        DyeColor color = null;
        int i = 0;
        for (String s : LIST) {
            if (s.toCharArray()[1] == getChatColor().toCharArray()[1]) {
                color = colors[i];
            }
            i++;
        }
        return color;
    }
}
