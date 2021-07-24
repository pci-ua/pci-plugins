package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.SubCommand;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.GUIUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ShopCommand.java
 * <p>
 * Implements SubCommand.java and it's methods.
 * Used to open the shop GUI.
 *
 * @author Jack Hogg
 */
public class ShopCommand implements SubCommand {

    @Override
    public String getName() {
        return "shop";
    }

    @Override
    public String getDescription() {
        return "Ouvre le magasin";
    }

    @Override
    public Map<String, String> getUsage() {
        Map<String, String> list = new HashMap<>();
        list.put("/pci shop", "Ouvre le magasin");

        return list;
    }

    @Override
    public Map<String, String> getPermissions() {
        Map<String, String> list = new HashMap<>();
        list.put("/pci shop", "");

        return list;
    }

    @Override
    public void commandUsage(Player player, String[] args) {

        FileUtils f_man = new FileUtils("shop");
        FileConfiguration file = f_man.get();

        GUIUtils gui = new GUIUtils(player, 27, "Magasin");

        if (args.length == 1) {
            gui.addItemWithPrice("pvp", Material.PLAYER_HEAD, "Gestion du PvP", 3, 50, Arrays.asList("Activez/Désactivez le PvP dans vos zones."),
                    file.getStringList("shop.purchased." + player.getUniqueId()).contains("pvp"));
            gui.addItemWithPrice("pve", Material.ZOMBIE_HEAD, "Gestion du PvE", 4, 40, Arrays.asList("Activez/Désactivez le PvE dans vos zones."),
                    file.getStringList("shop.purchased." + player.getUniqueId()).contains("pve"));
            gui.addItemWithPrice("nat", Material.FIRE_CHARGE, "Gestion de la nature", 5, 50, Arrays.asList("Activez/Désactivez les dégâts naturels dans votre zone."),
                    file.getStringList("shop.purchased." + player.getUniqueId()).contains("nat"));

            gui.openInventory(player);
        } else player.sendMessage(ChatColor.RED + "ERROR: Mauvaise utilisation de la commande.");

    }
}
