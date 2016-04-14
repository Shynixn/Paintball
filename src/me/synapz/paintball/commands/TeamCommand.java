package me.synapz.paintball.commands;

import me.synapz.paintball.arenas.RTFArena;
import me.synapz.paintball.utils.Messenger;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.commands.arena.DelLocation;
import me.synapz.paintball.commands.arena.SetLocation;
import me.synapz.paintball.enums.CommandType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class TeamCommand extends ArenaCommand {

    protected Team team;
    private String rawTeamName;

    public void onCommand(Player player, String[] args) {
        super.onCommand(player, args);

        if (arena == null)
            return;

        // This means it is an optional argument since it wasn't specified
        try {
            this.rawTeamName = args[getTeamArg()];
        } catch (ArrayIndexOutOfBoundsException exc) {
            if (arena != null)
                onCommand();
            return;
        }

        if (arena.getArenaTeamList().isEmpty()) {
            Messenger.error(player, arena.toString(ChatColor.RED) + " does not have any teams set!");
            return;
        }

        if ((this instanceof SetLocation || this instanceof DelLocation) && rawTeamName.equalsIgnoreCase("all") || arena instanceof RTFArena && rawTeamName.equalsIgnoreCase("neutral")) {
            onCommand();
            return;
        }

        this.player = player;
        this.args = args;
        this.team = stringToTeam();

        if (teamCheck()) {
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

    protected abstract int getTeamArg();

    // Checks to see if a team is invalid, if it is it sends the player the list of valid teams
    private boolean teamCheck() {
        String teamString = args[getTeamArg()];
        StringBuilder validTeams = new StringBuilder(" ");
        String finalValidTeams = "";
        if (arena.getArenaTeamList().isEmpty()) {
            return false;
        }

        for (Team team : arena.getArenaTeamList()) {
            validTeams.append(ChatColor.stripColor(team.getTitleName().toLowerCase().replace(" ", ""))).append(" ");
        }

        if (!(validTeams.toString().contains(" " + teamString.toLowerCase() + " "))) {
            // remove last space and replace spaces with /. So it should be <red/blue/green>
            finalValidTeams = validTeams.substring(1, validTeams.lastIndexOf(" "));
            Messenger.error(player, teamString + " is an invalid team. Choose either <" + finalValidTeams.toString().replace(" ", "/") + ">");
            return false;
        } else {
            return true;
        }
    }

    // Turns a string like 'red' in to a team
    private Team stringToTeam() {
        for (Team t : arena.getArenaTeamList()) {
            if (t.getTitleName().replace(" ", "").equalsIgnoreCase(args[getTeamArg()])) {
                return t;
            }
        }
        return null;
    }
}
