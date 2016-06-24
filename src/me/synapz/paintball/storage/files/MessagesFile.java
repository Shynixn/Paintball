package me.synapz.paintball.storage.files;

import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;
import me.synapz.paintball.storage.Settings;
import org.bukkit.plugin.Plugin;

public class MessagesFile extends PaintballFile {

    public MessagesFile(Plugin pb) {
        super(pb, "messages.yml");

        // Loads messages.yml and sets the message string for easy getting
        for (Messages msg : Messages.values())
            msg.setString(loadString(msg));
    }

    public String loadString(Messages message) {
        String path = message.toString(); // Turns MESSAGE_HERE into message-here

        if (fileConfig.get(path) == null) {
            fileConfig.set(path, message.getDefaultString());
            this.saveFile();
        }

        return fileConfig.getString(path)
                .replace(Tag.THEME.toString(), Settings.THEME)
                .replace(Tag.SECONDARY.toString(), Settings.SECONDARY)
                .replace(Tag.PREFIX.toString(), Messages.PREFIX.getString());
    }
}
