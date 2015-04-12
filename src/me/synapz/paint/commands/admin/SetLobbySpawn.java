package me.synapz.paint.commands.admin;


import me.synapz.paint.arenas.Arena;
import me.synapz.paint.arenas.ArenaManager;
import me.synapz.paint.commands.Command;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetLobbySpawn extends Command{

    public void onCommand(Player player, String[] args) {
        Location spawn = player.getLocation();
        String arenaName = args[2];

        Arena a = ArenaManager.getArenaManager().getArena(arenaName);

        if (a == null) {
            // no arena named that
        }

        a.setSpawn(spawn, ArenaManager.Team.RED);

    }

    public String getArgs() {
        String args = "<id>";
        return args;
    }

    public String getPermission() {
        return "paintball.admin.setlobby";
    }

    public String getName() {
        return "setlobby";
    }

    public String getInfo() {
        return "Set lobby of a Paintball Arena";
    }

    public CommandType getCommandType() {
        return CommandType.ADMIN;
    }

    public int getArgsInt() {
        return 2;
    }

}
