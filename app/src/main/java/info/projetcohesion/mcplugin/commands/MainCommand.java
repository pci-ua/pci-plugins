package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.Plugin;
import info.projetcohesion.mcplugin.managers.FileManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class MainCommand implements CommandExecutor {

    private Plugin _plugin = Plugin.getPlugin();
    private int task = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("pci"))
        {
            if (sender instanceof Player player) {

                // Affiche la help list
                if (args.length == 0)
                    usage(player);
                else {
                    if (args[0].equalsIgnoreCase("zone")) {
                        FileManager file = new FileManager("zones");

                            if (args[1].equalsIgnoreCase("claim")
                                    && args.length == 2)
                            {
                                /*
                                    1. Vérifier dans la liste complète des chunks si le chunk désiré n'est pas déjà attribué
                                    2. Vérifier si le joueur possède des chunks et qu'il n'a pas atteint la limite
                                    3. Ajout :
                                        3.1. S'il possède déjà des chunks, ajuster les valeurs
                                        3.2. Sinon initialiser le tout
                                 */

                                Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

                                if (file.get().getConfigurationSection("zones") != null) {
                                    for (String local : Objects.requireNonNull(file.get().getConfigurationSection("zones")).getKeys(false)) {
                                        for (int i = 1; i <= file.get().getInt("zones." + local + ".number_of_chunks"); i++) {
                                            if (chunk.getX() == file.get().getInt("zones." + local + ".chunks." + (char)(i + '0') + ".x")
                                                    && chunk.getZ() == file.get().getInt("zones." + local + ".chunks." + (char)(i + '0') + ".z")) {
                                                player.sendMessage("ERROR: Cette zone ne peut être possédée.");
                                                return true;
                                            }
                                        }
                                    }
                                }

                                if (file.get().isConfigurationSection("zones." + player.getUniqueId())) {
                                    if (file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks") == 4) {
                                        player.sendMessage("ERROR: Tu as déjà le nombre de zones maximales que tu peux posséder.");
                                    } else { // Saisie du chunk
                                        // Augmenter le nombre de chunks
                                        int chunks = file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks");

                                        // Ajouter le chunk
                                        file.get().set("zones." + player.getUniqueId() + ".chunks." + (chunks + 1) + ".x", chunk.getX());
                                        file.get().set("zones." + player.getUniqueId() + ".chunks." + (chunks + 1) + ".z", chunk.getZ());

                                        file.get().set("zones." + player.getUniqueId() + ".number_of_chunks",
                                                chunks + 1);

                                        if (!file.get().getStringList("zones." + player.getUniqueId() + ".allowed").contains(player.getUniqueId()))
                                            file.get().getStringList("zones." + player.getUniqueId() + ".allowed").add(player.getUniqueId().toString());

                                        file.get().options().copyDefaults(true);
                                        file.save();

                                    }
                                } else {
                                    List<String> list = new ArrayList<>();
                                    list.add(player.getUniqueId().toString());

                                    file.get().set("zones." + player.getUniqueId() + ".number_of_chunks", 1);
                                    file.get().set("zones." + player.getUniqueId() + ".allowed", list);
                                    file.get().set("zones." + player.getUniqueId() + ".chunks.1.x", chunk.getX());
                                    file.get().set("zones." + player.getUniqueId() + ".chunks.1.z", chunk.getZ());

                                    file.get().options().copyDefaults(true);
                                    file.save();

                                }
                                return true;


                            }
                            else if (args[1].equalsIgnoreCase("unclaim")
                                        && (args.length == 2 || args.length == 3))
                            {
                                if (!file.get().contains("zones." + player.getUniqueId())
                                        || file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks") == 0)
                                    player.sendMessage("ERROR: Vous n'avez pas de zone à laisser de côté.");
                                else {
                                    int chunks = file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks");

                                    if (args.length == 2) {

                                        for (String key : Objects.requireNonNull(file.get().getConfigurationSection("zones." + player.getUniqueId() + ".chunks")).getKeys(false)) {
                                            if (file.get().getInt("zones." + player.getUniqueId() + ".chunks." + key + ".x") == player.getWorld().getChunkAt(player.getLocation()).getX()
                                                    && file.get().getInt("zones." + player.getUniqueId() + ".chunks." + key + ".z") == player.getWorld().getChunkAt(player.getLocation()).getZ()) {

                                                // Faire revenir tous les autres chunks
                                                for (int i = Integer.parseInt(key) + 1; i <= chunks; i++) {
                                                    file.get().set("zones." + player.getUniqueId() + ".chunks." + (char)((i - 1) + '0'),
                                                            file.get().get("zones." + player.getUniqueId() + ".chunks." + (char)(i + '0')));
                                                }

                                                file.get().set("zones." + player.getUniqueId() + ".chunks." + (char)(chunks + '0'),
                                                        null);

                                                file.get().set("zones." + player.getUniqueId() + ".number_of_chunks",
                                                        chunks - 1);

                                                file.save();
                                                return true;
                                            }
                                        }

                                        player.sendMessage("ERROR: Le chunk sous vos pieds n'est pas à vous.");

                                    } else if (args.length == 3
                                            && NumberUtils.isNumber(args[2])) {

                                        int id_zone = Integer.parseInt(args[2]);

                                        if (file.get().isConfigurationSection("zones." + player.getUniqueId() + ".chunks." + (char)(id_zone + '0'))) {
                                            file.get().set("zones." + player.getUniqueId() + ".chunks." + (char)(id_zone + '0'), null);

                                            // Faire revenir tous les autres chunks
                                            for (int i = id_zone + 1; i <= chunks; i++) {
                                                file.get().set("zones." + player.getUniqueId() + ".chunks." + (char)((i - 1) + '0'),
                                                        file.get().get("zones." + player.getUniqueId() + ".chunks." + (char)(i + '0')));
                                            }

                                            file.get().set("zones." + player.getUniqueId() + ".chunks." + (char)(chunks + '0'),
                                                    null);

                                            file.get().set("zones." + player.getUniqueId() + ".number_of_chunks",
                                                    chunks - 1);

                                            file.save();
                                        } else {
                                            player.sendMessage("ERROR: Vous n'avez pas de chunk avec cet identifiant.");
                                        }
                                    } else {
                                        player.sendMessage("ERROR: L'identifiant entré n'est pas un entier.");
                                    }
                                }

                                return true;
                            } else if (args[1].equalsIgnoreCase("abandon")
                                    && args.length == 2) {

                                if (file.get().isConfigurationSection("zones." + player.getUniqueId())
                                    && (file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks")) != 0) {
                                        file.get().set("zones." + player.getUniqueId() + ".number_of_chunks", 0);
                                        file.get().set("zones." + player.getUniqueId() + ".chunks", null);
                                        file.save();
                                } else player.sendMessage("ERROR: Vous n'avez pas de zones à abandonner");

                                return true;
                            } else if (args[1].equalsIgnoreCase("add")
                                    && args.length == 3) {

                                OfflinePlayer pl = Bukkit.getPlayer(args[2]);
                                if (pl != null && pl.hasPlayedBefore())
                                    if (!file.get().getStringList("zones." + player.getUniqueId() + ".allowed").contains(pl.getUniqueId())) {
                                        // DEFECTUEUX
                                        // file.get().getStringList("zones." + player.getUniqueId() + ".allowed").add(pl.getUniqueId().toString());

                                        List<String> l = file.get().getStringList("zones." + player.getUniqueId() + ".allowed");
                                        l.add(pl.getUniqueId().toString());
                                        file.get().set("zones." + player.getUniqueId() + ".allowed", l);
                                    } else player.sendMessage("ERROR: Le joueur est déjà autorisé sur votre zone.");
                                else player.sendMessage("ERROR: Le joueur n'a jamais été sur le serveur");

                                file.save();

                                return true;
                            } else if (args[1].equalsIgnoreCase("remove")
                                    && args.length == 3) {

                                OfflinePlayer pl = Bukkit.getPlayer(args[2]);

                                if (pl.getUniqueId() == player.getUniqueId()) {
                                    player.sendMessage("Wow, wow, wow... Tu es propriétaire !");
                                    return true;
                                }

                                if (pl != null && pl.hasPlayedBefore())
                                    if (file.get().getStringList("zones." + player.getUniqueId() + ".allowed").contains(pl.getUniqueId().toString())) {
                                        // DEFECTUEUX
                                        // file.get().getStringList("zones." + player.getUniqueId() + ".allowed").remove(pls);

                                        List<String> l = file.get().getStringList("zones." + player.getUniqueId() + ".allowed");
                                        l.remove(pl.getUniqueId().toString());
                                        file.get().set("zones." + player.getUniqueId() + ".allowed", l);

                                    } else player.sendMessage("ERROR: Le joueur n'est pas autorisé sur votre zone : " + pl.getUniqueId());
                                else player.sendMessage("ERROR: Le joueur n'a jamais été sur le serveur");

                                file.save();

                                return true;
                            } else if (args[1].equalsIgnoreCase("spawn")
                                    && (args.length == 2 || args.length == 3)) {

                                if (!file.get().isConfigurationSection("zones." + player.getUniqueId())
                                        || file.get().getInt("zones." + player.getUniqueId() + ".number_of_chunks") == 0) {
                                    player.sendMessage("ERROR: Vous n'avez pas de zones.");
                                    return true;
                                }

                                String id_zone = "1";
                                Chunk chunk;

                                if (args.length == 3) {
                                    if (!NumberUtils.isNumber(args[2])) {
                                        player.sendMessage("ERROR: L'identifiant entré n'est pas un entier.");
                                        return true;
                                    }
                                    id_zone = args[2];
                                }

                                chunk = Objects.requireNonNull(Bukkit.getServer().getPlayer(player.getUniqueId())).getWorld().getChunkAt(
                                        file.get().getInt("zones." + player.getUniqueId() + ".chunks." + id_zone + ".x"),
                                        file.get().getInt("zones." + player.getUniqueId() + ".chunks." + id_zone + ".z")
                                );

                                task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable()
                                        {
                                            int timer = 5;
                                            public void run()
                                            {
                                                player.sendMessage(ChatColor.GREEN + "PC[i] Téléportation dans " + timer + " secondes.");

                                                if (timer == 0) {
                                                    Location loc = new Location(player.getWorld(),
                                                            chunk.getX() << 4,
                                                            player.getWorld().getHighestBlockAt(chunk.getX() << 4, chunk.getZ() << 4).getY(),
                                                            chunk.getZ() << 4);
                                                    player.teleport(loc);
                                                    Bukkit.getServer().getScheduler().cancelTask(task);
                                                    return;
                                                } else timer--;
                                            }
                                        }, 0, 20);
                                return true;
                            } else usage(player);
                    } else usage(player);
                }
            } else sender.sendMessage("ERROR: You do not have the permission to use this command");
        }

        return true;
    }

    // TODO: Command description
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "Command Usage: " +
                ChatColor.AQUA + "/pci <subcommand> <options>\n\n" +
                ChatColor.GREEN + "\tzone claim" +
                ChatColor.GREEN + "\tzone unclaim {id_zone}");
    }
}
