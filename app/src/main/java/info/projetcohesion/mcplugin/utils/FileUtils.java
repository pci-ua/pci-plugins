package info.projetcohesion.mcplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * FileUtils.java
 * <p>
 * Manages the file system.
 *
 * @author Jack Hogg
 */
public class FileUtils {

    private File _file;
    private FileConfiguration _customFile;
    private final String _fileName;

    /**
     * FileUtils constructor
     *
     * @param fileName Name of the file
     */
    public FileUtils(String fileName) {
        this._fileName = fileName;

        this.setup();
    }

    /**
     * Setup a file.
     */
    public void setup() {
        _file = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("pci-plugins")).getDataFolder(), _fileName + ".yml");

        if (!_file.exists()) {
            try {
                _file.createNewFile();
            } catch (IOException e) {
                System.err.println("FileManager: Couldn't create file.");
                e.printStackTrace();
            }
        }
        this._customFile = YamlConfiguration.loadConfiguration(_file);
    }

    /**
     * Get the file configuration.
     *
     * @return the file configuration
     */
    public FileConfiguration get() {
        return this._customFile;
    }

    /**
     * Save the file.
     */
    public void save() {
        try {
            this._customFile.save(this._file);
        } catch (IOException e) {
            System.err.println("FileManager: Couldn't save file.");
            e.printStackTrace();
        }
    }

    /**
     * Reload the file.
     */
    public void reload() {
        this._customFile = YamlConfiguration.loadConfiguration(this._file);
    }

}
