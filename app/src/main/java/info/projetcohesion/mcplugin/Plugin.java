package info.projetcohesion.mcplugin;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("Hello world !");
    }

    @Override
    public void onDisable() {
        System.out.println("Goodbye world!");
    }
}
