package me.synapz.paintball.storage.files;

import org.bukkit.plugin.Plugin;

public class LogsFile extends PaintballFile {

    public LogsFile(Plugin pb) {
        super(pb, "logs.yml");
    }
}
