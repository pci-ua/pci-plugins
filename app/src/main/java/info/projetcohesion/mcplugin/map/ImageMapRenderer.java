package info.projetcohesion.mcplugin.map;

import info.projetcohesion.mcplugin.commands.MapArtCommand;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A map renderer, capable of rendering images.
 * @see MapRenderer
 */
public class ImageMapRenderer extends MapRenderer {
    private final String _id;
    private boolean _done = false;

    /**
     * Create a ImageMapRenderer object linked to an image ID
     * @param id The image ID to use
     */
    public ImageMapRenderer(String id) {
        this._id = id;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!_done) { // Draw only once
            map.setScale(MapView.Scale.NORMAL);

            try {
                BufferedImage img = (BufferedImage) MapArtCommand.getImageManager().get(_id);
                canvas.drawImage(0, 0, img);
            } catch (IOException e) {
                player.sendMessage("Failed to generate the image !");
                e.printStackTrace();
            }

            map.setLocked(true);
            map.setTrackingPosition(false);
            map.setUnlimitedTracking(false);

            _done = true;
        }
    }
}
