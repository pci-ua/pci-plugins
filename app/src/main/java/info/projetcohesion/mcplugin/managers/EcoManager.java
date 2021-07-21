package info.projetcohesion.mcplugin.managers;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class EcoManager {

    private FileManager _f_man;
    private FileConfiguration _file;

    public EcoManager() {
        this._f_man = new FileManager("money");
        this._file = _f_man.get();
    }

    public double getAccountBalance(OfflinePlayer player) {
        return _file.getDouble(player.getUniqueId().toString());
    }

    public void setAccountBalance(OfflinePlayer player, double amount) {
        _file.set(player.getUniqueId().toString(), amount);
    }

    public boolean hasAccount(OfflinePlayer player) {
        return _file.contains(player.getUniqueId().toString());
    }

    public void newAccount(OfflinePlayer player) {
        _file.set(player.getUniqueId().toString(), 0.0);
    }


}
