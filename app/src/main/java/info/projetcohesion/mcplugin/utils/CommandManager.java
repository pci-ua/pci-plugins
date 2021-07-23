package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.SubCommand;
import info.projetcohesion.mcplugin.commands.EcoCommand;
import info.projetcohesion.mcplugin.commands.ShopCommand;
import info.projetcohesion.mcplugin.commands.ZoneCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new ZoneCommand());
        subcommands.add(new EcoCommand());
        subcommands.add(new ShopCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player p = (Player) sender;

            if (args.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        getSubcommands().get(i).commandUsage(p, args);
                    }
                }
            }else if(args.length == 0){
                p.sendMessage("---------------- " + net.md_5.bungee.api.ChatColor.of("#007f7f") + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500") + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "]" + ChatColor.WHITE + " ----------------");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage("=> " + ChatColor.AQUA + getSubcommands().get(i).getDescription());
                    p.sendMessage(" ");
                    for (Map.Entry<String, String> entry : getSubcommands().get(i).getUsage().entrySet()) {
                        String perm = getSubcommands().get(i).getPermissions().get(entry.getKey());

                        if (perm == ""
                                || p.hasPermission(perm))
                            p.sendMessage(net.md_5.bungee.api.ChatColor.of("#007f7f") + entry.getKey() + ChatColor.WHITE + " - " + net.md_5.bungee.api.ChatColor.of("#ffa500") + entry.getValue());
                    }
                    p.sendMessage(" ");
                }
                p.sendMessage("---------------------------------------");
            }

        }

        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

}
