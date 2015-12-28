package me.synapz.paintball;

import static me.synapz.paintball.storage.Settings.*;
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

    private static Map<ChatColor, String> colorNames = new EnumMap<ChatColor, String>(ChatColor.class);

    // Set the colorNames variable to the TEAM_NAMES variable, which was set in Setting, this way we can use it here
    public static void loadTeamColors() {
        colorNames = TEAM_NAMES;
    }

    // List of all available color codes
    public static List<String> LIST = Arrays.asList("§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§a", "§b", "§c", "§d", "§e", "§f");
    // Scoreboard (This is the only scoreboard for the plugin) and it is used for coloring player HUD
    private static Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

    // Team's chat color
    private ChatColor color;
    // Team's arena it is set to
    private Arena arena;
    // Amount of players on this team, incremented whenever someone joines the team
    int size = 0;

    /**
        Used to create a new Team.
        Specifically used in SetTeam's command class when we set the arena's teams
        Used when loading an already created arena, check's the arenas already-set teams then re-creates it in memory.
        @param a - arena being set to
        @param colorCode - color of the team
     **/
    public Team (Arena a, String colorCode) {

        this.arena = a;
        this.color = ChatColor.getByChar(colorCode.charAt(1));

        if (sb.getTeam(this.getTitleName()) == null) {
            sb.registerNewTeam(this.getTitleName()).setPrefix(color + "");
        }
    }

    // Return the one plugin's scoreboard instance
    public static Scoreboard getPluginScoreboard() {
        return sb;
    }

    // Return the team's specific path in config.
    public String getPath() {
        return "Arenas." + arena.getDefaultName() + ".Team-Locations." + this.getTitleName();
    }

    // Return the team's specific ChatColor associated with it, ex: ChatColor.RED.
    public ChatColor getChatColor() {
        return color;
    }

    // Return the team's color (Helpful or setting HUD color) ex: Color.AQUA
    public Color getColor() {
        return colors.get(color);
    }

    // Returns Team's title name, basically the team's name. This value can be changed in config.yml, ex: "&6: Orange" this would return Orange
    public String getTitleName() {
        return colorNames.get(color);
    }

    // Returns dye color for this team, ex: DyeColor.BLUE;
    public DyeColor getDyeColor() {
        return dyeColors.get(color);
    }

    public void playerJoinTeam() {
        size++;
    }

    public void playerLeaveTeam() {
        size--;
    }

    // Returns if the team is full or not, if there are 100 max players, only 25 can go in per team
    public boolean isFull() {
        return size != 0 && size%getMax() == 0;
    }

    public int getMax() {
        return Math.round(arena.getMax()/arena.getArenaTeamList().size());
    }

    public int getSize() {
       return size;
    }
}
