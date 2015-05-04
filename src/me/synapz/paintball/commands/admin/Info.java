package me.synapz.paintball.commands.admin;


import me.synapz.paintball.Message;
import me.synapz.paintball.Settings;
import me.synapz.paintball.commands.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Info extends Command{

    public void onCommand(Player player, String[] args) {
        ChatColor g = ChatColor.GRAY;
        String version = Settings.getSettings().getVersion();
        String website = Settings.getSettings().getWebsite();
        String author = Settings.getSettings().getAuthor();
        player.sendMessage(Message.getMessenger().getHelpTitle(false));
        Message.getMessenger().msg(player, ChatColor.DARK_AQUA, "Version: " + g + version, "Website: " + g + website, "Github: " + g + "https://github.com/Synapz1/Paintball", "Author: " + g + author);
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
