package me.synapz.paintball.storage;

import me.synapz.paintball.enums.Items;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;

public class ItemFile extends PaintballFile {

    public ItemFile(Plugin pb) {
        super(pb, "items.yml");
    }

    public String getName(Items item) {
        return loadString(item, "name", item.getDefaultName());
    }

    public Material getMaterial(Items item) {
        return Material.valueOf(loadString(item, "material", item.getDefaultMaterial().toString()));
    }

    public int getAmount(Items item) {
        return loadInt(item, "amount", item.getDefaultAmount());
    }

    public boolean isShown(Items item) {
        return loadBoolean(item, "shown", item.getDefaultShown());
    }

    public String getDescription(Items item) {
        return loadString(item, "description", item.getDefaultDescription());
    }

    public double getMoney(Items item) {
        return loadDouble(item, "money", item.getDefaultMoney());
    }

    public int getCoins(Items item) {
        return loadInt(item, "coins", item.getDefaultCoins());
    }

    public int getTime(Items item) {
        return loadInt(item, "time", item.getDefaultTime());
    }

    public String getPermission(Items item) {
        return loadString(item, "permission", item.getDefaultPermission());
    }

    public Sound getSound(Items item) {
        return Sound.valueOf(loadString(item, "sound", item.getDefaultSound().toString()));
    }

    private String loadString(Items item, String section, String value) {
        if (fileConfig.get(item.toString()) == null || fileConfig.get(getPath(item, section)) == null) {
            fileConfig.set(item.toString() + "." + section, value);
            this.saveFile();
        }

        return fileConfig.getString(getPath(item, section));
    }

    private boolean loadBoolean(Items item, String section, boolean value) {
        if (fileConfig.get(item.toString()) == null || fileConfig.get(getPath(item, section)) == null) {
            fileConfig.set(item.toString() + "." + section, value);
            this.saveFile();
        }

        return fileConfig.getBoolean(getPath(item, section));
    }

    private int loadInt(Items item, String section, int value) {
        if (fileConfig.get(item.toString()) == null || fileConfig.get(getPath(item, section)) == null) {
            fileConfig.set(item.toString() + "." + section, value);
            this.saveFile();
        }

        return fileConfig.getInt(getPath(item, section));
    }

    private double loadDouble(Items item, String section, double value) {
        if (fileConfig.get(item.toString()) == null || fileConfig.get(getPath(item, section)) == null) {
            fileConfig.set(item.toString() + "." + section, value);
            this.saveFile();
        }

        return fileConfig.getDouble(getPath(item, section));
    }

    private String getPath(Items item, String section) {
        return item.toString() + "." + section;
    }
}
