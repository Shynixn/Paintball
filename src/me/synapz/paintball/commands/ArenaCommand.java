package me.synapz.paintball.commands;

import me.synapz.paintball.Arena;
import me.synapz.paintball.ArenaManager;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.entity.Player;

public abstract class ArenaCommand extends PaintballCommand {

    protected Player player;
    protected String[] args;
    protected Arena arena;
    private String rawArenaName;

    public void onCommand(Player player, String[] args) {
        this.player = player;
        this.args = args;

        // This means specifying an arena is optional (/pb join)
        try {
            this.rawArenaName = args[getArenaArg()];
        } catch (ArrayIndexOutOfBoundsException exc) {
            onCommand();
            return;
        }

        this.arena = ArenaManager.getArenaManager().getArena(rawArenaName);

        if (arena == null && handleConditionsInSuperClass) {
            Messenger.error(player, rawArenaName + " is an invalid arena.");
            return;
        }

        if (!(this instanceof TeamCommand)) { // make sure this doesn't call onCommand if it is a team command, because it still has to check for team
            onCommand();
        } else {
            return;
        }
    }

    public abstract void onCommand();

    public abstract String getName();

    public abstract String getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int getMaxArgs();

    public abstract int getMinArgs();

    protected abstract int getArenaArg();
}
