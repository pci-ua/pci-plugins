package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.SubCommand;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.GUIUtils;
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

        return list;
    }

    @Override
    public void commandUsage(Player player, String[] args) {
        FileUtils f_man = new FileUtils("zones");
        FileConfiguration file = f_man.get();
        FileUtils f_man_s = new FileUtils("shop");
        FileConfiguration file_s = f_man_s.get();

        String p_uuid = "zones." + player.getUniqueId();

        if (args.length == 1) {
            GUIUtils gui = new GUIUtils(player, 27, "Gestion de zone");
            String actif = ChatColor.GREEN + "Activé", desactif = ChatColor.RED + "Desactivé";

            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("pvp"))
                gui.addItem("pvp", Material.PLAYER_HEAD, "Gestion du PvP", 3, Arrays.asList("Activez/Désactivez le PvP dans vos zones.", " ",
                        file.getStringList("zones." + player.getUniqueId() + ".effects").contains("pvp") ? actif : desactif));
            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("pve"))
                gui.addItem("pve", Material.ZOMBIE_HEAD, "Gestion du PvE", 4, Arrays.asList("Activez/Désactivez le PvE dans vos zones.", " ",
                        file.getStringList("zones." + player.getUniqueId() + ".effects").contains("pve") ? actif : desactif));
            if (file_s.getStringList("shop.purchased." + player.getUniqueId()).contains("nat"))
                gui.addItem("nat", Material.FIRE_CHARGE, "Gestion de la nature", 5, Arrays.asList("Activez/Désactivez les dégâts naturels dans votre zone.", " ",
                        file.getStringList("zones." + player.getUniqueId() + ".effects").contains("nat") ? actif : desactif));

            gui.openInventory(player);
            return;
        }

        if (args[1].equalsIgnoreCase("claim")
                && args.length == 2) {

            Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

            // Vérifier que le chunk voulu n'a pas déjà été saisi par un joueur (dont lui)
            if (file.getConfigurationSection("zones") != null)
                for (String local : Objects.requireNonNull(file.getConfigurationSection("zones")).getKeys(false))
                    for (int i = 1; i <= file.getInt("zones." + local + ".number_of_chunks"); i++)
                        if (chunk.getX() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x")
                                && chunk.getZ() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z")) {
                            player.sendMessage(ChatColor.RED + "ERROR: Cette zone ne peut être possédée.");
                            return;
                        }

            if (file.isConfigurationSection(p_uuid)) {
                // TODO: Changer la constante 4
                if (file.getInt(p_uuid + ".number_of_chunks") == 4) {
                    player.sendMessage(ChatColor.RED + "ERROR: Tu as déjà le nombre de zones maximales que tu peux posséder.");
                    return;
                } else {
                    int chunks = file.getInt(p_uuid + ".number_of_chunks");

                    // Ajouter le chunk
                    file.set(p_uuid + ".chunks." + (chunks + 1) + ".x", chunk.getX());
                    file.set(p_uuid + ".chunks." + (chunks + 1) + ".z", chunk.getZ());
                    file.set(p_uuid + ".number_of_chunks", chunks + 1);

                    if (!file.getStringList(p_uuid + ".allowed").contains(player.getUniqueId().toString()))
                        file.getStringList(p_uuid + ".allowed").add(player.getUniqueId().toString());

                }
            } else {
                List<String> list = new ArrayList<>();
                list.add(player.getUniqueId().toString());

                file.set(p_uuid + ".number_of_chunks", 1);
                file.set(p_uuid + ".allowed", list);
                file.set(p_uuid + ".chunks.1.x", chunk.getX());
                file.set(p_uuid + ".chunks.1.z", chunk.getZ());
            }

            player.sendMessage(ChatColor.GREEN + "Ce chunk est désormais à vous !");

        } else if (args[1].equalsIgnoreCase("unclaim")
                && (args.length == 2 || args.length == 3)) {

            if (!file.contains(p_uuid)
                    || file.getInt(p_uuid + ".number_of_chunks") == 0)
                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zone à laisser de côté.");

            else {
                int chunks = file.getInt(p_uuid + ".number_of_chunks");

                // Vérifier si le chunk sous les pieds est au joueur
                if (args.length == 2) {
                    for (String key : Objects.requireNonNull(file.getConfigurationSection(p_uuid + ".chunks")).getKeys(false)) {
                        if (file.getInt(p_uuid + ".chunks." + key + ".x") == player.getWorld().getChunkAt(player.getLocation()).getX()
                                && file.getInt(p_uuid + ".chunks." + key + ".z") == player.getWorld().getChunkAt(player.getLocation()).getZ()) {

                            // Faire revenir tous les autres chunks
                            for (int i = Integer.parseInt(key) + 1; i <= chunks; i++) {
                                file.set(p_uuid + ".chunks." + (char) ((i - 1) + '0'),
                                        file.get(p_uuid + ".chunks." + (char) (i + '0')));
                            }

                            file.set(p_uuid + ".chunks." + (char) (chunks + '0'),
                                    null);

                            file.set(p_uuid + ".number_of_chunks",
                                    chunks - 1);

                            player.sendMessage(ChatColor.GREEN + "Ce chunk est désormais à la nature !");

                            break;
                        }
                    }

                    player.sendMessage(ChatColor.RED + "ERROR: Le chunk sous vos pieds n'est pas à vous.");

                } else if (NumberUtils.isNumber(args[2])) { // Sinon, vérifier le chunk identifié

                    if (file.isConfigurationSection(p_uuid + ".chunks." + args[2])) {
                        file.set(p_uuid + ".chunks." + args[2], null);

                        // Faire revenir tous les autres chunks
                        for (int i = Integer.parseInt(args[2]) + 1; i <= chunks; i++) {
                            file.set(p_uuid + ".chunks." + (char) ((i - 1) + '0'),
                                    file.get(p_uuid + ".chunks." + (char) (i + '0')));
                        }

                        file.set(p_uuid + ".chunks." + (char) (chunks + '0'),
                                null);

                        file.set(p_uuid + ".number_of_chunks",
                                chunks - 1);

                        player.sendMessage(ChatColor.GREEN + "Le chunk est désormais à la nature !");

                    } else {
                        player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de chunk avec cet identifiant.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                }
            }

        } else if (args[1].equalsIgnoreCase("abandon")
                && args.length == 2) {

            if (file.isConfigurationSection("zones." + player.getUniqueId())
                    && (file.getInt(p_uuid + ".number_of_chunks")) != 0) {
                file.set(p_uuid + ".number_of_chunks", 0);
                file.set(p_uuid + ".chunks", null);
                player.sendMessage(ChatColor.GREEN + "Tous vos chunks ont été abandonnés...");
            } else player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones à abandonner");

        } else if (args[1].equalsIgnoreCase("add")
                && args.length == 3) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);
            if (pl != null && pl.hasPlayedBefore())
                if (!file.getStringList(p_uuid + ".allowed").contains(pl.getUniqueId().toString())) {
                    // DEFECTUEUX
                    // file.getStringList(p_uuid + ".allowed").add(pl.getUniqueId().toString());

                    List<String> l = file.getStringList(p_uuid + ".allowed");
                    l.add(pl.getUniqueId().toString());
                    file.set(p_uuid + ".allowed", l);

                    player.sendMessage(ChatColor.GREEN + "Le joueur "
                            + ChatColor.GOLD + pl.getName()
                            + ChatColor.GREEN + " a été ajouté à votre zone.");

                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est déjà autorisé sur votre zone.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur");

        } else if (args[1].equalsIgnoreCase("remove")
                && args.length == 3) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);

            assert pl != null;
            if (pl.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                player.sendMessage(ChatColor.RED + "Wow, wow, wow... Tu es propriétaire !");
                return;
            }

            if (pl.hasPlayedBefore())
                if (file.getStringList(p_uuid + ".allowed").contains(pl.getUniqueId().toString())) {
                    // DEFECTUEUX
                    // file.getStringList(p_uuid + ".allowed").remove(pls);

                    List<String> l = file.getStringList(p_uuid + ".allowed");
                    l.remove(pl.getUniqueId().toString());
                    file.set(p_uuid + ".allowed", l);

                    player.sendMessage(ChatColor.GREEN + "Le joueur "
                            + ChatColor.GOLD + pl.getName()
                            + ChatColor.GREEN + " a été retiré à votre zone.");

                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'est pas autorisé sur votre zone.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur");

        } else if (args[1].equalsIgnoreCase("spawn")
                && (args.length == 2 || args.length == 3)) {

            if (!file.isConfigurationSection("zones." + player.getUniqueId())
                    || file.getInt(p_uuid + ".number_of_chunks") == 0) {
                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones.");
                return;
            }

            String id_zone = "1";
            Chunk chunk;

            if (args.length == 3) {
                if (!NumberUtils.isNumber(args[2])) {
                    player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                    return;
                }
                id_zone = args[2];
            }

            chunk = Objects.requireNonNull(Bukkit.getServer().getPlayer(player.getUniqueId())).getWorld().getChunkAt(
                    file.getInt(p_uuid + ".chunks." + id_zone + ".x"),
                    file.getInt(p_uuid + ".chunks." + id_zone + ".z")
            );

            // Lancer la téléportation au bout de 5 secondes.
            task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable() {
                int timer = 5;

                public void run() {
                    player.sendMessage(ChatColor.GREEN + "PC[i] Téléportation dans " + timer + " secondes.");

                    if (timer == 0) {
                        Location loc = new Location(player.getWorld(),
                                chunk.getX() << 4,
                                player.getWorld().getHighestBlockAt(chunk.getX() << 4, chunk.getZ() << 4).getY(),
                                chunk.getZ() << 4);
                        player.teleport(loc);
                        Bukkit.getServer().getScheduler().cancelTask(task); // Détruire le Runnable
                    } else timer--;
                }
            }, 0, 20);

        }

        file.options().copyDefaults(true);
        f_man.save();
    }
}
