package info.projetcohesion.mcplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * GUIUtils.java
 * <p>
 * Manages the GUI (Inventory) system.
 *
 * @author Jack Hogg
 */
public class GUIUtils {

    private final Inventory _inv;

    private static final HashMap<String, Integer> s_items = new HashMap<>();
    private static final HashMap<String, Integer> s_prices = new HashMap<>();

    /**
     * GUIUtils constructor
     *
     * @param player Owner of the GUI
     * @param size   Size of the GUI
     * @param title  Title of the GUI
     */
    public GUIUtils(Player player, int size, String title) {

        assert size % 9 == 0;

        if (getPrices().size() != 0) getPrices().clear();
        if (getItems().size() != 0) getItems().clear();

        this._inv = Bukkit.createInventory(player,
                size,
                net.md_5.bungee.api.ChatColor.of("#007f7f")
                        + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500")
                        + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "] - "
                        + title);

        for (int i = 0; i < size; i++)
            addItem(null, Material.WHITE_STAINED_GLASS_PANE, "", i, null);

        addItem(null, Material.BARRIER, ChatColor.RED + "Quitter", size - 1, null);
        addPlayerSkull(player, size - 9);
    }

    /**
     * Add an item to the GUI.
     *
     * @param id   Custom ID of the Item
     * @param mat  Material of the item
     * @param desc DisplayName of the item
     * @param slot Slot of the item
     * @param lore Lore of the item
     */
    public void addItem(String id, Material mat, String desc, int slot, List<String> lore) {
        if (id != null) s_items.put(id, slot);

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#007f7f") + desc);
        meta.setLore(lore);

        item.setItemMeta(meta);

        this._inv.setItem(slot, item);
    }

    /**
     * Add an item with a price to the GUI.
     *
     * @param id     Custom ID of the Item
     * @param mat    Material of the item
     * @param desc   DisplayName of the item
     * @param slot   Slot of the item
     * @param price  Price of the item
     * @param lore   Lore of the item
     * @param bought Says if the item has been bought
     */
    public void addItemWithPrice(String id, Material mat, String desc, int slot, int price, List<String> lore, boolean bought) {
        assert price >= 0;

        List<String> l = new ArrayList<>();
        l.add(" ");
        l.addAll(lore);
        l.add(" ");
        if (!bought)
            l.add(net.md_5.bungee.api.ChatColor.of("#007f7f")
                    + "Prix : "
                    + net.md_5.bungee.api.ChatColor.of("#ffa500")
                    + price
                    + " \u272a");
        else l.add(ChatColor.RED + "ACHET\u00C9");

        addItem(id, mat, desc, slot, l);
        s_prices.put(id, price);
    }

    /**
     * Adds a player's skull to the GUI
     *
     * @param player Player
     * @param slot   Slot of the skull
     */
    public void addPlayerSkull(Player player, int slot) {
        FileUtils f_man = new FileUtils("money");
        FileConfiguration file = f_man.get();

        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skull = (SkullMeta) item.getItemMeta();

        assert skull != null;
        skull.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + player.getName());

        ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(net.md_5.bungee.api.ChatColor.of("#007f7f")
                + "Votre solde : "
                + net.md_5.bungee.api.ChatColor.of("#ffa500")
                + file.getInt("accounts." + player.getUniqueId())
                + " \u272a");

        skull.setLore(lore);
        skull.setOwningPlayer(player);

        item.setItemMeta(skull);

        this._inv.setItem(slot, item);
    }

    /**
     * Open a GUI
     *
     * @param player Player
     */
    public void openInventory(Player player) {
        player.openInventory(this._inv);
    }

    /**
     * Get's the items according to their slot.
     *
     * @return pairs of keys of slots and values of item id's
     */
    public static HashMap<String, Integer> getItems() {
        return s_items;
    }

    /**
     * Get's the items' price's according to their slot.
     *
     * @return pairs of keys of slots and values of prices
     */
    public static HashMap<String, Integer> getPrices() {
        return s_prices;
    }

    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        for (Map.Entry<K, V> entry: map.entrySet())
        {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
