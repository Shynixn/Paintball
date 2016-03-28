package me.synapz.paintball.storage;

import org.bukkit.plugin.Plugin;

public class ItemsFile extends PaintballFile {

    public ItemsFile(Plugin pb) {
        super(pb, "items.yml");
    }
}
