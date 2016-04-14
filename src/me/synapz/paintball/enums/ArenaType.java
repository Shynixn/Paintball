package me.synapz.paintball.enums;

import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public enum ArenaType {

    CTF("Capture the Flag", "ctf"),
    TDM("Team Deathmatch", "tdm"),
    FFA("Free For All", "ffa"),
    DOM("Domination", "dom"),
    LTS("Last Team Standing", "lts"),
    RTF("Rush the Flag", "rtf");

    private String fullName;
    private String shortName;

    private ArenaType(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
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
