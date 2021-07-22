package info.projetcohesion.mcplugin;

import org.bukkit.entity.Player;

import java.util.Map;


public interface SubCommand {

    String getName();
    String getDescription();
    Map<String, String> getUsage();
    Map<String, String> getPermissions(); //TODO: Revoir la façon de gérer les permissions
    void commandUsage(Player player, String args[]);

}
