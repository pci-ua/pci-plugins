package info.projetcohesion.mcplugin.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * An helper for using the ImageMagick CLI
 */
public class ImageMagick {

    /**
     * Run a command through ImageMagick CLI.
     * @param cmd The command, without the <code>"magick "</code> prefix.
     * @param data The data to send to ImageMagick through the standard input.
     * @return An Image object
     * @throws IOException If an I/O error occurs
     * @see Runtime#exec(String)
     * @see ImageIO#read(InputStream)
     */
    public static Image command(String cmd, byte[] data) throws IOException {
        cmd = "magick " + cmd;

        Process im = Runtime.getRuntime().exec(cmd);
        im.getOutputStream().write(data);
        im.getOutputStream().flush();
        im.getOutputStream().close();

        return ImageIO.read(im.getInputStream());
    }

    /**
     * Check for a working ImageMagick installation.
     * @return <code>true</code> if ImageMagick seems to be working, <code>false</code> otherwise.
     */
    public static boolean isWorking() {
        try {
            Process process = Runtime.getRuntime().exec("magick -version");
            process.waitFor();
            return process.exitValue() == 0; // If ImageMagick is working, this will be true
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}