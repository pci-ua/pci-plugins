package info.projetcohesion.mcplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ScoreboardUtils {

    private final Scoreboard _scoreboard;

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
        AtomicReference<Score> s3 = new AtomicReference<>(objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.RED + "Warzone"));

        if (file.getConfigurationSection("zones") != null)
            Objects.requireNonNull(file.getConfigurationSection("zones")).getKeys(false).forEach(local -> {

                for (int i = 1; i <= file.getInt("zones." + local + ".number_of_chunks"); i++)
                    if (chunk.getX() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x")
                            && chunk.getZ() == file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z")) {
                        if (player.getUniqueId().toString().equals(local))
                            s3.set(objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.GREEN + "Phaeon"));
                        else
                            s3.set(objective.getScore(ChatColor.GOLD + "Chunk : " + ChatColor.RED + Objects.requireNonNull(Bukkit.getPlayer(local)).getName()));

                        if (player.isOnline()) player.sendMessage("Coords: " + file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".x") + " " + file.getInt("zones." + local + ".chunks." + (char) (i + '0') + ".z"));

                        return;
                    }
            });

        Score s4 = objective.getScore("  ");
        Score s5 = objective.getScore(ChatColor.AQUA + "Pseudo : " + ChatColor.GREEN + player.getDisplayName());

        score.setScore(6);
        s1.setScore(5);
        s2.setScore(4);
        s3.get().setScore(3);
        s4.setScore(2);
        s5.setScore(1);

        this._scoreboard = scoreboard;
    }

    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(this._scoreboard);
    }

}
