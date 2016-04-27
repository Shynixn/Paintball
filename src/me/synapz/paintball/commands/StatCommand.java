package me.synapz.paintball.commands;

import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import org.bukkit.entity.Player;

public abstract class StatCommand extends PaintballCommand {

    protected StatType type;
    private String rawStatType;

    protected Player player;
    protected String[] args;

    public void onCommand(Player player, String[] args) {
        this.player = player;
        this.args = args;

        try {
            this.rawStatType = args[getStatArg()];
        } catch (ArrayIndexOutOfBoundsException exc) {
            this.rawStatType = "all";
        }

        if (!rawStatType.equalsIgnoreCase("all")) {
            type = StatType.getStatType(player, rawStatType);
            if (type == null) {
                return;
            }
        } else {
            type = null;
        }

        onCommand();
    }

    public abstract void onCommand();

    public abstract String getName();

    public abstract Messages getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int getMaxArgs();

    public abstract int getMinArgs();

    protected abstract int getStatArg();

}
