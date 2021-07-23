package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.Item;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GUIUtils {

    private final Inventory _inv;

    private static final HashMap<Integer, String> s_items = new HashMap<>();
    private static final HashMap<Integer, Integer> s_prices = new HashMap<>();

    public GUIUtils(Player player, int size, String title) {

        assert size % 9 == 0;

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

    public void addItem(String id, Material mat, String desc, int slot, List<String> lore) {
        if (id != null) s_items.put(slot, id);

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(net.md_5.bungee.api.ChatColor.of("#007f7f") + desc);
        meta.setLore(lore);

        item.setItemMeta(meta);

        this._inv.setItem(slot, item);
    }

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
        s_prices.put(slot, price);
    }

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

    public void openInventory(Player player) {
        player.openInventory(this._inv);
    }

    public static HashMap<Integer, String> getItems() {
        return s_items;
    }

    public static HashMap<Integer, Integer> getPrices() {
        return s_prices;
    }

}
