package info.projetcohesion.mcplugin;

import info.projetcohesion.mcplugin.httpserver.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * The core of the plugin, which is loaded by the Bukkit/Spigot plugin loader
 */
@SuppressWarnings("unused") // This class is loaded by the plugin loader, and is in fact used at runtime
public class Plugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            // Check if ImageMagick is installed
            Process process = Runtime.getRuntime().exec("magick -version");
            process.waitFor();
            if(process.exitValue() == 0) { // If ImageMagick is not installed, the exit value will not be 0
                Server.start();
            } else {
                System.err.println("ImageMagick is not installed, is not on PATH or does not work.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to boot the integrated HTTP server !");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Server.stop();
    }
}