package info.projetcohesion.mcplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * EcoUtils.java
 * <p>
 * Manages the money system.
 *
 * @author Jack Hogg
 */
public class EcoUtils {

    private final FileUtils _f_man;
    private final FileConfiguration _file;

    /**
     * EcoUtils constructor
     */
    public EcoUtils() {
        this._f_man = new FileUtils("money");
        this._file = _f_man.get();
    }

    /**
     * Gets the player's account balance.
     *
     * @param player Player
     * @return the player's balance
     */
    public int getAccountBalance(OfflinePlayer player) {
        return _file.getInt("accounts." + player.getUniqueId());
    }

    /**
     * Sets the player's account balance.
     *
     * @param player Player
     * @param amount New amount
     */
    public void setAccountBalance(OfflinePlayer player, int amount) {
        _file.set("accounts." + player.getUniqueId(), amount);
        save();
        if (player.isOnline()) new ScoreboardUtils((Player) player).setPlayerScoreboard((Player) player);
    }

    /**
     * Makes a player pay
     *
     * @param player Player
     * @param amount Amount payed
     */
    public void pay(OfflinePlayer player, int amount) {
        _file.set("accounts." + player.getUniqueId(), getAccountBalance(player) - amount);
        save();
        if (player.isOnline()) new ScoreboardUtils((Player) player).setPlayerScoreboard((Player) player);
    }

    /**
     * Checks if a player has an account
     *
     * @param player Player
     * @return <code>true</code> if the player has an account.
     */
    public boolean hasAccount(OfflinePlayer player) {
        return _file.contains("accounts." + player.getUniqueId());
    }

    /**
     * Create a new account
     *
     * @param player Player who is getting a new account
     */
    public void newAccount(OfflinePlayer player) {
        _file.set("accounts." + player.getUniqueId(), 0);
    }

    /**
     * Transfer money to one account to another
     *
     * @param player Player paying
     * @param target Player payed
     * @param amount Amount payed
     */
    public void transfer(Player player, OfflinePlayer target, int amount) {
        if (getAccountBalance(player) >= amount) {
            _file.set("accounts." + player.getUniqueId(), getAccountBalance(player) - amount);
            _file.set("accounts." + target.getUniqueId(), getAccountBalance(target) + amount);
            save();
            new ScoreboardUtils(player).setPlayerScoreboard(player);
            if (target.isOnline()) new ScoreboardUtils((Player) target).setPlayerScoreboard((Player) target);
        } else player.sendMessage(ChatColor.RED + "ERROR: Vous n'avez pas assez sur votre compte.");
    }

    /**
     * Saving the money.yml file
     */
    public void save() {
        _f_man.get().options().copyDefaults(true);
        _f_man.save();
    }
}
