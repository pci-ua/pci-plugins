package info.projetcohesion.mcplugin;

import info.projetcohesion.mcplugin.events.ChunkDamageEvent;
import info.projetcohesion.mcplugin.events.FancyInventoryEvent;
import info.projetcohesion.mcplugin.events.PlayerChunkChangeEvent;
import info.projetcohesion.mcplugin.events.PlayerServerJoinEvent;
import info.projetcohesion.mcplugin.utils.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin.java
 * <p>
 * Manages the main class.
 */
public class Plugin extends JavaPlugin {

    private static Plugin _plugin;

    @Override
    public void onEnable() {
        _plugin = this;

        registerCommands();
        registerEvents();

        System.out.println("Hello world !");
    }

    @Override
    public void onDisable() {
        System.out.println("Goodbye world!");
    }

    /**
     * Register the commands.
     */
    public void registerCommands() {
        this.getCommand("pci").setExecutor(new CommandManager());
    }

    /**
     * Register the events.
     */
    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChunkChangeEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerServerJoinEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new FancyInventoryEvent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChunkDamageEvent(), this);
    }

    /**
     * Gets the plugin's instance.
     *
     * @return the instance
     */
    public static Plugin getPlugin() {
        return _plugin;
    }
}
