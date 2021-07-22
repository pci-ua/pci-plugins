package info.projetcohesion.mcplugin.commands;

import info.projetcohesion.mcplugin.managers.ImageStorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class MapArtCommand implements CommandExecutor {
    private static ImageStorageManager s_storage = new ImageStorageManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("mapart")) {
            if(args.length == 0) {
                sender.sendMessage("Il y a actuellement " + s_storage.size() + " image(s) en attente.");
            } else if(args.length == 1 && args[0].equalsIgnoreCase("clear") && sender instanceof ConsoleCommandSender) { // "/mapart clear" only from the console
                s_storage = new ImageStorageManager();
                sender.sendMessage("Cleared.");
            } // TODO implement the rest of the command
            return true;
        }
        return false;
    }

    /**
     * Get the ImageStorageManager used to manage images within this command.
     * @return The ImageStorageManager used by the command.
     * @see ImageStorageManager
     */
    public static ImageStorageManager getImageManager() {
        return s_storage;
    }
}
