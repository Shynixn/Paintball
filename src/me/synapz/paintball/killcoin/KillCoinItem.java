package me.synapz.paintball.killcoin;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import me.synapz.paintball.ExpirationTime;
import me.synapz.paintball.Paintball;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class KillCoinItem extends ItemStack {

    // Display name
    // Description
    // Cost
    // KillCoins
    // Expiration Time
    // Permission

    private final String name;
    private final String description;
    private final double money;
    private final int killcoins;
    private final int expirationTime;
    private final String permission;
    private final boolean showItem;
    private final boolean configItem;
    private final String type;

    public KillCoinItem(@NotNull Material material, @NotNull String name, @Nullable String description, @NotNull double money, @NotNull int killcoins, @Nullable int expirationTime, @Nullable String permission, @NotNull int amount, @NotNull boolean showItem) {
        super(material, amount);
        this.name = name;
        this.description = description;
        this.money = money;
        this.killcoins = killcoins;
        this.expirationTime = expirationTime;
        this.permission = permission;
        this.showItem = showItem;
        this.configItem = false;
        this.type = "";

        KillCoinItemHandler.getHandler().addItem(this);
    }

    // Creates a KillCoinItem from config.yml based on a rawItem
    public KillCoinItem(String path, FileConfiguration file) {
        super(Material.valueOf(file.getString(path + ".material")), file.getInt(path + ".amount"));
        this.name = file.getString(path + ".name");
        this.description = file.getString(path + ".description");
        this.money = file.getDouble(path + ".money");
        this.killcoins = file.getInt(path + ".killcoins");
        this.expirationTime = file.getInt(path + ".expiration-time");
        this.permission = file.getString(path + ".permission-required");
        this.showItem = file.getBoolean(path + ".shown");
        this.configItem = true;
        this.type = file.getString(path + ".type");

        KillCoinItemHandler.getHandler().addItem(this);
    }

    private KillCoinItem(KillCoinItem item) {
        super(item);
        this.name = item.getItemName();
        this.description = item.getDescription();
        this.money = item.getMoney();
        this.killcoins = item.getKillCoins();
        this.expirationTime = item.getExpirationTime();
        this.permission = item.getPermission();
        this.showItem = item.showItem();
        this.configItem = item.hasType();
        this.type = item.getItemType();
    }

    public String getItemName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getMoney() {
        return money;
    }

    public int getKillCoins() {
        return killcoins;
    }

    public int getExpirationTime() {
        return expirationTime;
    }

    public String getPermission() {
        return permission;
    }

    public KillCoinItem getItemStack(ArenaPlayer arenaPlayer) {
        ItemMeta meta = this.getItemMeta();
        List<String> newLore = new ArrayList<String>();

        if (!this.showItem()) {
            return null;
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getItemName()));

        if (hasDescription()) {
            String[] description = getDescription().split("\n");
            for (int i = 0; i < description.length; i++) {
                newLore.add((i == 0 ? Settings.THEME + "Description: " : "") + Settings.SECONDARY + ChatColor.translateAlternateColorCodes('&', description[i]));
            }
        }

        if (hasExpirationTime()) {
            newLore.add(Settings.THEME + "Lasts: " + Settings.SECONDARY + (getExpirationTime() > 60 ? getExpirationTime()/60 : getExpirationTime()) + Settings.THEME + (getExpirationTime() > 60 ? " minutes" : " seconds"));
        }

        if (requiresMoney())
            newLore.add(Settings.THEME + "Cost: " + Settings.SECONDARY + getMoney()); // todo: implement currency so $amount

        if (requiresKillCoins())
            newLore.add(Settings.THEME + "KillCoins: " + Settings.SECONDARY + getKillCoins());

        String error = getError(arenaPlayer);
        if (this.hasError(arenaPlayer))
            newLore.add(ChatColor.RED + "" + ChatColor.ITALIC + error);

        meta.setLore(newLore);
        this.setItemMeta(meta);
        return this;
    }

    public String getError(ArenaPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (this.hasPermission() && !player.getPlayer().hasPermission(permission)) {
            builder.append("You don't have permission to use this item!");
        } else {
            if (player.getKillCoins() < this.getKillCoins())
            builder.append("You don't have enough KillCoins ");

            // if (arenaPlayer.getMoney() < getMoney() // TODO: import Vault for this
            // builder.append(", Money");
        }

        if (!builder.toString().isEmpty()) {
            return builder.toString();
        } else {
            return null;
        }
    }

    public void giveItemToPlayer(ArenaPlayer playerToGetItem) {
        KillCoinItem itemToGive = new KillCoinItem(this);
        if (this.hasExpirationTime()) {
            new ExpirationTime(itemToGive, playerToGetItem, getExpirationTime()).runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 10);
        }
        playerToGetItem.getPlayer().getInventory().addItem(itemToGive);
    }

    public String getItemType() {
        return type;
    }

    public void onClickItem(PlayerInteractEvent event) {}

    public boolean hasError(ArenaPlayer player) {
        return !(getError(player) == null);
    }

    public boolean hasType() {
        return configItem;
    }

    public boolean showItem() {
        return showItem;
    }

    public boolean hasDescription() {
        return !(description.isEmpty());
    }

    public boolean hasExpirationTime() {
        return !(expirationTime <= 0);
    }

    public boolean requiresMoney() {
        return !(money <= 0);
    }

    public boolean requiresKillCoins() {
        return !(killcoins <= 0);
    }

    public boolean hasPermission() {
        return !(permission.equals("none"));
    }
}