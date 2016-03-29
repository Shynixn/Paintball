package me.synapz.paintball.commands.admin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.synapz.paintball.Messenger;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Utils;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SetHolo extends PaintballCommand {

    @Override
    public void onCommand(Player player, String[] args) {

        if (!Settings.HOLOGRAPHIC_DISPLAYS) {
            Messenger.error(player, "Please download plugin HolographicDisplays to use this feature.", "http://dev.bukkit.org/bukkit-plugins/holographic-displays/");
        }

        Settings.ARENA.addLeaderboard(player.getLocation().add(0, 2, 0), args.length == 3 ? StatType.getStatType(player, args[2]) : null, true);
        Messenger.success(player, "Hologram leaderboard set to your location!");
    }

    @Override
    public String getName() {
        return "setholo";
    }

    @Override
    public String getInfo() {
        return "Create a leaderboard hologram.";
    }

    @Override
    public String getArgs() {
        return "[stat]";
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
        return 3;
    }

    @Override
    public int getMinArgs() {
        return 2;
    }
}
