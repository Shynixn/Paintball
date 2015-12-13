package me.synapz.paintball.players;

import me.synapz.paintball.Arena;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.Team;
import me.synapz.paintball.Utils;
import me.synapz.paintball.storage.Settings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.synapz.paintball.storage.Settings.PREFIX;
import static me.synapz.paintball.storage.Settings.getSettings;

public abstract class PaintballPlayer {

    protected Arena arena;
    protected Player player;
    protected Team team;

    public PaintballPlayer(Arena a, Team t, Player p) {
        this.arena = a;
        this.team = t;
        this.player = p;

        getSettings().getCache().savePlayerInformation(player);
        arena.addPlayer(this);

        initPlayer();
    }

    public Arena getArena() {
        return arena;
    }

    public Player getPlayer() {
        return player;
    }

    public Team getTeam() {
        return team;
    }

    protected void giveWoolHelmet() {
        player.getInventory().setHelmet(Utils.makeWool(team.getTitleName(), team.getDyeColor()));
    }

    public void chat(String message) {
        String chat = getChatLayout();

        chat = chat.replace("%TEAMNAME%", team.getTitleName());
        chat = chat.replace("%TEAMCOLOR%", team.getChatColor() + "");
        chat = chat.replace("%MSG%", message);
        chat = chat.replace("%PREFIX%", PREFIX);
        chat = chat.replace("%PLAYER%", player.getName());
        for (PaintballPlayer pbPlayer : arena.getAllPlayers().values()) {
            pbPlayer.getPlayer().sendMessage(chat);
        }
    }

    protected abstract void initPlayer();

    public void leaveArena() {
        if (Team.getPluginScoreboard().getTeam(team.getTitleName()) != null)
            Team.getPluginScoreboard().getTeam(team.getTitleName()).removePlayer(player);
        arena.removePlayer(this);
        Settings.getSettings().getCache().restorePlayerInformation(player.getUniqueId());

        // check for seeing if to stop the arena and have a player win
        if (arena.getAllArenaPlayers().size() == 1) {
            ArenaPlayer pbPlayer = (ArenaPlayer) arena.getAllArenaPlayers().toArray()[0];
            Team.getPluginScoreboard().getTeam(team.getTitleName()).removePlayer(player);
            Settings.getSettings().getCache().restorePlayerInformation(pbPlayer.getPlayer().getUniqueId());
            arena.win(team);
            arena.removePlayer(this);
            arena.setState(Arena.ArenaState.WAITING);
        }
    }

    protected abstract String getChatLayout();
}
