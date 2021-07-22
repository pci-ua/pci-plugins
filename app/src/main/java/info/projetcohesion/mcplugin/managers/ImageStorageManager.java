package info.projetcohesion.mcplugin.managers;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Store images with random IDs for future reference.
 */
public class ImageStorageManager {
    private final HashMap<String, Image> storage = new HashMap<>();

    private String getFreeID() {
        String id;

        Random r = new Random();

        while (true) {
            id = Integer.toString(r.nextInt());

            if (!storage.containsKey(id)) {
                return id;
            }
        }
    }

    public Image get(String id) {
        return storage.get(id);
    }

    public String add(byte[] data) {
        // TODO implement adding an image as a byte array

        return add((Image) null);
    }

    public String add(Image img) {
        String id = getFreeID();
        storage.put(id, img);

        return id;
    }

    public void removeID(String id) {
        storage.remove(id);
    }
}
