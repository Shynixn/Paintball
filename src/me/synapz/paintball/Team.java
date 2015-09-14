package me.synapz.paintball;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import static org.bukkit.Color.*;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Team {

    private static Map<ChatColor, DyeColor> dyeColors = new EnumMap<ChatColor, DyeColor>(ChatColor.class){{
        put(ChatColor.DARK_BLUE, DyeColor.BLUE);
        put(ChatColor.DARK_GREEN, DyeColor.GREEN);
        put(ChatColor.DARK_AQUA, DyeColor.CYAN);
        put(ChatColor.DARK_RED, DyeColor.RED);
        put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
        put(ChatColor.GOLD, DyeColor.ORANGE);
        put(ChatColor.GRAY, DyeColor.SILVER);
        put(ChatColor.DARK_GRAY, DyeColor.GRAY);
        put(ChatColor.BLUE, DyeColor.LIGHT_BLUE);
        put(ChatColor.BLACK, DyeColor.BLACK);
        put(ChatColor.GREEN, DyeColor.LIME);
        put(ChatColor.AQUA, DyeColor.CYAN);
        put(ChatColor.RED, DyeColor.RED);
        put(ChatColor.LIGHT_PURPLE, DyeColor.MAGENTA);
        put(ChatColor.YELLOW, DyeColor.YELLOW);
        put(ChatColor.WHITE, DyeColor.WHITE);
    }};

    private static Map<ChatColor, Color> colors = new EnumMap<ChatColor, Color>(ChatColor.class) {{
        put(ChatColor.DARK_BLUE, NAVY);
        put(ChatColor.DARK_GREEN, GREEN);
        put(ChatColor.DARK_AQUA, TEAL);
        put(ChatColor.DARK_RED, MAROON);
        put(ChatColor.DARK_PURPLE, PURPLE);
        put(ChatColor.GOLD, ORANGE);
        put(ChatColor.GRAY, SILVER);
        put(ChatColor.DARK_GRAY, GRAY);
        put(ChatColor.BLUE, BLUE);
        put(ChatColor.BLACK, BLACK);
        put(ChatColor.GREEN, LIME);
        put(ChatColor.AQUA, AQUA);
        put(ChatColor.RED, RED);
        put(ChatColor.LIGHT_PURPLE, FUCHSIA);
        put(ChatColor.YELLOW, YELLOW);
        put(ChatColor.WHITE, WHITE);
    }};

    private static Map<ChatColor, String> colorNames = new EnumMap<ChatColor, String>(ChatColor.class){{
        put(ChatColor.DARK_BLUE, "Navy Blue");
        put(ChatColor.DARK_GREEN, "Green");
        put(ChatColor.DARK_AQUA, "Cyan");
        put(ChatColor.DARK_RED, "Red");
        put(ChatColor.DARK_PURPLE, "Purple");
        put(ChatColor.GOLD, "Orange");
        put(ChatColor.GRAY, "Silver");
        put(ChatColor.DARK_GRAY, "Gray");
        put(ChatColor.BLUE, "Blue");
        put(ChatColor.BLACK, "Black");
        put(ChatColor.GREEN, "Green");
        put(ChatColor.AQUA, "Aqua");
        put(ChatColor.RED, "light Red");
        put(ChatColor.LIGHT_PURPLE, "Magenta");
        put(ChatColor.YELLOW, "Yellow");
        put(ChatColor.WHITE, "White");
    }};


    public static List<String> LIST = Arrays.asList("§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f");
    private static Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

    private ChatColor color;
    private Arena arena;

    public Team (Arena a, String colorCode) {
        this.arena = a;
        this.color = ChatColor.getByChar(colorCode.charAt(1));

        if (sb.getTeam(this.getTitleName()) == null) {
            sb.registerNewTeam(this.getTitleName()).setPrefix(color + "");
        }
    }

    public static Scoreboard getPluginScoreboard() {
        return sb;
    }

    public String getPath() {
        return "Arenas." + arena.getDefaultName() + ".Team-Locations." + this.getTitleName();
    }

    public ChatColor getChatColor() {
        return color;
    }

    public Color getColor() {
        return colors.get(color);
    }

    public String getTitleName() {
        return colorNames.get(color);
    }

    public DyeColor getDyeColor() {
        return dyeColors.get(color);
    }
}
