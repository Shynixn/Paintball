package me.synapz.paintball.players;

import me.synapz.paintball.arenas.Arena;
import me.synapz.paintball.arenas.CTFArena;
import me.synapz.paintball.enums.StatType;
import me.synapz.paintball.enums.Team;
import me.synapz.paintball.storage.Settings;
import me.synapz.paintball.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CTFArenaPlayer extends ArenaPlayer {

    private CTFArena ctfArena = (CTFArena) arena;
    private boolean isFlagHolder = false;
    private Team heldFlag;

    private int captured;
    private int dropped;

    public CTFArenaPlayer(Arena a, Team t, Player p) {
        super(a, t, p);
    }

    @Override
    public void kill(ArenaPlayer arenaPlayer, String action) {
        CTFArenaPlayer ctfArenaPlayer = (CTFArenaPlayer) arenaPlayer;

        arena.decrementTeamScore(team);
        super.kill(ctfArenaPlayer, action);

        if (ctfArenaPlayer.isFlagHolder())
            ctfArenaPlayer.dropFlag();
    }

    public void pickupFlag(Location loc, Team pickedUp) {

        if (pickedUp != null && pickedUp == team)
            return;

        isFlagHolder = true;
        heldFlag = pickedUp;

        loc.getBlock().setType(Material.AIR);

        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + player.getName() + " has stolen " + pickedUp.getTitleName() + "'s flag!");

        player.getInventory().setHelmet(Utils.makeBanner(team.getChatColor() + team.getTitleName() + " Flag", pickedUp.getDyeColor()));
        player.updateInventory();

        if (ctfArena.getDropedFlagLocations().containsKey(loc))
            ctfArena.remFlagLocation(loc);
    }

    public void dropFlag() {
        dropped++;

        ctfArena.addFlagLocation(getLastLocation(), heldFlag);
        Utils.createFlag(heldFlag, getLastLocation());

        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + player.getName() + " has dropped the flag!");

        removeFlag();
    }

    public void scoreFlag() {
        captured++;

        arena.incrementTeamScore(team);
        arena.updateAllScoreboard();

        arena.broadcastMessage(Settings.THEME + ChatColor.BOLD + "The " + team.getTitleName() + " has scored a flag!");

        Location toReset = ctfArena.getFlagLocation(heldFlag);
        Utils.createFlag(heldFlag, toReset);

        removeFlag();
    }

    public boolean isFlagHolder() {
        return isFlagHolder;
    }

    public Team getHeldFlag() {
        return heldFlag;
    }

    private void removeFlag() {
        if (arena.ARENA_WOOL_HELMET)
            player.getInventory().setHelmet(Utils.makeWool(team.getChatColor() + team.getTitleName() + " Team", team.getDyeColor()));
        else
            player.getInventory().setArmorContents(colorLeatherItems(new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_HELMET)));


        player.updateInventory();

        isFlagHolder = false;
        heldFlag = null;
    }

    @Override
    public void leave() {
        super.leave();

        Settings.PLAYERDATA.addToStat(StatType.FLAGS_CAPTURED, this, captured);
        Settings.PLAYERDATA.addToStat(StatType.FLAGS_DROPPED, this, dropped);
        Settings.PLAYERDATA.saveFile();
    }
}
