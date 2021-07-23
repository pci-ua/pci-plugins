package info.projetcohesion.mcplugin;

import info.projetcohesion.mcplugin.events.MapInitEvent;
import info.projetcohesion.mcplugin.events.PlayerChunkChangeEvent;
import info.projetcohesion.mcplugin.events.PlayerServerJoinEvent;
import info.projetcohesion.mcplugin.httpserver.Server;
import info.projetcohesion.mcplugin.utils.CommandManager;
import info.projetcohesion.mcplugin.utils.ImageMagick;
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

        if(ImageMagick.isWorking()) { // The HTTP server is useless without ImageMagick, so don't start it without it.
            try {
                Server.start();
            } catch (IOException e) {
                System.err.println("Failed to boot the integrated HTTP server !");
                e.printStackTrace();
            }
        } else {
            System.err.println("ImageMagick is not working. Check your PATH for a working magick binary.");
        }
    }

    @Override
    public void onDisable() {
        Server.stop();
    }

    public void registerCommands() {
        this.getCommand("pci").setExecutor(new CommandManager());
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChunkChangeEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerServerJoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MapInitEvent(), this);
    }

    public static Plugin getPlugin() {
        return s_plugin;
    }
}
