package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.Command;
import org.bukkit.entity.Player;

import static me.synapz.paintball.storage.Settings.*;
import static org.bukkit.ChatColor.*;


public class Info extends Command{

    public void onCommand(Player player, String[] args) {
        player.sendMessage(Message.getMessenger().getHelpTitle(CommandType.PLAYER));
        Message.getMessenger().msg(player, false, DARK_AQUA, "Version: " + GRAY + VERSION, "Website: " + GRAY + WEBSITE, "Author: " + GRAY + AUTHOR);

    }

    public String getArgs() {
        String args = "";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.info";
    }

    public String getName() {
        return "info";
    }

    public String getInfo() {
        return "Get plugin info";
    }

    public Command.CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 2;
    }
}
