package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.ZoneCategory;
import info.projetcohesion.mcplugin.ZoneData;
import info.projetcohesion.mcplugin.utils.EcoUtils;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.GUIUtils;
import info.projetcohesion.mcplugin.utils.ZoneUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * FancyInventoryEvent.java
 * <p>
 * Implements Listener.java.
 * Used to control the events after the player clicks on an item
 * in a specific personalised inventory.
 *
 * @author Jack Hogg
 */
public class FancyInventoryEvent implements Listener {

    /**
     * General handling of the clicks.
     *
     * @param e Event handled
     */
    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        if (e.getView().getTitle().contains(net.md_5.bungee.api.ChatColor.of("#007f7f")
                + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500")
                + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "] "))
            e.setCancelled(true);

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.BARRIER))
            e.getWhoClicked().closeInventory();

    }

    /**
     * Handling of the clicks on the shop inventory.
     *
     * @param e Event handled
     */
    @EventHandler
    public void onShopPurchase(InventoryClickEvent e) {
        FileUtils f_man = new FileUtils("shop");
        FileConfiguration file = f_man.get();
        EcoUtils eco = new EcoUtils();

        HashMap<String, Integer> items = GUIUtils.getItems();
        HashMap<String, Integer> prices = GUIUtils.getPrices();

        if (e.getView().getTitle().contains("Magasin")
                && items.containsValue(e.getSlot())) {

            if (file.getStringList("shop.purchased." + e.getWhoClicked().getUniqueId()).contains(GUIUtils.getKey(items, e.getSlot()))) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "ERROR: Vous avez déjà acheté ceci.");
                return;
            }

            if (eco.getAccountBalance((Player) e.getWhoClicked()) >= prices.get(GUIUtils.getKey(items, e.getSlot()))) {

                // UPDATE ITEM
                ItemMeta meta = Objects.requireNonNull(e.getCurrentItem()).getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();

                assert lore != null;
                lore.remove(lore.size() - 1);
                lore.add(ChatColor.RED + "ACHETE");

                meta.setLore(lore);
                e.getCurrentItem().setItemMeta(meta);

                // AJOUTER DANS LES CHOSES ACHETES
                List<String> purchased;

                if (!file.contains("shop.purchased." + e.getWhoClicked().getUniqueId())) purchased = new ArrayList<>();
                else purchased = file.getStringList("shop.purchased." + e.getWhoClicked().getUniqueId());

                purchased.add(GUIUtils.getKey(items, e.getSlot()));
                file.set("shop.purchased." + e.getWhoClicked().getUniqueId(), purchased);

                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Bravo ! Votre commande a été passée.");
                eco.pay((Player) e.getWhoClicked(), prices.get(GUIUtils.getKey(items, e.getSlot())));
                f_man.save();

            } else e.getWhoClicked().sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas assez pour acheter ceci.");
        }
    }

    /**
     * Handling of the clicks on the zone inventory that handle zone effects.
     *
     * @param e Event handled
     */
    @EventHandler
    public void onZoneChange(InventoryClickEvent e) {

        ZoneData zones = ZoneUtils.getZones().get(e.getWhoClicked().getUniqueId().toString());

        HashMap<String, Integer> items = GUIUtils.getItems();

        if (e.getView().getTitle().contains("Gestion de zone")
                && items.containsValue(e.getSlot())) {

            // UPDATE ITEM
            ItemMeta meta = Objects.requireNonNull(e.getCurrentItem()).getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();

            assert lore != null;

            lore.remove(lore.size() - 1);

            if (zones.getEffects().contains(GUIUtils.getKey(items, e.getSlot()))) {
                lore.add(ChatColor.RED + "Desactivé");
                zones.getEffects().remove(GUIUtils.getKey(items, e.getSlot()));
            } else {
                lore.add(ChatColor.GREEN + "Activé");
                zones.getEffects().add(GUIUtils.getKey(items, e.getSlot()));
            }

            meta.setLore(lore);
            e.getCurrentItem().setItemMeta(meta);

        }
    }


    /**
     * Handling of the clicks on the zone inventory that handle zone categories.
     *
     * @param e Event handled
     */
    @EventHandler
    public void onZoneCategoryChange(InventoryClickEvent e) {

        ZoneData zones = ZoneUtils.getZones().get(e.getWhoClicked().getUniqueId().toString());

        HashMap<String, Integer> items = GUIUtils.getItems();

        if (e.getView().getTitle().contains("Catégorie de zone")
                && items.containsValue(e.getSlot())) {

            String extract = "Chunk ";
            String chunk = e.getView().getTitle().substring(e.getView().getTitle().indexOf(extract));
            chunk = chunk.substring(extract.length() + chunk.indexOf(extract));

            // UPDATE ITEM
            ItemMeta meta = Objects.requireNonNull(e.getCurrentItem()).getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();

            assert lore != null;

            if (!lore.get(lore.size() - 1).equalsIgnoreCase(ChatColor.GREEN + "Actif")) {
                for (int i : items.values()) {

                    // Récupérer la configuration active
                    if (e.getInventory().getItem(i).getItemMeta().getLore()
                            .get(e.getInventory().getItem(i).getItemMeta().getLore().size() - 1)
                            .equals(ChatColor.GREEN + "Actif")) {

                        ItemMeta aux = e.getInventory().getItem(i).getItemMeta();
                        List<String> loreAux = aux.getLore();

                        loreAux.remove(e.getInventory().getItem(i).getItemMeta().getLore().size() - 1);
                        aux.setLore(loreAux);

                        e.getInventory().getItem(i).setItemMeta(aux);
                    }
                }

                lore.add(ChatColor.GREEN + "Actif");

                zones.getChunks().get(Integer.parseInt(chunk) - 1).setCategory(GUIUtils.getKey(items, e.getSlot()));
            } else {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(ChatColor.RED + "Vous avez déjà attribué cette catégorie.");
                return;
            }

            meta.setLore(lore);
            e.getCurrentItem().setItemMeta(meta);

        }
    }

}
