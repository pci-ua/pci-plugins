package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.SubCommand;
import info.projetcohesion.mcplugin.managers.EcoManager;
import info.projetcohesion.mcplugin.managers.FileManager;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class EcoCommand implements SubCommand {

    @Override
    public String getName() {
        return "eco";
    }

    @Override
    public String getDescription() {
        return "Gestion de la monnaie";
    }

    @Override
    public Map<String, String> getUsage() {
        Map<String, String> list = new HashMap<>();
        list.put("/pci eco balance", "Montant sur le compte");
        list.put("/pci eco <add | remove> <player> <amount>", "Ajouter ou retirer de la monnaie");
        list.put("/pci eco send <player> <amount>", "Envoyer de l'argent");
        list.put("/pci eco ask <player> [amount]", "Demander de l'argent");

        return list;
    }

    @Override
    public Map<String, String> getPermissions() {
        Map<String, String> list = new HashMap<>();
        list.put("/pci eco balance", "");
        list.put("/pci eco <add | remove> <player> <amount>", "pci.eco.admin");
        list.put("/pci eco send <player> <amount>", "");
        list.put("/pci eco ask <player> [amount]", "");

        return list;
    }

    @Override
    public void commandUsage(Player player, String[] args) {

        FileManager f_man = new FileManager("money");
        EcoManager eco = new EcoManager();
        FileConfiguration file = f_man.get();

        if (args.length == 1
                || (args.length == 2 && args[1].equalsIgnoreCase("balance"))) {
            player.sendMessage(ChatColor.GREEN + "Vous avez un total de : " +
                    ChatColor.GOLD + file.getInt("accounts." + player.getUniqueId().toString()) + " \u272a");

            return;
        }

        if (args[1].equalsIgnoreCase("add")
                && args.length == 4
                && player.hasPermission("pci.eco.admin")) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);
            if (pl != null
                    && pl.hasPlayedBefore())
                if (NumberUtils.isDigits(args[3])) {
                    eco.setAccountBalance(pl, eco.getAccountBalance(pl) + Integer.parseInt(args[3]));
                    f_man.save();
                    file.options().copyDefaults(true);
                } else player.sendMessage(ChatColor.RED + "ERROR: Le montant est invalide.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est inaccessible");

        } else if (args[1].equalsIgnoreCase("remove")
                && args.length == 4
                && player.hasPermission("pci.eco.admin")) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);
            if (pl != null
                    && pl.hasPlayedBefore())
                if (NumberUtils.isDigits(args[3]))
                    if (eco.getAccountBalance(pl) - Integer.parseInt(args[3]) >= 0) {
                        eco.setAccountBalance(pl, eco.getAccountBalance(pl) - Integer.parseInt(args[3]));
                        f_man.save();
                        file.options().copyDefaults(true);
                    } else eco.setAccountBalance(pl, 0);
                else player.sendMessage(ChatColor.RED + "ERROR: Le montant est invalide.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est inaccessible");


        } else if (args[1].equalsIgnoreCase("send")) {

            OfflinePlayer pl = Bukkit.getPlayer(args[2]);
            if (pl != null
                    && pl.hasPlayedBefore())
                if (NumberUtils.isDigits(args[3])) {
                    eco.transfer(player, pl, Integer.parseInt(args[3]));
                    f_man.save();
                    file.options().copyDefaults(true);
                } else player.sendMessage(ChatColor.RED + "ERROR: Le montant est invalide.");
            else player.sendMessage(ChatColor.RED + "ERROR: Le joueur est inaccessible");


        } else if (args[1].equalsIgnoreCase("ask")
                && (args.length == 3 || args.length == 4)) {

            Player pl = Bukkit.getPlayer(args[2]);

            if (pl == null
                    || !pl.hasPlayedBefore()
                    || !pl.isOnline()) {
                player.sendMessage(ChatColor.RED + "ERROR: Le joueur n'est pas disponible ou est inexistant.");
                return;
            }

            // Une demande sans montant
            if (args.length == 3) {
                pl.sendMessage(ChatColor.GREEN + "Bonjour, " + player.getName() + " vous demande si vous accepteriez de lui donner de l'argent ("
                        + ChatColor.GOLD + "/pci eco send " + player.getName() + " <amount>" + ChatColor.GREEN + ").");
            } else { // Une demande avec montant
                if (!NumberUtils.isDigits(args[3])) {
                    player.sendMessage(ChatColor.RED + "ERROR: Le montant est invalide.");
                    return;
                }

                pl.sendMessage(ChatColor.GREEN + "Bonjour, " + player.getName() + " vous demande si vous accepteriez de lui donner " + Integer.parseInt(args[3]) + " ("
                        + ChatColor.GOLD + "/pci eco send " + player.getName() + " " + Integer.parseInt(args[3]) + ChatColor.GREEN + ").");
            }

            pl.sendMessage(ChatColor.GREEN + "Pour rappel, voici votre solde : " + ChatColor.GOLD + eco.getAccountBalance(pl) + " \u272a");
            pl.sendMessage(ChatColor.GREEN + "/!\\ Si le joueur abuse de cette demande, ne pas hésiter à contacter la modération.");

        } else player.sendMessage("ERROR: Impossible d'utiliser cette commande.");

        // Ne semble pas sauvegarder correectement si mis ici.
        // f_man.save();
        // file.options().copyDefaults(true);

    }
}
