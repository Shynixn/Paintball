package me.synapz.paintball.commands.admin;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.commands.PaintballCommand;
import me.synapz.paintball.enums.CommandType;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.locations.HologramLocation;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Messenger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DelHolo extends PaintballCommand {

    @Override
    public void onCommand(Player player, String[] args) {
        int radius = 5;
        int removed = 0;

        if (!Settings.HOLOGRAPHIC_DISPLAYS) {
            Messenger.error(player, Messages.DOWNLOAD_HOLO, Messages.HOLO_LINK);
            return;
        }

        if (args.length == 3){
            try {
                radius = Integer.parseInt(args[2]);
            } catch (NumberFormatException exc) {
                Messenger.error(player, Messages.VALID_NUMBER);
                return;
            }
        }

        if (Settings.ARENA.getHologramList().isEmpty()) {
            Messenger.success(player, Messages.NO_HOLOGRAMS_REMOVED);
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

            for (Hologram hologram : HologramsAPI.getHolograms(Paintball.getInstance())) {
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
            Messenger.success(player, Messages.NO_HOLOGRAMS_REMOVED);
            return;
        }

        Messenger.success(player, new MessageBuilder(Messages.HOLOGRAMS_REMOVED).replace(Tag.AMOUNT, removed + "").build());
    }

    @Override
    public String getName() {
        return "delholo";
    }

    @Override
    public Messages getInfo() {
        return Messages.COMMAND_HOLO_INFO;
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