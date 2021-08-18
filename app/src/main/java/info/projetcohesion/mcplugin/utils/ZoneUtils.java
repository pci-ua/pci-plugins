package info.projetcohesion.mcplugin.utils;

import info.projetcohesion.mcplugin.ZoneChunkData;
import info.projetcohesion.mcplugin.ZoneData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ZoneUtils {

    private static final HashMap<String, ZoneData> s_zones = new HashMap<>();

    public static void newData(String uuid) {
        List<String> allowed = new ArrayList<>(), effects = new ArrayList<>();
        List<ZoneChunkData> chunks = new ArrayList<>();

        s_zones.put(uuid, new ZoneData(0, allowed, effects, chunks));
    }

    public static void loadData() {
        FileConfiguration file = new FileUtils("zones").get();

        if (file.getConfigurationSection("zones") == null)
            return;

        for (String uuid : Objects.requireNonNull(file.getConfigurationSection("zones")).getKeys(false)) {

            List<ZoneChunkData> chunks = new ArrayList<>();

            for (int i = 1; i <= file.getInt("zones." + uuid + ".number_of_chunks"); i++) {
                ZoneChunkData chunk = new ZoneChunkData(
                        file.getInt("zones." + uuid + ".chunks." + (char) (i + '0') + ".x"),
                        file.getInt("zones." + uuid + ".chunks." + (char) (i + '0') + ".z"),
                        file.getString("zones." + uuid + ".chunks." + (char) (i + '0') + ".category")
                );

                chunks.add(chunk);
            }

            ZoneData data = new ZoneData(
                    file.getInt("zones." + uuid + ".number_of_chunks"),
                    file.getStringList("zones." + uuid + ".allowed"),
                    file.getStringList("zones." + uuid + ".effects"),
                    chunks
            );

            s_zones.put(uuid, data);
        }
    }

    public static void saveData() {
        FileUtils f_man = new FileUtils("zones");
        FileConfiguration file = f_man.get();

        file.set("zones", null);

        for (String uuid : s_zones.keySet()) {
            file.set("zones." + uuid + ".number_of_chunks", s_zones.get(uuid).getNumberOfChunks());

            List<String> allowed = new ArrayList<>(s_zones.get(uuid).getAllowed());
            file.set("zones." + uuid + ".allowed", allowed);

            List<String> effects = new ArrayList<>(s_zones.get(uuid).getEffects());
            file.set("zones." + uuid + ".effects", effects);

            for (ZoneChunkData chunk : s_zones.get(uuid).getChunks()) {
                int id = s_zones.get(uuid).getChunks().indexOf(chunk) + 1;

                file.set("zones." + uuid + ".chunks." + (char) (id + '0') + ".x", chunk.getX());
                file.set("zones." + uuid + ".chunks." + (char) (id + '0') + ".z", chunk.getZ());
                file.set("zones." + uuid + ".chunks." + (char) (id + '0') + ".category", chunk.getCategory());
            }
        }

        file.options().copyDefaults(true);
        f_man.save();
    }

    public static void showData() {
        for (String uuid : s_zones.keySet()) {
            Bukkit.getPlayer("Phaeon").sendMessage("Number of chunks : " + s_zones.get(uuid).getNumberOfChunks());

            Bukkit.getPlayer("Phaeon").sendMessage("Allowed : ");
            for (String s : s_zones.get(uuid).getAllowed())
                Bukkit.getPlayer("Phaeon").sendMessage(s);

            Bukkit.getPlayer("Phaeon").sendMessage("Effects : ");
            for (String s : s_zones.get(uuid).getEffects())
                Bukkit.getPlayer("Phaeon").sendMessage(s);

            Bukkit.getPlayer("Phaeon").sendMessage("Chunks : ");
            for (ZoneChunkData chunk : s_zones.get(uuid).getChunks()) {
                Bukkit.getPlayer("Phaeon").sendMessage(chunk.getX() + " " + chunk.getZ() + " " + chunk.getCategory());
            }
        }
    }

    public static ZoneData getPlayerData(String playerUUID) {
        return s_zones.get(playerUUID);
    }

    public static List<ZoneData> getAllData() {
        List<ZoneData> data = new ArrayList<>();

        for (String uuid : s_zones.keySet()) {
            data.add(s_zones.get(uuid));
        }

        return data;
    }

    public static List<ZoneChunkData> getAllChunks() {
        List<ZoneChunkData> data = new ArrayList<>();

        for (String uuid : s_zones.keySet()) {
            data.addAll(s_zones.get(uuid).getChunks());
        }

        return data;
    }

    public static HashMap<String, ZoneData> getZones() {
        return s_zones;
    }

}
