package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.ZoneData;
import info.projetcohesion.mcplugin.utils.EcoUtils;
import info.projetcohesion.mcplugin.utils.ScoreboardUtils;
import info.projetcohesion.mcplugin.utils.ZoneUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * PlayerServerJoinEvent.java
 * <p>
 * Implements Listener.java.
 * Used to control the events after the player joins the server.
 *
 * @author Jack Hogg
 */
public class PlayerServerJoinEvent implements Listener {

    /**
     * Handling of the player joining the server
     *
     * @param event Event handled
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        EcoUtils file = new EcoUtils();

        if (ZoneUtils.getPlayerData(event.getPlayer().getUniqueId().toString()) == null) {
            ZoneUtils.newData(event.getPlayer().getUniqueId().toString());
        }

        if (!file.hasAccount(event.getPlayer())) {
            file.newAccount(event.getPlayer());
            file.save();
        }

        ScoreboardUtils util = new ScoreboardUtils(event.getPlayer());
        util.setPlayerScoreboard(event.getPlayer());
    }

}
