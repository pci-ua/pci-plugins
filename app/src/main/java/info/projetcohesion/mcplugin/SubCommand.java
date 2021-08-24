package info.projetcohesion.mcplugin;

import org.bukkit.entity.Player;

import java.util.Map;

/**
 * SubCommand.java
 * Interface for the sub-commands
 */
public interface SubCommand {

    /** Get the sub-command name
     * @return the sub-command name
     */
    String getName();

    /** Get the sub-command description
     * @return the sub-command description
     */
    String getDescription();

    /** Get the different usages
     * @return the list of the sub-command's different usages
     */
    Map<String, String> getUsage();

    /** Get the different permissions
     * @return the list of the sub-command's different permissions
     */
    Map<String, String> getPermissions();

    /**
     * Sub-command's usage
     * @param player Player using the command
     * @param args Sub-command arguments
     */
    void commandUsage(Player player, String args[]);
}
