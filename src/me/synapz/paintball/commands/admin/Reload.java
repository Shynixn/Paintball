package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Reload extends PaintballCommand {

    public void onCommand(Player player, String[] args) {
        Settings.getSettings().reloadConfig();
        Message.getMessenger().msg(player, false, ChatColor.GREEN, "Successfully reloaded configuration files.");
    }

    public String getName() {
        return "reload";
    }

    public String getInfo() {
        return "Reload config.yml";
    }

    public String getArgs() {
        return "";
    }

    public String getPermission() {
        return "paintball.admin.reload";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getMaxArgs() {
        return 2;
    }

    public int getMinArgs() {
        return 2;
    }

}