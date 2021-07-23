package info.projetcohesion.mcplugin;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Item {

    private String _desc;
    private List<String> _lore;
    private ItemStack _item;
    private ItemMeta _meta;

    private String _id;

    public Item(String id, Material m, int amount, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(m, amount);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setLore(lore);

        item.setItemMeta(meta);

        this._id = id;
        this._desc = displayName;
        this._lore = lore;
        this._meta = meta;
        this._item = item;
    }

    public ItemStack getItem() {
        return _item;
    }

    public void setItem(ItemStack item) {
        this._item = item;
    }

    public ItemMeta getMeta() {
        return _meta;
    }

    public void setMeta(ItemMeta meta) {
        this._meta = meta;
    }

    public String getDesc() {
        return _desc;
    }

    public void setDesc(String desc) {
        _desc = desc;
    }

    public List<String> getLore() {
        return _lore;
    }

    public void setLore(List<String> lore) {
        _lore = lore;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        _id = id;
    }
}
