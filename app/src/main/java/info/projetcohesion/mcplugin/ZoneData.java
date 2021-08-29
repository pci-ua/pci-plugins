package info.projetcohesion.mcplugin;

import java.util.List;

public class ZoneData {

    private int _numberOfChunks;

    private final List<String> _allowed;
    private final List<String> _effects;
    private final List<ZoneChunkData> _chunks;

    public ZoneData(int number_of_chunks, List<String> allowed, List<String> effects, List<ZoneChunkData> chunks) {
        this._numberOfChunks = number_of_chunks;
        this._allowed = allowed;
        this._effects = effects;
        this._chunks = chunks;
    }

    public void addChunk(int x, int z) {
        ZoneChunkData chunk = new ZoneChunkData(x, z, "z_def");
        this._chunks.add(chunk);
        this._numberOfChunks++;
    }

    public boolean removeChunk(int x, int z) {
        if (isOwned(x, z)) {
            int id = getChunkId(x, z);
            this._chunks.remove(id);
            this._numberOfChunks--;

            return true;
        }

        return false;
    }

    public boolean removeChunkById(int id) {
        ZoneChunkData chunk = this._chunks.get(id);

        if (this.isOwned(chunk.getX(), chunk.getZ())) {
            this._chunks.remove(id);
            this._numberOfChunks--;

            return true;
        }

        return false;
    }

    public void removeAllChunks() {
        this._chunks.clear();
        this._numberOfChunks = 0;
    }

    public boolean hasZones() {
        return this._numberOfChunks != 0;
    }

    public void addMember(String playerUUID) {
        this._allowed.add(playerUUID);
    }

    public boolean isMember(String playerUUID) {
        return this._allowed.contains(playerUUID);
    }

    public void removeMember(String playerUUID) {
        this._allowed.remove(playerUUID);
    }

    public boolean isOwned(int x, int z) {
        for (ZoneChunkData chunk : this._chunks) {
            if (chunk.getX() == x
                    && chunk.getZ() == z)
                return true;
        }

        return false;
    }

    public int getChunkId(int x, int z) {
        for (int i = 0; i < this._chunks.size(); i++) {
            if (_chunks.get(i).getX() == x
                    && _chunks.get(i).getZ() == z)
                return i;
        }

        return -1;
    }

    public int getNumberOfChunks() {
        return _numberOfChunks;
    }

    public List<String> getAllowed() {
        return _allowed;
    }

    public List<String> getEffects() {
        return _effects;
    }

    public List<ZoneChunkData> getChunks() {
        return _chunks;
    }
}
