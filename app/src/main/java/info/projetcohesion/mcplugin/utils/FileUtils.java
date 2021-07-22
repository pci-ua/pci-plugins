package info.projetcohesion.mcplugin.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    private File _file;
    private FileConfiguration _customFile;
    private String _fileName;

    public FileUtils(String fileName) {
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
            e.printStackTrace();
        }
    }

    public void reload(){
        this._customFile = YamlConfiguration.loadConfiguration(this._file);
    }

}
