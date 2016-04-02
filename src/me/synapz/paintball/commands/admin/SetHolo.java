package me.synapz.paintball.commands.admin;

import me.synapz.paintball.Messenger;
import me.synapz.paintball.commands.StatCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.storage.Settings;
import net.md_5.bungee.api.ChatColor;

public class SetHolo extends StatCommand {

    @Override
    public void onCommand() {
        int page = 1;

        if (!Settings.HOLOGRAPHIC_DISPLAYS) {
            Messenger.error(player, "Please download plugin HolographicDisplays to use this feature.", "http://dev.bukkit.org/bukkit-plugins/holographic-displays/");
            return;
        }

        if (args.length == 4) {
            try {
                page = Integer.parseInt(args[3]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, "Please enter a valid number for the page.");
                return;
            }
        }

        if (page <= 0) {
            Messenger.error(player, "The page cannot be lower than 0");
            return;
        } else if (page > Settings.PLAYERDATA.getMaxPage()) {
            Messenger.error(player, "Page " + ChatColor.GRAY + page + ChatColor.RED + "/" + ChatColor.GRAY + Settings.PLAYERDATA.getMaxPage() + ChatColor.RED + " cannot be found.");
            return;
        }

        Settings.ARENA.addLeaderboard(player.getLocation().add(0, 2, 0), type, page, true);
        Messenger.success(player, "Hologram leaderboard set to your location!");
    }

    @Override
    public String getName() {
        return "setholo";
    }

    @Override
    public String getInfo() {
        return "Creates a leaderboard hologram.";
    }

    @Override
    public String getArgs() {
        return "<stat/all> [page]";
    }

    @Override
    public String getPermission() {
        return "paintball.admin.setholo";
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    @Override
    public int getMaxArgs() {
        return 4;
    }

    @Override
    public int getMinArgs() {
        return 3;
    }

    @Override
    protected int getStatArg() {
        return 2;
    }
}