package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.utils.EcoUtils;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.GUIUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FancyInventoryEvent implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        if (e.getView().getTitle().contains(net.md_5.bungee.api.ChatColor.of("#007f7f")
                + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500")
                + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "] "))
            e.setCancelled(true);

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.BARRIER)) {
            e.getWhoClicked().closeInventory();
        }

    }

    @EventHandler
    public void onShopPurchase(InventoryClickEvent e) {
        FileUtils f_man = new FileUtils("shop");
        FileConfiguration file = f_man.get();
        EcoUtils eco = new EcoUtils();

        HashMap<Integer, String> items = GUIUtils.getItems();
        HashMap<Integer, Integer> prices = GUIUtils.getPrices();

        if (e.getView().getTitle().contains("Magasin")
                && items.containsKey(e.getSlot())) {
            if (file.getStringList("shop.purchased." + e.getWhoClicked().getUniqueId()).contains(items.get(e.getSlot()))) {
                e.getWhoClicked().sendMessage(ChatColor.RED + "ERROR: Vous avez déjà acheté ceci.");
                return;
            }

            if (eco.getAccountBalance((Player) e.getWhoClicked()) >= prices.get(e.getSlot())) {

                // UPDATE ITEM
                ItemMeta meta = Objects.requireNonNull(e.getCurrentItem()).getItemMeta();
                assert meta != null;
                List<String> lore = meta.getLore();

                assert lore != null;
                lore.add(ChatColor.RED + "ACHETE");

                meta.setLore(lore);
                e.getCurrentItem().setItemMeta(meta);

                // AJOUTER DANS LES CHOSES ACHETES
                List<String> purchased;

                if (!file.contains("shop.purchased." + e.getWhoClicked().getUniqueId())) purchased = new ArrayList<>();
                else purchased = file.getStringList("shop.purchased." + e.getWhoClicked().getUniqueId());

                purchased.add(items.get(e.getSlot()));
                file.set("shop.purchased." + e.getWhoClicked().getUniqueId(), purchased);

                e.getWhoClicked().sendMessage(ChatColor.GREEN + "Bravo ! Votre commande a été passée.");
                eco.pay((Player) e.getWhoClicked(), prices.get(e.getSlot()));
                f_man.save();

            } else e.getWhoClicked().sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas assez pour acheter ceci.");
        }
    }


    @EventHandler
    public void onZoneChange(InventoryClickEvent e) {
        FileUtils f_man = new FileUtils("zones");
        FileConfiguration file = f_man.get();

        HashMap<Integer, String> items = GUIUtils.getItems();

        if (e.getView().getTitle().contains("Gestion de zone")
                && items.containsKey(e.getSlot())) {

            // UPDATE ITEM
            ItemMeta meta = Objects.requireNonNull(e.getCurrentItem()).getItemMeta();
            assert meta != null;
            List<String> lore = meta.getLore();

            assert lore != null;

            lore.remove(lore.size() - 1);

            List<String> l = file.getStringList("zones." + e.getWhoClicked().getUniqueId() + ".effects");
            if (l.contains(items.get(e.getSlot()))) {
                lore.add(ChatColor.RED + "Desactivé");
                l.remove(items.get(e.getSlot()));
            } else {
                lore.add(ChatColor.GREEN + "Activé");
                l.add(items.get(e.getSlot()));
            }
            file.set("zones." + e.getWhoClicked().getUniqueId() + ".effects", l);

            meta.setLore(lore);
            e.getCurrentItem().setItemMeta(meta);

            f_man.save();

        }
    }

}
