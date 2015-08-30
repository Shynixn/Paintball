package me.synapz.paintball;

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


    public String getTitleName() {
        String color = "error";
        String[] names = new String[] {"Blue", "Green", "Aqua", "Red", "Purple", "Yellow", "LightGray", "Gray", "LightBlue", "Black", "LightGreen", "LightAqua", "LightRed", "LightPurple", "LightYellow"};
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
}
