package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.managers.FileManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class MainCommand implements CommandExecutor {

    /* TODO list
        - Créer des constantes
        - Répartir les sous commandes dans plusieurs fichiers
     */

    private final Plugin _plugin = Plugin.getPlugin();
    private int task = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("pci")) {
            if (sender instanceof Player player) {

                // Affiche la help list
                if (args.length == 0) usage(player);
                else {
                    if (args[0].equalsIgnoreCase("zone")) {
                        FileManager f_man = new FileManager("zones");
                        FileConfiguration file = f_man.get();

                        String p_uuid = "zones." + player.getUniqueId();

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
                                            return true;
                                        }

                            if (file.isConfigurationSection(p_uuid)) {
                                // TODO: Changer la constante 4
                                if (file.getInt(p_uuid + ".number_of_chunks") == 4) {
                                    player.sendMessage(ChatColor.RED + "ERROR: Tu as déjà le nombre de zones maximales que tu peux posséder.");
                                    return true;
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

                                            break;
                                        }
                                    }

                                    player.sendMessage(ChatColor.RED + "ERROR: Le chunk sous vos pieds n'est pas à vous.");

                                } else if (NumberUtils.isNumber(args[2])) { // Sinon, vérifier le chunk identifié

                                    // int id_zone = Integer.parseInt(args[2]);

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
                                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est déjà autorisé sur votre zone.");
                            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur");

                        } else if (args[1].equalsIgnoreCase("remove")
                                && args.length == 3) {

                            OfflinePlayer pl = Bukkit.getPlayer(args[2]);

                            assert pl != null;
                            if (pl.getUniqueId().toString().equals(player.getUniqueId().toString())) {
                                player.sendMessage(ChatColor.RED + "Wow, wow, wow... Tu es propriétaire !");
                                return true;
                            }

                            if (pl.hasPlayedBefore())
                                if (file.getStringList(p_uuid + ".allowed").contains(pl.getUniqueId().toString())) {
                                    // DEFECTUEUX
                                    // file.getStringList(p_uuid + ".allowed").remove(pls);

                                    List<String> l = file.getStringList(p_uuid + ".allowed");
                                    l.remove(pl.getUniqueId().toString());
                                    file.set(p_uuid + ".allowed", l);

                                } else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'est pas autorisé sur votre zone.");
                            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'a jamais été sur le serveur");

                        } else if (args[1].equalsIgnoreCase("spawn")
                                && (args.length == 2 || args.length == 3)) {

                            if (!file.isConfigurationSection("zones." + player.getUniqueId())
                                    || file.getInt(p_uuid + ".number_of_chunks") == 0) {
                                player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas de zones.");
                                return true;
                            }

                            String id_zone = "1";
                            Chunk chunk;

                            if (args.length == 3) {
                                if (!NumberUtils.isNumber(args[2])) {
                                    player.sendMessage(ChatColor.RED + "ERROR: L'identifiant entré n'est pas un entier.");
                                    return true;
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

                        } else usage(player);

                        file.options().copyDefaults(true);
                        f_man.save();

                    } else usage(player);
                }
            } else sender.sendMessage(ChatColor.RED + "ERROR: You do not have the permission to use this command");
        }

        return true;
    }

    // TODO: Command description
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GREEN + "---===== PC[" + ChatColor.GOLD + "i" + ChatColor.DARK_GREEN + "] =====---" +
                ChatColor.GREEN + "/pci\n" +
                ChatColor.AQUA + "zone claim " + ChatColor.GOLD + "" +
                ChatColor.AQUA + "zone unclaim " + ChatColor.GOLD + "[zone_id]" +
                ChatColor.AQUA + "zone spawn " + ChatColor.GOLD + "[zone_id]" +
                ChatColor.AQUA + "zone abandon " + ChatColor.GOLD + "" +
                ChatColor.AQUA + "zone add|remove " + ChatColor.GOLD + "<player_name>");
    }
}
