package me.synapz.paintball.arenas;

import me.synapz.paintball.enums.ArenaType;
import me.synapz.paintball.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Item;

import java.util.ArrayList;
import java.util.List;

import static me.synapz.paintball.storage.Settings.ARENA;

public class KCArena extends Arena {

    private List<Item> dogTags = new ArrayList<>();
    public Sound killDeniedSound;
    public Sound killConfirmedSound;

    public KCArena(String name, String currentName, boolean addToConfig) {
        super(name, currentName, addToConfig);
    }

    @Override
    public ArenaType getArenaType() {
        return ArenaType.KC;
    }

    @Override
    public void forceLeaveArena() {
        super.forceLeaveArena();

        // Removes all dog tags found on the floor
        for (Item toRemove : dogTags)
            toRemove.remove();
    }

    public void addDogTag(Item toAdd) {
        dogTags.add(toAdd);
    }

    public void removeDogTag(Item toRemove) {
        dogTags.remove(toRemove);
    }

    @Override
    public void loadValues() {
        super.loadValues();

        killConfirmedSound = Utils.loadSound(ARENA.loadString("KC.kill-confirmed", this));
        killDeniedSound = Utils.loadSound(ARENA.loadString("KC.kill-denied", this));
    }
}
