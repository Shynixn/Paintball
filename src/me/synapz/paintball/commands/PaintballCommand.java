package me.synapz.paintball.commands;

import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import org.bukkit.entity.Player;

public abstract class PaintballCommand {

    public boolean handleConditionsInSuperClass = true;

    public abstract void onCommand(Player player, String[] args);

    public abstract String getName();

    public abstract Messages getInfo();

    public abstract String getArgs();

    public abstract String getPermission();

    public abstract CommandType getCommandType();

    public abstract int getMaxArgs();

    public abstract int getMinArgs();

    // Get's the correct usage of the command
    public String getCorrectUsage() {
        String type = this.getCommandType() == CommandType.ADMIN ? "admin " : this.getCommandType() == CommandType.ARENA ? "arena " : "";
        String name = this.getName().equals("admin") || this.getName().equals("arena") ? "" : this.getName();
        return "Usage: /paintball " + type + name + " " + this.getArgs();
    }
}
