package info.projetcohesion.mcplugin;

import info.projetcohesion.mcplugin.commands.MainCommand;
import info.projetcohesion.mcplugin.events.PlayerChunkChangeEvent;
import info.projetcohesion.mcplugin.httpserver.Server;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * The core of the plugin, which is loaded by the Bukkit/Spigot plugin loader
 */
@SuppressWarnings("unused") // This class is loaded by the plugin loader, and is in fact used at runtime
public class Plugin extends JavaPlugin {

    private static Plugin s_plugin;

    @Override
    public void onEnable() {
        s_plugin = this;

        registerCommands();
        registerEvents();

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

    public void registerCommands() {
        this.getCommand("pci").setExecutor(new MainCommand());
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChunkChangeEvent(), this);
    }

    public static Plugin getPlugin() {
        return s_plugin;
    }
}
