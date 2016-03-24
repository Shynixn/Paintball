package me.synapz.paintball.coin;

import me.synapz.paintball.Utils;
import me.synapz.paintball.events.ArenaClickItemEvent;
import me.synapz.paintball.players.ArenaPlayer;
import me.synapz.paintball.storage.Settings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CoinItem extends ItemStack {
    // Display name
    // Description
    // Cost
    // Coins
    // Expiration Time
    // Permission

    private String nameWithSpaces;
    private final String name;
    private final String description;
    private final double money;
    private final int coins;
    private final int expirationTime;
    private final String permission;
    private final boolean showItem;
    private final boolean configItem;
    private final String type;
    private final Sound sound;

    public CoinItem(Material material, String name, int amount, boolean showItem, String description, double money, int coins, int expirationTime, String permission, Sound sound) {
        super(material, amount);
        this.name = name;
        this.nameWithSpaces = name;
        this.description = description;
        this.money = money;
        this.coins = coins;
        this.expirationTime = expirationTime;
        this.permission = permission;
        this.showItem = showItem;
        this.configItem = false;
        this.type = "";
        this.sound = sound;

        CoinItemHandler.getHandler().addItem(this);
    }

    // Creates a CoinItem from config.yml based on a rawItem
    public CoinItem(String path, FileConfiguration file) {
        super(Material.valueOf(file.getString(path + ".material")), file.getInt(path + ".amount"));
        this.name = file.getString(path + ".name");
        this.nameWithSpaces = name;
        this.description = file.getString(path + ".description");
        this.money = file.getDouble(path + ".money");
        this.coins = file.getInt(path + ".coins");
        this.expirationTime = file.getInt(path + ".expiration-time");
        this.permission = file.getString(path + ".permission-required");
        this.showItem = file.getBoolean(path + ".shown");
        this.configItem = true;
        this.type = file.getString(path + ".type");
        this.sound = Sound.valueOf(file.getString(path + "sound"));

        CoinItemHandler.getHandler().addItem(this);
    }

    public CoinItem(CoinItem item) {
        super(item);
        this.name = item.getItemName(false);
        this.nameWithSpaces = name;
        this.description = item.getDescription();
        this.money = item.getMoney();
        this.coins = item.getCoins();
        this.expirationTime = item.getExpirationTime();
        this.permission = item.getPermission();
        this.showItem = item.showItem();
        this.configItem = item.hasType();
        this.type = item.getItemType();
        this.sound = item.getSound();
    }

    // Gets the item name
    public String getItemName(boolean withSpaces) {
        return ChatColor.RESET + (withSpaces ? nameWithSpaces : name);
    }

    // Gets the description of the item
    public String getDescription() {
        return description;
    }

    // Gets the money required to buy the item
    public double getMoney() {
        return money;
    }

    // Gets the item's amount of required coins
    public int getCoins() {
        return coins;
    }

    // Gets the item's expiration time
    public int getExpirationTime() {
        return expirationTime;
    }

    // Gets the items permission
    public String getPermission() {
        return permission;
    }

    // Sets the values specific to the arena player, then returns the item to be placed in coinshop
    // forCoinShop sees if it is a Coin Shop, if it is, do not add spaces to the name
    public CoinItem getItemStack(ArenaPlayer arenaPlayer, boolean forCoinShop) {
        ItemMeta meta = this.getItemMeta();
        List<String> newLore = new ArrayList<String>();

        if (!forCoinShop) { // if this isn't for the shop but for being placed in their inventory, add spaces to make the item different than others with same name
            setItemDisplayName(arenaPlayer.getPlayer().getInventory());
        }

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.getItemName(!forCoinShop)));

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
            newLore.add(Settings.THEME + "Cost: " + Settings.SECONDARY + arenaPlayer.getArena().CURRENCY + getMoney());

        if (requiresCoins())
            newLore.add(Settings.THEME + "Coins: " + Settings.SECONDARY + getCoins());

        if (hasError(arenaPlayer)) {
            getError(arenaPlayer).forEach((String lore) -> {
                newLore.add(ChatColor.RED + "" + ChatColor.ITALIC + lore);
            });
        }

        meta.setLore(newLore);
        this.setItemMeta(meta);
        return this;
    }

    // Gets the error the arena player has (not enough money, permission, or coins)
    public List<String> getError(ArenaPlayer player) {
        List<String> builder = new ArrayList<>();

        if (this.hasPermission() && !player.getPlayer().hasPermission(permission)) {
            builder.add("You don't have permission to use this item!");
        } else {
            if (this.requiresCoins() && player.getCoins() < this.getCoins())
                builder.add("You don't have enough coins ");

            if (this.requiresMoney() && Settings.ECONOMY.getBalance(player.getPlayer()) < getMoney())
                builder.add("You don't have enough money");
        }

        if (!builder.isEmpty()) {
            return builder;
        } else {
            return null;
        }
    }

    // Adds a copy of the CoinItem to the player's inventory
    public void giveItemToPlayer(ArenaPlayer playerToGetItem) {
        playerToGetItem.addItem(new CoinItem(this));
    }

    // Removes the item from the inventory
    public void remove(ArenaPlayer arenaPlayer) {
        Player player = arenaPlayer.getPlayer();
        if (player != null && player.getInventory().contains(this)) {
            for (ItemStack itemStack : player.getPlayer().getInventory().getContents()) {
                if (Utils.equals(itemStack, this.getItemName(true))) {
                    player.getPlayer().getInventory().remove(itemStack);
                    break;
                }
            }
        }
    }

    // Checks to see if this CoinItem equals an ItemStack by looking at its name and lore
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
    public void onClickItem(ArenaClickItemEvent event) {}

    // If there is an error when adding the item to the inventory (player doesn't have enough coins, permission, etc)
    public boolean hasError(ArenaPlayer player) {
        return !(getError(player) == null);
    }

    public Sound getSound() {
        return sound;
    }

    // Checks to see if this CoinItem is a configItem
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

    // Checks to see if the item requires any coins
    public boolean requiresCoins() {
        return !(coins <= 0);
    }

    public boolean hasSound() {
        return sound != null;
    }
    // Checks to see if the player requires any permissions
    public boolean hasPermission() {
        return !(permission.equalsIgnoreCase("none") || permission.equals(""));
    }

    // Important method that if the player has 2 of the same item names in their inventory, will rename this one to the name, with a space so ExpirationTime doesn't get confused on which is which
    private void setItemDisplayName(PlayerInventory inv) {
        for (ItemStack item : inv) {
            // while the item has the same name, add a space
            while (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(this.getItemName(true))) {
                StringBuilder builder = new StringBuilder(getItemName(true));
                builder.append(ChatColor.RESET);
                this.nameWithSpaces = builder.toString();
            }
        }
    }
}
