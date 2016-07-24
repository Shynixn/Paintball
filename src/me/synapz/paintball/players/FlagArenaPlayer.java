package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.MessageBuilder;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class FlagArenaPlayer extends ArenaPlayer {

    private int captured;
    private int dropped;
    private boolean isFlagHolder;

    public FlagArenaPlayer(LobbyPlayer lobbyPlayer) {
        super(lobbyPlayer);
    }

    public void dropFlag() {
        dropped++;

        arena.broadcastMessage(Settings.THEME + new MessageBuilder(Messages.ARENA_FLAG_DROP).replace(Tag.TEAM, team.getTitleName()).build());

        removeFlag();
    }

    public void scoreFlag() {
        if (arena.getState() != Arena.ArenaState.IN_PROGRESS)
            return;

        captured++;

        arena.broadcastMessage(Settings.THEME + new MessageBuilder(Messages.ARENA_FLAG_SCORE).replace(Tag.TEAM, team.getTitleName()).build());

        arena.incrementTeamScore(team, true);
        arena.updateAllScoreboard();

        removeFlag();
    }


    public void pickupFlag(Location loc, Team pickedUp) {
        if (arena.getState() != Arena.ArenaState.IN_PROGRESS || pickedUp != null && pickedUp == team)
            return;

        isFlagHolder = true;

        loc.getBlock().setType(Material.AIR);

        if (pickedUp == null)
            return;

        arena.broadcastMessage(Settings.THEME + new MessageBuilder(Messages.ARENA_FLAG_STEAL).replace(Tag.SENDER, player.getName()).replace(Tag.TEAM, pickedUp.getTitleName()).build());

        player.getInventory().setHelmet(Utils.makeBanner(pickedUp.getChatColor() + pickedUp.getTitleName() + "'s Flag", pickedUp.getDyeColor()));
        player.updateInventory();
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        arena.decrementTeamScore(team);
        super.kill(arenaPlayer, action);

        if (((FlagArenaPlayer) arenaPlayer).isFlagHolder())
            ((FlagArenaPlayer) arenaPlayer).dropFlag();
    }

    public boolean isFlagHolder() {
        return isFlagHolder;
    }

    @Override
    public void leave() {
        super.leave();

        uuidStatsFile.addToStat(StatType.FLAGS_CAPTURED, captured);
        uuidStatsFile.addToStat(StatType.FLAGS_DROPPED, dropped);
    }

    protected void removeFlag() {
        if (arena.ARENA_WOOL_HELMET)
            player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + " Team", team.getDyeColor()));
        else
            player.getInventory().setArmorContents(Utils.colorLeatherItems(team, new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));

        isFlagHolder = false;
        player.updateInventory();
    }
}
