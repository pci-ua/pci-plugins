package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.managers.EcoManager;
import info.projetcohesion.mcplugin.managers.FileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerServerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Vérifier que le joueur a bien un compte lors de son arrivé sur le serveur
        EcoManager file = new EcoManager();

        if (!file.hasAccount(event.getPlayer()))
            file.newAccount(event.getPlayer());
    }

}
