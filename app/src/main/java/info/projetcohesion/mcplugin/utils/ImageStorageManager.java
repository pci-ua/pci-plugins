package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.Random;

/**
 * Store images with random IDs for future reference.
 */
public class ImageStorageManager implements Serializable {
    private final File _imgDir = new File(Plugin.getPlugin().getDataFolder(), "images/");

    public ImageStorageManager() {
        _imgDir.mkdirs();
    }

    /**
     * Get an ID that is currently unused.
     * @return An ID that can be used for image storage
     */
    private String getFreeID() {
        String id;

        Random r = new Random();

        while (true) {
            id = Integer.toString(r.nextInt(9999));

            if (!(new File(_imgDir, id).exists())) {
                return id;
            }
        }
    }

    /**
     * Get the image with the associated ID
     * @param id The image's ID
     * @return The corresponding image
     */
    public Image get(String id) throws IOException {
        File file = new File(_imgDir, id);
        if (file.exists() && file.isFile()) {
            return ImageIO.read(file);
        } else {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
    }

    /**
     * Convert and resize an image in a byte array format and save it.
     * @param data The image in a byte array
     * @return The ID of the added image
     * @throws IOException If an I/O error occurs while processing the data
     * @see ImageMagick
     * @see ImageStorageManager#getFreeID()
     */
    public String convertAndAdd(byte[] data) throws IOException {
        String id = this.getFreeID();

        byte[] bmp = ImageMagick.command("convert - -resize 128x128! BMP:-", data);

        File out = new File(_imgDir, id);
        out.createNewFile();

        FileOutputStream fos = new FileOutputStream(out);
        fos.write(bmp);
        fos.flush();
        fos.close();

        return id;
    }

    /**
     * Remove the image with the associated ID
     * @param id The ID to remove
     */
    public void removeID(String id) {
        File file = new File(_imgDir, id);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Check if this ID is used
     * @param id The ID to check against
     * @return If the ID is already used or not
     */
    public boolean exists(String id) {
        return new File(_imgDir, id).exists();
    }

    /**
     * Get the number of images stored.
     * @return The number of images stored
     */
    public int size() {
        return _imgDir.listFiles().length;
    }
}
