package info.projetcohesion.mcplugin;

import org.bukkit.configuration.file.FileConfiguration;

public class ZoneChunkData {

    private final int _x;
    private final int _z;
    private String _category;

    public ZoneChunkData(int x, int z, String category) {
        this._x = x;
        this._z = z;
        this._category = category;
    }

    public int getX() {
        return _x;
    }

    public int getZ() {
        return _z;
    }

    public String getCategory() {
        return _category;
    }

    public void setCategory(String category) { this._category = category; }
}
