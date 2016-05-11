package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.KCArena;
import me.synapz.paintball.countdowns.ProtectionCountdown;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.locations.TeamLocation;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class KCArenaPlayer extends ArenaPlayer {

    public KCArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        arena.decrementTeamScore(team);
        super.kill(arenaPlayer, action);
    }

    @Override
    public void setHealth(Team fromTeam, int newHealth) {
        super.setHealth(fromTeam, newHealth);

        // this means they died
        if (getHealth() == arena.HITS_TO_KILL) {
            // get their last location and spawn a colored wool on it (the "Dog Tag", to confirm the kill)
            Item toAdd = getLastLocation().getWorld().dropItemNaturally(getLastLocation(), Utils.makeWool(ChatColor.RESET + "" + team.getChatColor() + "Dog Tag", team.getDyeColor()));
            ((KCArena) arena).addDogTag(toAdd);
        }
    }

    public void score() {
        arena.incrementTeamScore(team, true);
        arena.updateAllScoreboard();
    }
}