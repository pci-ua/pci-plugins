package info.projetcohesion.mcplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        if (command.l)

        if (sender instanceof Player) {

        } else
            sender.sendMessage("ERROR: You do not have the permission to use this command");

        return false;
    }
}
