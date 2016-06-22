package me.synapz.paintball.utils;

import me.synapz.paintball.enums.Messages;
import me.synapz.paintball.enums.Tag;

public class MessageBuilder {

    private final Messages message;
    private String builtMessage;

    public MessageBuilder(Messages message) {
        this.builtMessage = message.getString();
        this.message = message;
    }

    public MessageBuilder replace(Tag tag, String replacement) {
        if (replacement == null || tag == null) return this;
        builtMessage = builtMessage.replace(tag.toString(), replacement);
        return this;
    }

    public String build() {
        return builtMessage;
    }

    public Messages getMessage() {
        return message;
    }
}
