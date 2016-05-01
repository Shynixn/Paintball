package me.synapz.paintball.enums;

import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public enum ArenaType {

    CTF("Capture the Flag", "Capture other team's flags and bring them to your base"),
    TDM("Team Deathmatch", "Kill players on the other team"),
    FFA("Free For All", "Everyone is on their own team"),
    DOM("Domination", "Secure other team's beacon points"),
    LTS("Last Team Standing", "Limited lives, last team standing wins"),
    RTF("Rush the Flag", "Capture the neutral flag and bring it to your base"),
    DTC("Destroy the Core", "Get to the other team's Core and shoot it to destroy it"),
    SFG("Safe Guard", "Bring your zombie to your base by standing close to it"),
    KC("Kill Confirmed", "After you kill a player, confirm the kill before the other team does");

    private String fullName;
    private String shortName;
    private String gameInfo;

    private ArenaType(String fullName, String gameInfo) {
        this.fullName = fullName;
        this.shortName = toString().toLowerCase();
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
            Messenger.error(sender, new MessageBuilder(Messages.INVALID_ARENA_TYPE).replace(Tag.ARENA_TYPES, getReadableList()).build());

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
