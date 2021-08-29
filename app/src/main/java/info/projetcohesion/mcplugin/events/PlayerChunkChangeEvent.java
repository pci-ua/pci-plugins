package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.ZoneChunkData;
import info.projetcohesion.mcplugin.ZoneData;
import info.projetcohesion.mcplugin.utils.FileUtils;
import info.projetcohesion.mcplugin.utils.ScoreboardUtils;
import info.projetcohesion.mcplugin.utils.ZoneUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
    public void onMove(PlayerMoveEvent event) {
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

        ZoneData zones = ZoneUtils.getZones().get(event.getPlayer().getUniqueId().toString());

        if (zones != null)
            for (ZoneChunkData data : ZoneUtils.getAllChunks())
                if ((block.getChunk().getX()) == data.getX()
                        && (block.getChunk().getZ()) == data.getZ()) {

                    // Zone par défaut
                    if (data.getCategory().equals("z_def")) {
                        // Le joueur "propriétaire" est aussi dans la liste d'admis par défault
                        if (!zones.getAllowed().contains(event.getPlayer().getUniqueId().toString())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "ERROR: Vous n'êtes pas autorisé à construire ici.");
                        }
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

        ZoneData zones = ZoneUtils.getZones().get(event.getPlayer().getUniqueId().toString());

        if (zones != null)
            for (ZoneChunkData data : ZoneUtils.getAllChunks())
                if ((block.getChunk().getX()) == data.getX()
                        && (block.getChunk().getZ()) == data.getZ()) {

                    // Zone par défaut
                    if (data.getCategory().equals("z_def")
                            || (data.getCategory().equals("z_pers")
                            && event.getBlock().getType() == Material.CHEST)) {
                        if (!zones.getAllowed()
                                .contains(event.getPlayer().getUniqueId().toString())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "ERROR: Vous n'êtes pas autorisé à construire ici.");
                        }
                    }

                    return;

                }
    }

    /**
     * Handling of the player interacting with a chest
     *
     * @param event Event handled
     */
    @EventHandler
    public void onChestOpen(PlayerInteractEvent event) {

        Block block = event.getClickedBlock();

        ZoneData zones = ZoneUtils.getZones().get(event.getPlayer().getUniqueId().toString());

        if (zones != null
                && block != null)
            for (ZoneChunkData data : ZoneUtils.getAllChunks())
                if ((block.getChunk().getX()) == data.getX()
                        && (block.getChunk().getZ()) == data.getZ()) {

                    if (block.getType() == Material.CHEST
                            && data.getCategory().equals("z_pers")) {
                        if (!zones.getAllowed()
                                .contains(event.getPlayer().getUniqueId().toString())) {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage(ChatColor.RED + "ERROR: Vous n'êtes pas autorisé à ouvrir ce coffre.");
                        }
                    }

                    return;
                }
    }
}
