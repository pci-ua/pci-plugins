package info.projetcohesion.mcplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class EcoUtils {

    private FileUtils _f_man;
    private FileConfiguration _file;

    public EcoUtils() {
        this._f_man = new FileUtils("money");
        this._file = _f_man.get();
    }

    public int getAccountBalance(OfflinePlayer player) {
        return _file.getInt("accounts." + player.getUniqueId());
    }

    public void setAccountBalance(OfflinePlayer player, int amount) {
        _file.set("accounts." + player.getUniqueId(), amount);
        save();
        if (player.isOnline()) new ScoreboardUtils((Player) player).setPlayerScoreboard((Player) player);
    }

    public void pay(OfflinePlayer player, int amount) {
        _file.set("accounts." + player.getUniqueId(), getAccountBalance(player) - amount);
        save();
        if (player.isOnline()) new ScoreboardUtils((Player) player).setPlayerScoreboard((Player) player);
    }

    public boolean hasAccount(OfflinePlayer player) {
        return _file.contains("accounts." + player.getUniqueId());
    }

    public void newAccount(OfflinePlayer player) {
        _file.set("accounts." + player.getUniqueId(), 0);
    }

    public void transfer(Player player, OfflinePlayer target, int amount) {
        if (getAccountBalance(player) >= amount) {
            _file.set("accounts." + player.getUniqueId(), getAccountBalance(player) - amount);
            _file.set("accounts." + target.getUniqueId(), getAccountBalance(target) + amount);
            save();
            new ScoreboardUtils(player).setPlayerScoreboard(player);
            if (target.isOnline()) new ScoreboardUtils((Player) target).setPlayerScoreboard((Player) target);;
        } else player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas assez sur votre compte.");
    }

    public void save() {
        _f_man.get().options().copyDefaults(true);
        _f_man.save();
    }
}
