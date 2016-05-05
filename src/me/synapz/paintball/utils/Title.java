package me.synapz.paintball.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Jeremy(Refrigerbater) on 5/4/2016.
 */
public class Title {
    private static Class<?> titlePacket;
    private static Class<?> actionPacket;
    private static Class<?> chatBasePacket;
    private static Class<?> chatSerializerPacket;
    private static Class<?> packet;

    /*
    0 = TITLE
    1 = SUBTITLE
    2 = TIMES
    3 = CLEAR
    4 = RESET
     */
    private static Object[] actionValues;

    private String title = "";
    private String subtitle = "";

    private int fadeIn = -1;
    private int stay = -1;
    private int fadeOut = -1;

    /**
     * Create a new default title.
     */
    public Title() {
        loadNmsClasses();
    }

    /**
     * Create a new title.
     * @param title The custom title message.
     */
    public Title(String title) {
        this.setTitle(title);
        loadNmsClasses();
    }

    /**
     * Create a new title.
     * @param title The custom title message.
     * @param subtitle The custom subtitle message.
     */
    public Title(String title, String subtitle) {
        this.setTitle(title);
        this.setSubtitle(subtitle);
        loadNmsClasses();
    }

    /**
     * Create a new title.
     * @param title The custom title message.
     * @param subtitle The custom subtitle message.
     * @param fadeIn The custom fade in time in ticks.
     * @param stay The custom stay time in ticks.
     * @param fadeOut The custom fade out time in ticks.
     */
    public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        this.setTitle(title);
        this.setSubtitle(subtitle);
        this.setFadeIn(fadeIn);
        this.setStay(stay);
        this.setFadeOut(fadeOut);
        loadNmsClasses();
    }

    /**
     * Load all nms classes into their respective objects.
     */
    private void loadNmsClasses() {
        try {
            if (titlePacket == null) titlePacket = ReflectionUtil.getNMSClass("PacketPlayOutTitle");
            if (actionPacket == null) actionPacket = ReflectionUtil.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            if (chatBasePacket == null) chatBasePacket = ReflectionUtil.getNMSClass("IChatBaseComponent");
            if (chatSerializerPacket == null) chatSerializerPacket = ReflectionUtil.getNMSClass("ChatComponentText");
            if (actionValues == null) actionValues = actionPacket.getEnumConstants();
            if (packet == null) packet = ReflectionUtil.getNMSClass("Packet");
        } catch (ClassNotFoundException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error loading nms classes for title util.");
        }
    }

    /**
     * Send a title and subtitle(if set) to a {@link Player} using custom timings(if set).
     * @param player The player.
     */
    public void send(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket, int.class, int.class, int.class).newInstance(actionValues[2], null, getFadeIn(), getStay(), getFadeOut());

            // send timings first if there are any
            if (getFadeIn() != -1 && getFadeOut() != -1 && getStay() != -1)
                sendPacket.invoke(connection, packet);

            // send title
            Object serializedText = chatSerializerPacket.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', getTitle()));
            packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[0], serializedText);
            sendPacket.invoke(connection, packet);

            // send subtitle if not empty
            if (!getSubtitle().equals("")) {
                serializedText = chatSerializerPacket.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', getSubtitle()));
                packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[1], serializedText);
                sendPacket.invoke(connection, packet);
            }

        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error sending title to player: " + player.getName());
            e.printStackTrace();
        }
    }

    /**
     * Update a title for a {@link Player}
     * @param player The player.
     */
    public void updateTitle(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket, int.class, int.class, int.class).newInstance(actionValues[2], null, getFadeIn(), getStay(), getFadeOut());

            Object serializedText = chatSerializerPacket.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', getTitle()));
            packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[0], serializedText);
            sendPacket.invoke(connection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error updating title for player: " + player.getName());
        }
    }

    /**
     * Update a subtitle for a {@link Player}
     * @param player The player.
     */
    public void updateSubtitle(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket, int.class, int.class, int.class).newInstance(actionValues[2], null, getFadeIn(), getStay(), getFadeOut());

            Object serializedText = chatSerializerPacket.getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', getSubtitle()));
            packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[0], serializedText);
            sendPacket.invoke(connection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error updating subtitle for player: " + player.getName());
        }
    }

    /**
     * Update a title's timings for a {@link Player}
     * @param player The player.
     */
    public void updateTimes(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket, int.class, int.class, int.class).newInstance(actionValues[2], null, getFadeIn(), getStay(), getFadeOut());

            if (getFadeIn() != -1 && getFadeOut() != -1 && getStay() != -1)
                sendPacket.invoke(connection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error updating title times for player: " + player.getName());
        }
    }

    /**
     * Clear a title for a {@link Player}
     * @param player The player.
     */
    public void clear(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[3], null);
            sendPacket.invoke(connection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error clearing title for player: " + player.getName());
        }
    }

    /**
     * Reset a title's settings for a {@link Player}
     * @param player The player.
     */
    public void reset(Player player) {
        try {
            Object connection = ReflectionUtil.getConnection(player);
            Method sendPacket = ReflectionUtil.getMethod(connection.getClass(), "sendPacket", packet);
            Object packet = titlePacket.getConstructor(actionPacket, chatBasePacket).newInstance(actionValues[4], null);
            sendPacket.invoke(connection, packet);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException  | InstantiationException e) {
            Messenger.error(Bukkit.getConsoleSender(), "Error resetting title for player: " + player.getName());
        }
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubtitle()
    {
        return subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public int getFadeIn()
    {
        return fadeIn;
    }

    public void setFadeIn(int fadeIn)
    {
        this.fadeIn = fadeIn;
    }

    public int getStay()
    {
        return stay;
    }

    public void setStay(int stay)
    {
        this.stay = stay;
    }

    public int getFadeOut()
    {
        return fadeOut;
    }

    public void setFadeOut(int fadeOut)
    {
        this.fadeOut = fadeOut;
    }
}
