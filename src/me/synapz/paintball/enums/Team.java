package me.synapz.paintball.enums;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.locations.TeamLocation;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.ARENA_FILE;
import static org.bukkit.Color.*;

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
        put(ChatColor.BLUE, DyeColor.BLUE);
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

    public static final Map<String, String> DEFAULT_NAMES = new HashMap<String, String>() {{
        put("&0", "Black");
        put("&1", "Navy Blue");
        put("&2", "Green");
        put("&3", "Cyan");
        put("&4", "Red");
        put("&5", "Purple");
        put("&6", "Orange");
        put("&7", "Silver");
        put("&8", "Gray");
        put("&9", "Blue");
        put("&a", "Lime");
        put("&b", "Aqua");
        put("&c", "Light Red");
        put("&d", "Magenta");
        put("&e", "Yellow");
        put("&f", "White");
    }};

    // Team's chat color
    private ChatColor color;
    // Team's arena it is set to
    private Arena arena;
    // Team's name
    private String name;
    // Amount of players on this team, incremented whenever someone joines the team
    int size = 0;

    /**
        Used to create a new Team.
        Specifically used in SetTeam's command class when we set the arena's teams
        Used when loading an already created arena, check's the arenas already-set teams then re-creates it in memory.
        @param a - arena being set to
        @param  - color of the team
     **/

    public Team(Arena a) {
        this.arena = a;
        this.color = ChatColor.ITALIC;
        this.name = "Spectator";
    }

    public Team(Arena a, String colorCode, String name) {
        this.arena = a;
        this.color = ChatColor.getByChar(colorCode.charAt(1));
        this.name = name;
    }

    public Team(Arena a, String colorCode) {
        this(a, colorCode, DEFAULT_NAMES.get(colorCode.replace("§", "&")));
    }

    // Return the team's specific path in config.
    public String getPath(TeamLocation.TeamLocations type, int spawnNumber) {
        return  "Arenas." + arena.getDefaultName() + "." + type.toString() + "." + this.getConfigName() + "." + spawnNumber;
    }

    // Return the team's specific path in config.
    public String getPath() {
        return  "Arenas." + arena.getDefaultName() + "." + this.getConfigName() + ".Flag";
    }

    public int getSpawnPointsSize(TeamLocation.TeamLocations type) {
        String path = "Arenas." + arena.getDefaultName() + "." + type.toString() + "." + this.getConfigName();
        ConfigurationSection section = ARENA_FILE.getConfigurationSection(path);
        return section == null ? 0 : section.getValues(false).size();
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
        return name;
    }

    // Returns dye color for this team, ex: DyeColor.BLUE;
    public DyeColor getDyeColor() {
        return dyeColors.get(color);
    }

    public void playerJoinTeam() {
        if (size < 0)
            size = 0;

        size++;
        arena.updateAllScoreboard();
    }

    public void playerLeaveTeam() {
        if (size < 0)
            size = 1;

        size--;
        arena.updateAllScoreboard();
    }

    // Returns if the team is full or not, if there are 100 max players, only 25 can go in per team
    public boolean isFull() {
        return size != 0 && size%getMax() == 0;
    }

    public int getMax() {
        return arena.getTeamMax(this);
    }

    public int getSize() {
        if (size < 0)
            return 0;
        
       return size;
    }

    private String getConfigName() {
        return DEFAULT_NAMES.get(String.valueOf(getChatColor()).replace("§", "&")).toLowerCase().replace(" ", "");
    }
}