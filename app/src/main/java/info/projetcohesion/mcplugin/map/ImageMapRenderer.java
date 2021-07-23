package info.projetcohesion.mcplugin.map;

import info.projetcohesion.mcplugin.commands.MapArtCommand;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

public class ImageMapRenderer extends MapRenderer {
    private final String _id;
    private boolean _done = false;

    public ImageMapRenderer(String id) {
        this._id = id;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!_done) {
            map.setScale(MapView.Scale.NORMAL);

            BufferedImage img = (BufferedImage) MapArtCommand.getImageManager().get(_id);
            canvas.drawImage(0, 0, img);

            map.setLocked(true);
            map.setTrackingPosition(false);
            map.setUnlimitedTracking(false);

            _done = true;
        }
    }
}
