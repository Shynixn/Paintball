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
import org.bukkit.inventory.PlayerInventory;
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

    private String nameWithSpaces;
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
        this.nameWithSpaces = name;
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
        this.nameWithSpaces = name;
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
        this.name = item.getItemName(false);
        this.nameWithSpaces = name;
        this.description = item.getDescription();
        this.money = item.getMoney();
        this.killcoins = item.getKillCoins();
        this.expirationTime = item.getExpirationTime();
        this.permission = item.getPermission();
        this.showItem = item.showItem();
        this.configItem = item.hasType();
        this.type = item.getItemType();
    }

    // Gets the item name
    public String getItemName(boolean withSpaces) {
        return withSpaces ? nameWithSpaces : name;
    }

    // Gets the description of the item
    public String getDescription() {
        return description;
    }

    // Gets the money required to buy the item
    public double getMoney() {
        return money;
    }

    // Gets the item's amount of required KIllCoins
    public int getKillCoins() {
        return killcoins;
    }

    // Gets the item's expiration time
    public int getExpirationTime() {
        return expirationTime;
    }

    // Gets the items permission
    public String getPermission() {
        return permission;
    }

    // Sets the values specific to the arena player, then returns the item to be placed in KillCoinShop
    // forKillCoinShop sees if it is a KillCoin Shop, if it is, do not add spaces to the name
    public KillCoinItem getItemStack(ArenaPlayer arenaPlayer, boolean forKillCoinShop) {
        ItemMeta meta = this.getItemMeta();
        List<String> newLore = new ArrayList<String>();

        if (!this.showItem()) {
            return null;
        }

        if (!forKillCoinShop) { // if this isnt for the shop but for being placed in their inventory, add spaces to make the item different than others with same name
            setItemDisplayName(arenaPlayer.getPlayer().getInventory());
            setItemDisplayName(arenaPlayer.getPlayer().getInventory());
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getItemName(!forKillCoinShop)));

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

        if (hasError(arenaPlayer))
            newLore.add(ChatColor.RED + "" + ChatColor.ITALIC + getError(arenaPlayer));

        meta.setLore(newLore);
        this.setItemMeta(meta);
        return this;
    }

    // Gets the error the arena player has (not enough money, permission, or killcoins)
    public String getError(ArenaPlayer player) {
        StringBuilder builder = new StringBuilder();

        if (this.hasPermission() && !player.getPlayer().hasPermission(permission)) {
            builder.append("You don't have permission to use this item!");
        } else {
            System.out.println("Player coins: " + player.getKillCoins() + " Required: " + this.getKillCoins());
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

    // Adds a copy of the KillCoinItem to the player's inventory
    public void giveItemToPlayer(ArenaPlayer playerToGetItem) {
        // TODO: subtract their money and killcoins
        KillCoinItem itemToGive = new KillCoinItem(this);
        playerToGetItem.getPlayer().getInventory().addItem(itemToGive.getItemStack(playerToGetItem, false));
        if (this.hasExpirationTime()) {
            new ExpirationTime(itemToGive, playerToGetItem, getExpirationTime()).runTaskTimer(JavaPlugin.getProvidingPlugin(Paintball.class), 0, 10
            );
        }
    }

    // Checks to see if this KillCoinItem equals an ItemStack by looking at its name a lore
    public boolean equals(ItemStack itemStack) {
        if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            ItemMeta itemStackMeta = itemStack.getItemMeta();
            ItemMeta itemMeta = this.getItemMeta();
            return itemMeta.hasDisplayName() && itemMeta.hasDisplayName() && itemMeta.getDisplayName().equals(itemStackMeta.getDisplayName()) && itemMeta.getLore().equals(itemStackMeta.getLore());
        }
        return false;
    }

    // Gets the type of item TODO: Replace with return of enum
    public String getItemType() {
        return type;
    }

    // The method that gets fired whenever a player clicks on the item
    public void onClickItem(PlayerInteractEvent event) {}

    // If there is an error when adding the item to the inventory (player doesn't have enough killcoins, permission, etc)
    public boolean hasError(ArenaPlayer player) {
        return !(getError(player) == null);
    }

    // Checks to see if this KillCoinItem is a configItem
    public boolean hasType() {
        return configItem;
    }

    // Weather or not to show the item in the inventory
    public boolean showItem() {
        return showItem;
    }

    // Checks to see if the item has a description
    public boolean hasDescription() {
        return !(description.isEmpty());
    }

    // Checks to see if the item has a expiration date
    public boolean hasExpirationTime() {
        return !(expirationTime <= 0);
    }

    // Checks to see if the item requires any money
    public boolean requiresMoney() {
        return !(money <= 0);
    }

    // Checks to see if the item requires any killcoins
    public
    boolean requiresKillCoins() {
        return !(killcoins <= 0);
    }

    // Checks to see if the player requires any permissions
    public boolean hasPermission() {
        return !(permission.equals("none"));
    }

    // Important method that if the player has 2 of the same item names in their inventory, will rename this one to the name, with a space so ExpirationTime doesn't get confused on which is which
    private void setItemDisplayName(PlayerInventory inv) {
        for (ItemStack item : inv) {
            // while the item has the same name, add a space
            while (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(this.getItemName(true))) {
                StringBuilder builder = new StringBuilder(getItemName(true));
                builder.append(" "); // TODO: append to begining too so formating isn't mesed up
                this.nameWithSpaces = builder.toString();
            }
        }
    }
}