package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.Plugin;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * An helper for the ImageMagick CLI
 */
public class ImageMagick {

    /**
     * Run a command through the ImageMagick CLI.
     * @param cmd The command, without the <code>"magick "</code> prefix.
     * @param data The data to send to ImageMagick through the standard input.
     * @return Image data in a byte array format
     * @throws IOException If an I/O error occurs
     * @throws ImageMagickException If the ImageMagick process does not return a 0 exit value
     * @see Runtime#exec(String)
     * @see ImageIO#read(InputStream)
     */
    public static byte[] command(String cmd, byte[] data) throws IOException, ImageMagickException {
        cmd = "magick " + cmd;

        Process im = Runtime.getRuntime().exec(cmd);
        im.getOutputStream().write(data);
        im.getOutputStream().flush();
        im.getOutputStream().close();

        byte[] out = im.getInputStream().readAllBytes();
        im.getInputStream().close();

		try {
			if(im.waitFor() != 0) {
				Plugin.getPlugin().getLogger().severe("ImageMagick exit code is not zero !");
				throw new ImageMagickException();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new ImageMagickException();
		}

		return out;
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
	
    /**
     * When something went wrong with ImageMagick
     */
    public static class ImageMagickException extends Exception {
        /**
         * Build a new ImageMagickException.
         * @see Exception#Exception()
         */
        public ImageMagickException() {
            super();
        }
    }
}
