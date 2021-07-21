package info.projetcohesion.mcplugin;

import info.projetcohesion.mcplugin.commands.MainCommand;
import info.projetcohesion.mcplugin.events.PlayerChunkChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

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

    public void registerCommands() {
        this.getCommand("pci").setExecutor(new MainCommand());
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChunkChangeEvent(), this);
    }

    public static Plugin getPlugin() {
        return _plugin;
    }
}
