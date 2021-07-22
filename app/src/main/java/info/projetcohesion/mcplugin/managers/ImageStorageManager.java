package info.projetcohesion.mcplugin.managers;

import info.projetcohesion.mcplugin.utils.ImageMagick;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

/**
 * Store images with random IDs for future reference.
 */
public class ImageStorageManager {
    private final HashMap<String, Image> _storage = new HashMap<>();

    /**
     * Get an ID that is currently unused.
     * @return An ID that can be used for image storage
     */
    private String getFreeID() {
        String id;

        Random r = new Random();

        while (true) {
            id = Integer.toString(r.nextInt(9999));

            if (!_storage.containsKey(id)) {
                return id;
            }
        }
    }

    /**
     * Get the image with the associated ID
     * @param id The image's ID
     * @return The corresponding image
     */
    public Image get(String id) {
        return _storage.get(id);
    }

    /**
     * Convert and resize an image in a byte array format and save it.
     * @param data The image in a byte array
     * @return The ID of the added image
     * @throws IOException If an I/O error occurs while processing the data
     * @see ImageMagick
     * @see ImageStorageManager#add(Image)
     * @see ImageStorageManager#getFreeID()
     */
    public String convertAndAdd(byte[] data) throws IOException {
        return this.add(ImageMagick.command("convert - -resize 128x128! BMP:-", data));
    }

    /**
     * Add an image to this <code>ImageStorageManager</code>
     * @param img The image to add
     * @return The ID of the added image
     */
    public String add(Image img) {
        String id = this.getFreeID();
        _storage.put(id, img);

        return id;
    }

    /**
     * Remove the image with the associated ID
     * @param id The ID to remove
     */
    public void removeID(String id) {
        _storage.remove(id);
    }

    /**
     * Check if this ID is used
     * @param id The ID to check against
     * @return If the ID is already used or not
     * @see HashMap#containsKey(Object) 
     */
    public boolean exists(String id) {
        return _storage.containsKey(id);
    }

    /**
     * Get the number of images stored.
     * @return The number of images stored
     * @see HashMap#size()
     */
    public int size() {
        return _storage.size();
    }
}
