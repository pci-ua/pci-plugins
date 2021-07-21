package info.projetcohesion.mcplugin.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private File _file;
    private FileConfiguration _customFile;
    private String _fileName;

    public FileManager (String fileName) {
        this._fileName = fileName;

        this.setup();
    }

    public void setup(){
        _file = new File(Bukkit.getServer().getPluginManager().getPlugin("pci-plugins").getDataFolder(), _fileName + ".yml");

        if (!_file.exists()){
            try{
                _file.createNewFile();
            }catch (IOException e){
                System.err.println("FileManager: Couldn't create file.");
                e.printStackTrace();
            }
        }
        this._customFile = YamlConfiguration.loadConfiguration(_file);
    }

    public FileConfiguration get(){
        return this._customFile;
    }

    public void save(){
        try{
            this._customFile.save(this._file);
        }catch (IOException e){
            System.err.println("FileManager: Couldn't save file.");
        }
    }

    public void reload(){
        this._customFile = YamlConfiguration.loadConfiguration(this._file);
    }

}
