package dk.bjom.snorch;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class SnorchTracker {
    private static final Set<String> SNORCH_NAMES = Set.of("Snorch", "Snorch Lantern", "Snorch Campfire");

    private final Snorch plugin;
    private final Set<Location> locations = new HashSet<>();
    private final File dataFile;

    public SnorchTracker(Snorch plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "snorch-locations.yml");
        load();
    }

    public boolean isSnorchItem(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomName()) return false;
        String name = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(meta.customName()));
        return SNORCH_NAMES.contains(name);
    }

    public void add(Location loc) {
        locations.add(loc);
        save();
    }

    public void remove(Location loc) {
        if (locations.remove(loc)) save();
    }

    public boolean contains(Location loc) {
        return locations.contains(loc);
    }

    public boolean isNearby(Location center, int radius) {
        World world = center.getWorld();
        if (world == null) return false;
        for (Location snorch : locations) {
            if (!world.equals(snorch.getWorld())) continue;
            int dx = Math.abs(snorch.getBlockX() - center.getBlockX());
            int dy = Math.abs(snorch.getBlockY() - center.getBlockY());
            int dz = Math.abs(snorch.getBlockZ() - center.getBlockZ());
            if (dx <= radius && dy <= radius && dz <= radius) return true;
        }
        return false;
    }

    private void load() {
        if (!dataFile.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String entry : config.getStringList("locations")) {
            String[] parts = entry.split(",", 4);
            if (parts.length != 4) continue;
            World world = Bukkit.getWorld(parts[0]);
            if (world == null) continue;
            try {
                int x = Integer.parseInt(parts[1].trim());
                int y = Integer.parseInt(parts[2].trim());
                int z = Integer.parseInt(parts[3].trim());
                locations.add(new Location(world, x, y, z));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void save() {
        YamlConfiguration config = new YamlConfiguration();
        List<String> entries = new ArrayList<>();
        for (Location loc : locations) {
            entries.add(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
        }
        config.set("locations", entries);
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save snorch-locations.yml: " + e.getMessage());
        }
    }
}
