package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.SubCommand;
import info.projetcohesion.mcplugin.events.MapInitEvent;
import info.projetcohesion.mcplugin.utils.ImageStorageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class MapArtCommand implements SubCommand {
    private static final ImageStorageManager s_storage = new ImageStorageManager();

    /**
     * Get the ImageStorageManager used to manage images within this command.
     * @return The ImageStorageManager used by the command.
     * @see ImageStorageManager
     */
    public static ImageStorageManager getImageManager() {
        return s_storage;
    }

    @Override
    public String getName() {
        return "mapart";
    }

    @Override
    public String getDescription() {
        return "Récupère les maparts envoyés sur le site web.";
    }

    @Override
    public Map<String, String> getUsage() {
        HashMap<String, String> usage = new HashMap<>();
        usage.put("/pci mapart", "Affiche le nombre d'images actuellement stockées");
        usage.put("/pci mapart get <ID>", "Récupère en jeu l'image avec l'ID associé");
        usage.put("/pci mapart clear", "Vide les images en attente");

        return usage;
    }

    @Override
    public Map<String, String> getPermissions() {
        HashMap<String, String> perms = new HashMap<>();
        perms.put("/pci mapart", "");
        perms.put("/pci mapart clear", "pci.mapart.clear");

        return perms;
    }

    @Override
    public void commandUsage(Player player, String[] args) {
        if(args.length == 1) { // /pci mapart
            player.sendMessage("Il y a actuellement " + s_storage.size() + " image(s) stockée(s) actuellement.");
        } else if(args.length == 2 && args[1].equalsIgnoreCase("clear") && player.isOp()) { // /pci mapart clear
            s_storage.clean();
            player.sendMessage("Vidé.");
        } else if(args.length == 2 && args[1].equalsIgnoreCase("get")) { // /pci mapart get
            player.sendMessage("ID manquant");
        } else if(args.length == 3 && args[1].equalsIgnoreCase("get")) { // /pci mapart get <id>
            if (s_storage.exists(args[2])) {
                MapInitEvent.setWipId(args[2]); // Set the working id for the next map

                player.getInventory().addItem(new ItemStack(Material.MAP));

                player.sendMessage("Ouvrez une nouvelle carte pour admirer le résultat !");
            } else {
                player.sendMessage("ID inexistant.");
            }
        }
    }
}
