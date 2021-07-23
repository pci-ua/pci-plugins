package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.map.ImageMapRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapInitEvent implements Listener {
    private static String s_wipId;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMapInit(MapInitializeEvent event) {
        if(s_wipId != null) { // Normal map if s_wipId == null
            MapView map = event.getMap();

            // We don't want other renderers on the same map
            for(MapRenderer r : map.getRenderers()) {
                map.removeRenderer(r);
            }

            map.addRenderer(new ImageMapRenderer(s_wipId));

            s_wipId = null; // Set the WIP ID to null to indicate that the next map should be a vanilla one, except if a new command is issued in between
        }
    }

    /**
     * Set the ID for the next generated map art.
     * @param s_wipId The ID to use.
     */
    public static void setWipId(String s_wipId) {
        MapInitEvent.s_wipId = s_wipId;
    }
}
