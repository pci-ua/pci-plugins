package info.projetcohesion.mcplugin.map;

import info.projetcohesion.mcplugin.commands.MapArtCommand;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer {
    private String _id;

    public ImageMapRenderer(String id) {
        this._id = id;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0, 0, MapArtCommand.getImageManager().get(_id));
    }
}
