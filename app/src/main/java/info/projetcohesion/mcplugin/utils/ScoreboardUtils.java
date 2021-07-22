package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.managers.EcoManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardUtils {

    private Scoreboard _scoreboard;

    public ScoreboardUtils() {

    }

    public ScoreboardUtils(Player player) {

        EcoManager eco = new EcoManager();

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("Title",
                "dummy",
                net.md_5.bungee.api.ChatColor.of("#007f7f") + "PC[" + net.md_5.bungee.api.ChatColor.of("#ffa500") + "i" + net.md_5.bungee.api.ChatColor.of("#007f7f") + "]");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore("");
        Score s1 = objective.getScore(ChatColor.GOLD + "Solde : " + ChatColor.GREEN + eco.getAccountBalance(player) + " \u272a");
        Score s2 = objective.getScore(" ");
        Score s3 = objective.getScore(ChatColor.AQUA + "Pseudo : " + ChatColor.GREEN + player.getDisplayName());

        score.setScore(4);
        s1.setScore(3);
        s2.setScore(2);
        s3.setScore(1);

        this._scoreboard = scoreboard;
    }

    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(this._scoreboard);
    }

    public Scoreboard getScoreboard() {
        return this._scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this._scoreboard = scoreboard;
    }

}
