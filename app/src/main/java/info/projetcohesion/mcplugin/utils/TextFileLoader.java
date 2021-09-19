package info.projetcohesion.mcplugin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Helper to load text files from the filesystem
 */
public abstract class TextFileLoader {
    /**
     * Load a text file
     * @param f The file to load
     * @return The text of the file
     * @throws IOException If an I/O errors occurs
     */
    public static String load(File f) throws IOException {
        String out;

        FileInputStream fis = new FileInputStream(f);

        out = new String(fis.readAllBytes());

        fis.close();
        return out;
    }
}
