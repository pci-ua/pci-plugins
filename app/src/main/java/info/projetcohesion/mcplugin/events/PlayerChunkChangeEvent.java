package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.ScoreboardUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

/**
 * PlayerChunkChangeEvent.java
 * <p>
 * Implements Listener.java.
 * Used to control the events after the player changes chunk.
 *
 * @author Jack Hogg
 */
public class PlayerChunkChangeEvent implements Listener {

    private final FileUtils file = new FileUtils("zones");

    /**
     * Handling of the player changing chunk
     *
     * @param event Event handled
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBuild(PlayerMoveEvent event) {
        // Modifier le scoreboard
        if (!event.getFrom().getChunk().equals(Objects.requireNonNull(event.getTo()).getChunk()))
            new ScoreboardUtils(event.getPlayer()).setPlayerScoreboard(event.getPlayer());

    }

    /**
     * Handling of the player placing a block
     *
     * @param event Event handled
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBuild(BlockPlaceEvent event) {
        Block block = event.getBlock();

        // Vérifier que le chunk voulu n'a pas déjà été saisi par un joueur (dont lui)
        if (file.get().getConfigurationSection("zones") != null)
            for (String local : Objects.requireNonNull(file.get().getConfigurationSection("zones")).getKeys(false))
                for (int i = 1; i <= file.get().getInt("zones." + local + ".number_of_chunks"); i++)
                    if ((block.getChunk().getX()) == file.get().getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x")
                            && (block.getChunk().getZ()) == file.get().getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z")) {

                        if (!local.equals(event.getPlayer().getUniqueId().toString())
                                && !file.get().getStringList("zones." + local + ".allowed")
                                .contains(event.getPlayer().getUniqueId().toString())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "ERROR: Vous n'êtes pas autorisé à construire ici.");
                        }

                        return;

                    }
    }

    /**
     * Handling of the player breaking a block
     *
     * @param event Event handled
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Vérifier que le chunk voulu n'a pas déjà été saisi par un joueur (dont lui)
        if (file.get().getConfigurationSection("zones") != null)
            for (String local : Objects.requireNonNull(file.get().getConfigurationSection("zones")).getKeys(false))
                for (int i = 1; i <= file.get().getInt("zones." + local + ".number_of_chunks"); i++)
                    if ((block.getChunk().getX()) == file.get().getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x")
                            && (block.getChunk().getZ()) == file.get().getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z")) {

                        if (!local.equals(event.getPlayer().getUniqueId().toString())
                                && !file.get().getStringList("zones." + local + ".allowed")
                                .contains(event.getPlayer().getUniqueId().toString())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "ERROR: Vous n'êtes pas autorisé à construire ici.");
                        }

                        return;

                    }
    }
}
