package me.synapz.paintball.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Created by Jeremy Lugo on 7/24/2016.
 */
public class FireworkUtil
{
    public static void spawnFirework(FireworkEffect effect, Location location)
    {
        Firework firework = (Firework) location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
        try
        {
            Class<?> entityFireworks = getClass("net.minecraft.server.", "EntityFireworks");
            Class<?> craftFirework = getClass("org.bukkit.craftbukkit.", "entity.CraftFirework");
            Object fireworkObject = craftFirework.cast(firework);
            Method handle = fireworkObject.getClass().getMethod("getHandle");
            Object entityFirework = handle.invoke(fireworkObject);
            Field expectedLifespan = entityFireworks.getDeclaredField("expectedLifespan");
            Field ticksFlown = entityFireworks.getDeclaredField("ticksFlown");
            ticksFlown.setAccessible(true);
            ticksFlown.setInt(entityFirework, expectedLifespan.getInt(entityFirework) - 1);
            ticksFlown.setAccessible(false);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    private static Class<?> getClass(String prefix, String nmsClassString) throws ClassNotFoundException
    {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
        String name = prefix + version + nmsClassString;
        Class<?> nmsClass = Class.forName(name);
        return nmsClass;
    }
}
