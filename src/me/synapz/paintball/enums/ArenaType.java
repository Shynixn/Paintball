package me.synapz.paintball.enums;

import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.entity.Player;

public enum ArenaType {

    CTF(Messages.CTF_LONG_NAME, Messages.CTF_DESCRIPTION, Messages.CTF_SHORT_NAME),
    TDM(Messages.TDM_LONG_NAME, Messages.TDM_DESCRIPTION, Messages.TDM_SHORT_NAME),
    FFA(Messages.FFA_LONG_NAME, Messages.FFA_DESCRIPTION, Messages.FFA_SHORT_NAME),
    DOM(Messages.DOM_LONG_NAME, Messages.DOM_DESCRIPTION, Messages.DOM_SHORT_NAME),
    LTS(Messages.LTS_LONG_NAME, Messages.LTS_DESCRIPTION, Messages.LTS_SHORT_NAME),
    RTF(Messages.RTF_LONG_NAME, Messages.RTF_DESCRIPTION, Messages.RTF_SHORT_NAME),
    DTC(Messages.DTC_LONG_NAME, Messages.DTC_DESCRIPTION, Messages.DTC_SHORT_NAME),
    KC(Messages.KC_LONG_NAME, Messages.KC_DESCRIPTION, Messages.KC_SHORT_NAME);

    private Messages fullName;
    private Messages shortName;
    private Messages gameInfo;

    ArenaType(Messages fullName, Messages gameInfo, Messages shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
        this.gameInfo = gameInfo;
    }

    public String getFullName() {
        return fullName.getString();
    }

    public String getShortName() {
        return shortName.getString();
    }

    public String getStaticName() {
        return this.toString();
    }

    public String getGameInfo() {
        return gameInfo.getString();
    }

    public static ArenaType getArenaType(Player sender, String strType) {
        ArenaType type = null;

        for (ArenaType t : ArenaType.values()) {
            if (t.getFullName().equalsIgnoreCase(strType) || t.getShortName().equalsIgnoreCase(strType) || t.getStaticName().equalsIgnoreCase(strType))
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
