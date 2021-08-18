package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.*;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.GUIUtils;
import info.projetcohesion.mcplugin.utils.ZoneUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * ZoneCommand.java
 * <p>
 * Implements SubCommand.java and it's methods.
 * Used to control the player's personal chunks.
 *
 * @author Jack Hogg
 */
public class ZoneCommand implements SubCommand {

    private final Plugin _plugin = Plugin.getPlugin();
    private int task = 0;

    @Override
    public String getName() {
        return "zone";
    }

    @Override
    public String getDescription() {
        return "Gestion des zones";
    }

    @Override
    public Map<String, String> getUsage() {
        Map<String, String> list = new HashMap<>();

        list.put("/pci zone claim", "Saisir le chunk");
        list.put("/pci zone unclaim [zone_id]", "Abandonner le chunk");
        list.put("/pci zone abandon", "Abandonner tous les chunks");
        list.put("/pci zone <add | remove>", "Autoriser ou interdire un joueur sur vos chunks");
        list.put("/pci zone spawn [zone_id]", "Se téléporter à un chunk");
        list.put("/pci zone categ [zone_id]", "Changer la catégorie de sa zone");

        return list;
    }

    @Override
    public Map<String, String> getPermissions() {
        Map<String, String> list = new HashMap<>();
        list.put("/pci zone claim", "");
        list.put("/pci zone unclaim [zone_id]", "");
        list.put("/pci zone abandon", "");
        list.put("/pci zone <add | remove>", "");
        list.put("/pci zone spawn [zone_id]", "");
        list.put("/pci zone categ [zone_id]", "");

        return list;
    }

    @Override
    public void commandUsage(Player player, String[] args) {
        FileUtils f_man_s = new FileUtils("shop");
        FileConfiguration file_s = f_man_s.get();

        ZoneData zones = ZoneUtils.getZones().get(player.getUniqueId().toString());

        if (args.length == 1) {

            GUIUtils gui = new GUIUtils(player, 27, "Gestion de zone");
            String actif = ChatColor.GREEN + "Activé", desactif = ChatColor.RED + "Desactivé";

            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("pvp"))
                gui.addItem("pvp", Material.PLAYER_HEAD, "Gestion du PvP", 3, Arrays.asList("Activez/Désactivez le PvP dans vos zones.", " ",
                        zones.getEffects().contains("pvp") ? actif : desactif));

            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("pve"))
                gui.addItem("pve", Material.ZOMBIE_HEAD, "Gestion du PvE", 4, Arrays.asList("Activez/Désactivez le PvE dans vos zones.", " ",
                        zones.getEffects().contains("pve") ? actif : desactif));

            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("nat"))
                gui.addItem("nat", Material.FIRE_CHARGE, "Gestion de la nature", 5, Arrays.asList("Activez/Désactivez les dégâts naturels dans votre zone.", " ",
                        zones.getEffects().contains("nat") ? actif : desactif));

            gui.openInventory(player);
            return;
        }

        if (args[1].equalsIgnoreCase("claim")
                && args.length == 2) {

            if (zones.getNumberOfChunks() == 4)
            {
                player.sendMessage(ChatColor.RED + "ERROR: Tu as déjà le nombre de zones maximales que tu peux posséder.");
                return;
            }

            Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

            // Vérifier que le chunk voulu n'a pas déjà été saisi par un joueur (dont lui)
            for (ZoneChunkData data : ZoneUtils.getAllChunks()) {
                if (chunk.getX() == data.getX()
                        && chunk.getZ() == data.getZ()) {
                    player.sendMessage(ChatColor.RED + "ERROR: Cette zone ne peut être possédée.");
                    return;
                }
            }

            zones.addChunk(
                chunk.getX(),
                chunk.getZ()
            );

            if (!zones.isMember(player.getUniqueId().toString())) zones.addMember(
                player.getUniqueId().toString()
            );

            player.sendMessage(ChatColor.GREEN + "Ce chunk est désormais à vous !");

        } else if (args[1].equalsIgnoreCase("unclaim")
                && (args.length == 2 || args.length == 3)) {

            if (zones == null
                    || !zones.hasZones())
                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zone à laisser de côté.");

            else {
                if (args.length == 2) {
                    Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
                    zones.removeChunk(chunk.getX(), chunk.getZ());
                    player.sendMessage(ChatColor.GREEN + "Le chunk est rendu à la nature.");
                } else if (NumberUtils.isNumber(args[2])) { // Sinon, vérifier le chunk identifié
                    zones.removeChunkById(Integer.parseInt(args[2]) - 1);
                    player.sendMessage(ChatColor.GREEN + "Le chunk est rendu à la nature.");
                } else {
                    player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                }
            }
        } else if (args[1].equalsIgnoreCase("abandon")
                && args.length == 2) {

            if (zones != null
                    && zones.hasZones()) {
                zones.removeAllChunks();
                player.sendMessage(ChatColor.GREEN + "Tous vos chunks ont été abandonnés...");
            } else player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones à abandonner");

        } else if (args[1].equalsIgnoreCase("add")
                && args.length == 3) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);
            if (pl != null && pl.hasPlayedBefore())
                if (!zones.isMember(pl.getUniqueId().toString())) {
                    zones.addMember(pl.getName());

                    player.sendMessage(ChatColor.GREEN + "Le joueur "
                            + ChatColor.GOLD + pl.getName()
                            + ChatColor.GREEN + " a été ajouté à votre zone.");

                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est déjà autorisé sur votre zone.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur.");

        } else if (args[1].equalsIgnoreCase("remove")
                && args.length == 3) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);

            assert pl != null;
            if (pl.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.RED + "Wow, wow, wow... Tu es propriétaire !");
                return;
            }

            if (pl.hasPlayedBefore())
                if (zones.isMember(pl.getUniqueId().toString())) {

                    zones.removeMember(pl.getUniqueId().toString());

                    player.sendMessage(ChatColor.GREEN + "Le joueur "
                            + ChatColor.GOLD + pl.getName()
                            + ChatColor.GREEN + " a été retiré à votre zone.");

                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'est pas autorisé sur votre zone.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur");

        } else if (args[1].equalsIgnoreCase("spawn")
                && (args.length == 2 || args.length == 3)) {

            if (zones == null
                    || zones.getNumberOfChunks() == 0) {
                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones.");
                return;
            }

            int id_zone = 1;

            if (args.length == 3) {
                if (!NumberUtils.isNumber(args[2])) {
                    player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                    return;
                }
                id_zone = Integer.parseInt(args[2]);
            }

            final int x = zones.getChunks().get(id_zone - 1).getX();
            final int z = zones.getChunks().get(id_zone - 1).getZ();

            // Lancer la téléportation au bout de 5 secondes.
            task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable() {
                int timer = 5;

                public void run() {
                    player.sendMessage(ChatColor.GREEN + "PC[i] Téléportation dans " + timer + " secondes.");

                    if (timer == 0) {
                        Location loc = new Location(player.getWorld(),
                                x << 4,
                                player.getWorld().getHighestBlockAt(x << 4, z << 4).getY(),
                                z << 4);
                        player.teleport(loc);
                        Bukkit.getServer().getScheduler().cancelTask(task); // Détruire le Runnable
                    } else timer--;
                }
            }, 0, 20);

        } else if (args[1].equalsIgnoreCase("categ")
                && args.length == 3) {

            if (zones == null
                    || zones.getNumberOfChunks() == 0) {
                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones.");
                return;
            }

            if (!NumberUtils.isNumber(args[2])) {
                player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                return;
            }

            String id_zone = args[2];

            /* TODO : Créer un GUI pour changer le type de zone
            - Un item avec la catégorie en cours */

            GUIUtils gui = new GUIUtils(player, 27, "Catégorie de zone - " + ChatColor.RED + "Chunk " + id_zone);
            String zoneState = zones.getChunks().get(Integer.parseInt(id_zone)).getCategory();

            gui.addItem("z_def", Material.STONE, "Défault", 2,
                    Arrays.asList("Configuration par défault.",
                            zoneState.equals("z_def") ? ChatColor.GREEN + "Actif" : ""));

            gui.addItem("z_pers", Material.CHEST, "Personnel", 3,
                    Arrays.asList(
                            "Aucun PvP sur votre zone mais vos coffres seront protégés.",
                            zoneState.equals("z_pers") ? ChatColor.GREEN + "Actif" : ""));

            gui.addItem("z_war", Material.DIAMOND_SWORD, "Warzone", 5,
                    Arrays.asList(
                            "Votre zone sera dédiée au PvP, que ce soit avec vos",
                            "membres ou les autres membres du serveur.",
                            zoneState.equals("z_war") ? ChatColor.GREEN + "Actif" : ""));

            gui.addItem("z_comm", Material.WHITE_BANNER, "Communauté", 6,
                    Arrays.asList(
                            "Toute interaction sera proscrite mais l'ensemble du",
                            "serveur pourra participer et intéragir sur votre zone.",
                            zoneState.equals("z_comm") ? ChatColor.GREEN + "Actif" : ""));

            gui.openInventory(player);

        }
    }
}
