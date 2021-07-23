package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.map.ImageMapRenderer;
import info.projetcohesion.mcplugin.utils.FileUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapInitEvent implements Listener {
    private static String s_wipId;
    private FileUtils idMap = new FileUtils("maps-id");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMapInit(MapInitializeEvent event) {
        MapView map = event.getMap();
        if(idMap.get().getString(Integer.toString(map.getId())) != null) {
            this.render(map, idMap.get().getString(Integer.toString(map.getId())));
        } else if(s_wipId != null) { // Normal map if s_wipId == null
            this.render(map, s_wipId);
            idMap.get().set(Integer.toString(map.getId()), s_wipId);

            s_wipId = null; // Set the WIP ID to null to indicate that the next map should be a vanilla one, except if a new command is issued in between

            idMap.save();
        }
    }

    private void render(MapView map, String id) {
        // We don't want other renderers on the same map
        for(MapRenderer r : map.getRenderers()) {
            map.removeRenderer(r);
        }

        map.addRenderer(new ImageMapRenderer(id));
    }

    /**
     * Set the ID for the next generated map art.
     * @param wipId The ID to use.
     */
    public static void setWipId(String wipId) {
        MapInitEvent.s_wipId = wipId;
    }
}
