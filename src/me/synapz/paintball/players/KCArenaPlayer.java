package me.synapz.paintball.players;

import me.synapz.paintball.arenas.KCArena;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Item;

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

    public void score(int amount) {
        while (amount > 0) {
            arena.incrementTeamScore(team, true);
            amount--;
        }

        player.getWorld().playSound(player.getLocation(), ((KCArena) arena).killConfirmedSound, 5, 5);
        arena.updateAllScoreboard();
    }

    public void deny() {
        player.getWorld().playSound(player.getLocation(), ((KCArena) arena).killDeniedSound, 5, 5);
    }
}