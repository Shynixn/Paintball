package me.synapz.paintball.enums;

import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public enum ArenaType {

    CTF("Capture the Flag", "ctf", "Capture other team's flags and bring them to your base"),
    TDM("Team Deathmatch", "tdm", "Kill players on the other team"),
    FFA("Free For All", "ffa", "Everyone is on their own team"),
    DOM("Domination", "dom", "Secure other team's beacon points"),
    LTS("Last Team Standing", "lts", "Limited lives, last team standing wins"),
    RTF("Rush the Flag", "rtf", "Capture the neutral flag and bring it to your base"),
    DTC("Destroy the Core", "dtc", "Get to the other team's Core and shoot it to destroy it");

    private String fullName;
    private String shortName;
    private String gameInfo;

    private ArenaType(String fullName, String shortName, String gameInfo) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.gameInfo = gameInfo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getGameInfo() {
        return gameInfo;
    }

    public static ArenaType getArenaType(Player sender, String strType) {
        ArenaType type = null;

        // Had to convert ELM to FFA so this will turn the type to FFA if it loads as ELM
        if (strType.equalsIgnoreCase("elm"))
            return FFA;

        for (ArenaType t : ArenaType.values()) {
            if (t.getFullName().equalsIgnoreCase(strType) || t.getShortName().equalsIgnoreCase(strType))
                type = t;
        }

        if (type == null && sender != null)
            Messenger.error(sender, "Invalid arena type. Choose either <" + getReadableList() + ">");

        return type;
    }

    public static String getReadableList() {
        StringBuilder builder = new StringBuilder();

        for (ArenaType type : ArenaType.values()) {
            builder.append(type.getShortName() + "/");
        }

        return builder.substring(0, builder.lastIndexOf("/"));
    }

}
