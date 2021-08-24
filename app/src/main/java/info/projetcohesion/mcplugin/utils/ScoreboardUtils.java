package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.ZoneChunkData;
import info.projetcohesion.mcplugin.ZoneData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ScoreboardUtils.java
 * <p>
 * Manages the Scoreboard.
 *
 * @author Jack Hogg
 */
public class ScoreboardUtils {

    private final Scoreboard _scoreboard;

    /**
     * ScoreboardUtils constructor
     *
     * @param player Player
     */
    public ScoreboardUtils(Player player) {

        EcoUtils eco = new EcoUtils();
        FileUtils f_man = new FileUtils("zones");
        FileConfiguration file = f_man.get();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        assert manager != null;
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Title",
                "dummy",
                net.md_5.bungee.api.ChatColor.of("#007f7f") + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500") + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "]");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore("");
        Score s1 = objective.getScore(ChatColor.GOLD + "Solde : " + ChatColor.GREEN + eco.getAccountBalance(player) + " \u272a");
        Score s2 = objective.getScore(" ");

        Chunk chunk = player.getWorld().getChunkAt(player.getLocation());
        Score s3 = (objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.RED + "Warzone"));

        ZoneData zones = ZoneUtils.getZones().get(player.getUniqueId().toString());

        if (zones != null)
            for (ZoneChunkData data : ZoneUtils.getAllChunks())
                if (chunk.getX() == data.getX()
                        && chunk.getZ() == data.getZ()) {

                    if (zones.getChunks().contains(data))
                        s3 = objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.GREEN + player.getDisplayName());
                    else
                        s3 = objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.RED + "Territoire ennemi");

                }

        Score s4 = objective.getScore("  ");
        Score s5 = objective.getScore(ChatColor.AQUA + "Pseudo : " + ChatColor.GREEN + player.getDisplayName());

        score.setScore(6);
        s1.setScore(5);
        s2.setScore(4);
        s3.setScore(3);
        s4.setScore(2);
        s5.setScore(1);

        this._scoreboard = scoreboard;
    }

    /**
     * Set a player's scoreboard
     *
     * @param player Player
     */
    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(this._scoreboard);
    }

}
