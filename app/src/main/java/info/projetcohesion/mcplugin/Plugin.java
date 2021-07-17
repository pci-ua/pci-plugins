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
        System.out.println("Loading pci-plugins");
        try {
            Server.start();
        } catch (IOException e) {
            System.err.println("Failed to boot the integrated HTTP server !");
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        System.out.println("Unloading pci-plugins");
        Server.stop();
    }
}