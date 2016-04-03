package me.synapz.paintball.commands.admin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.locations.HologramLocation;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DelHolo extends PaintballCommand {

    @Override
    public void onCommand(Player player, String[] args) {
        int radius = 5;
        int removed = 0;

        if (!Settings.HOLOGRAPHIC_DISPLAYS) {
            Messenger.error(player, "Please download plugin HolographicDisplays to use this feature.", "http://dev.bukkit.org/bukkit-plugins/holographic-displays/");
            return;
        }

        if (args.length == 3){
            try {
                radius = Integer.parseInt(args[2]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, "Please enter a valid number for radius");
                return;
            }
        }

        if (Settings.ARENA.getHologramList().isEmpty()) {
            Messenger.success(player, "No holograms were removed.");
            return;
        }

        for (String loc : Settings.ARENA.getHologramList()) {
            HologramLocation holoLoc = new HologramLocation(loc);
            Location locToGetDistance = holoLoc.getLocation();

            if (locToGetDistance.distance(player.getLocation()) <= radius) {
                holoLoc.removeLocation();
            } else {
                continue;
            }

            for (Hologram hologram : HologramsAPI.getHolograms(JavaPlugin.getProvidingPlugin(Paintball.class))) {
                Location hLoc = hologram.getLocation();

                int hx = hLoc.getBlockX();
                int hy = hLoc.getBlockY();
                int hz = hLoc.getBlockZ();

                int x = locToGetDistance.getBlockX();
                int y = locToGetDistance.getBlockY();
                int z = locToGetDistance.getBlockZ();

                if (hx == x && hy == y && hz == z) {
                    hologram.delete();
                    removed++;
                }
            }
        }

        if (removed == 0) {
            Messenger.success(player, "No holograms were removed.");
            return;
        }

        Messenger.success(player, "Removed " + ChatColor.GRAY + removed + ChatColor.GREEN + " holograms.");
    }

    // pb admin deloholo 1
    @Override
    public String getName() {
        return "delholo";
    }

    @Override
    public String getInfo() {
        return "Remove holograms around you.";
    }

    @Override
    public String getArgs() {
        return "[radius]";
    }

    @Override
    public String getPermission() {
        return "paintball.admin.delholo";
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
