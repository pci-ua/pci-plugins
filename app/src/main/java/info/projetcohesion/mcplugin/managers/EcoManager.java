package info.projetcohesion.mcplugin.managers;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class EcoManager {

    private FileManager _f_man;
    private FileConfiguration _file;

    public EcoManager() {
        this._f_man = new FileManager("money");
        this._file = _f_man.get();
    }

    public int getAccountBalance(OfflinePlayer player) {
        return _file.getInt("accounts." + player.getUniqueId());
    }

    public void setAccountBalance(OfflinePlayer player, int amount) {
        _file.set("accounts." + player.getUniqueId(), amount);
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
        } else player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas assez sur votre compte.");
    }

    public void save() {
        _f_man.get().options().copyDefaults(true);
        _f_man.save();
    }
}
