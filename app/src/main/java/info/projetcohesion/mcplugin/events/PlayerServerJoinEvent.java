package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.utils.EcoUtils;
import info.projetcohesion.mcplugin.utils.ScoreboardUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerServerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        EcoUtils file = new EcoUtils();

        if (!file.hasAccount(event.getPlayer())) {
            file.newAccount(event.getPlayer());
            file.save();
        }

        ScoreboardUtils util = new ScoreboardUtils(event.getPlayer());
        util.setPlayerScoreboard(event.getPlayer());
    }

}
