package me.synapz.paintball.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

/**
 * Created by Jeremy(Refrigerbater) on 5/4/2016.
 */
public class ChatComponent
{

    private BaseComponent component;

    /**
     * Create a new {@link ChatComponent}
     * @param text The message to add.
     */
    public ChatComponent(String text) {
        component = new TextComponent(text);
    }

    /**
     * Add a new {@link ClickEvent} with an {@link ClickEvent.Action} with type SUGGEST_COMMAND.
     * @param text The text to insert.
     * @return Edited {@link ChatComponent}
     */
    public ChatComponent addClickInsert(String text) {
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, text));
        return this;
    }

    /**
     * Add a new {@link HoverEvent} with an {@link HoverEvent.Action} with type SHOW_TEXT
     * @param text The text to show.
     * @return Edited {@link ChatComponent}
     */
    public ChatComponent addHoverText(String text) {
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text))));
        return this;
    }

    /**
     * Append a {@link BaseComponent} after this {@link ChatComponent}
     * @param component The {@link BaseComponent} to add.
     * @return Edited {@link ChatComponent}
     */
    public ChatComponent appendChatComponent(ChatComponent component) {
        this.component.addExtra(component.getComponent());
        return this;
    }

    /**
     * Send {@link BaseComponent} to a {@link Player}
     * @param player The {@link Player} to send the {@link BaseComponent} to.
     */
    public void sendComponent(Player player) {
        player.spigot().sendMessage(component);
    }

    public BaseComponent getComponent() {
        return component;
    }
}
