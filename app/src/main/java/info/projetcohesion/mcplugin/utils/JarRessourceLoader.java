package info.projetcohesion.mcplugin.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper to load a text file from the JAR archive.
 */
public abstract class JarRessourceLoader {

    /**
     * Load a text file from the JAR archive.
     * @param uri The URI to load inside the JAR archive
     * @return The text file content
     * @throws IOException If the file can't be found
     */
    public static String load(String uri) throws IOException {
        String out;

        InputStream is = JarRessourceLoader.class.getResourceAsStream(uri);
        assert is != null;

        out = new String(is.readAllBytes());
        is.close();

        return out;
    }
}
