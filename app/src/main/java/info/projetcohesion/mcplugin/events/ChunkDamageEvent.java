package info.projetcohesion.mcplugin.events;

import info.projetcohesion.mcplugin.utils.FileUtils;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.ArrayList;
import java.util.Objects;

public class ChunkDamageEvent implements Listener {

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player player) {
            if (event.getDamager() instanceof LivingEntity
                    && effectActivated(player, "pve")) {
                player.sendMessage("PVE");
                event.setCancelled(true);
            }

            else if (event.getDamager() instanceof Player
                    && effectActivated(player, "pvp")) {
                player.sendMessage("pvp");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamageByNature(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player
                && !event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            if (getBlacklist().contains(event.getCause())
                    && effectActivated(player, "nat")) {
                player.sendMessage("NATURE IS COMING");
                event.setCancelled(true);
            }
        }
    }

    public ArrayList<DamageCause> getBlacklist() {
        ArrayList<DamageCause> liste = new ArrayList<>();

        liste.add(DamageCause.BLOCK_EXPLOSION);
        liste.add(DamageCause.FALL);
        liste.add(DamageCause.FIRE);
        liste.add(DamageCause.FIRE_TICK);
        liste.add(DamageCause.FREEZE);
        liste.add(DamageCause.HOT_FLOOR);
        liste.add(DamageCause.LAVA);
        liste.add(DamageCause.LIGHTNING);
        liste.add(DamageCause.DROWNING);
        liste.add(DamageCause.POISON);

        return liste;
    }

    public boolean effectActivated(Player player, String id) {
        FileUtils f_man = new FileUtils("zones");
        FileConfiguration file = f_man.get();

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());

        if (file.getConfigurationSection("zones") != null)
            for (String local : Objects.requireNonNull(file.getConfigurationSection("zones")).getKeys(false))
                for (int i = 1; i <= file.getInt("zones." + local + ".number_of_chunks"); i++)
                    if (chunk.getX() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x")
                            && chunk.getZ() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z")) {
                        return (file.getStringList("zones." + local + ".effects").contains(id));

                    }

        return false;
    }

}
