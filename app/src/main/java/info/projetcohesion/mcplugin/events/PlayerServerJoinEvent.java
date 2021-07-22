package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.managers.EcoManager;
import info.projetcohesion.mcplugin.managers.FileManager;
import info.projetcohesion.mcplugin.utils.ScoreboardUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerServerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        EcoManager file = new EcoManager();

        if (!file.hasAccount(event.getPlayer())) {
            file.newAccount(event.getPlayer());
            file.save();
        }

        ScoreboardUtils util = new ScoreboardUtils(event.getPlayer());
        util.setPlayerScoreboard(event.getPlayer());
    }

}
