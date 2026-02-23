package dk.bjom.snorch;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListener implements Listener {
    private final Snorch plugin;
    private final SnorchTracker tracker;
    private final Map<Location, List<BukkitTask>> activeMelts = new HashMap<>();

    public EventListener(Snorch plugin, SnorchTracker tracker) {
        this.plugin = plugin;
        this.tracker = tracker;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSnowForm(BlockFormEvent event) {
        if (event.getNewState().getType() != Material.SNOW) return;

        int radius = plugin.getConfig().getInt("snorch-radius", 10);
        if (tracker.isNearby(event.getBlock().getLocation(), radius)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSnorchPlace(BlockPlaceEvent event) {
        Material type = event.getBlockPlaced().getType();
        if (type != Material.SOUL_TORCH && type != Material.SOUL_WALL_TORCH
                && type != Material.SOUL_LANTERN
                && type != Material.SOUL_CAMPFIRE) return;
        if (!tracker.isSnorchItem(event.getItemInHand())) return;
        tracker.add(event.getBlockPlaced().getLocation());
        startMelt(event.getBlockPlaced());
    }

    @EventHandler
    public void onSnorchBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (!tracker.contains(loc)) return;
        tracker.remove(loc);
        List<BukkitTask> tasks = activeMelts.remove(loc);
        if (tasks != null) tasks.forEach(BukkitTask::cancel);
    }

    private void meltLayer(Block snowBlock, int layerTicks, List<BukkitTask> tasks, Location torchLocation) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!activeMelts.containsKey(torchLocation)) return;
            if (snowBlock.getType() != Material.SNOW) return;
            Snow snow = (Snow) snowBlock.getBlockData();
            if (snow.getLayers() <= snow.getMinimumLayers()) {
                snowBlock.setType(Material.AIR);
            } else {
                snow.setLayers(snow.getLayers() - 1);
                snowBlock.setBlockData(snow);
                meltLayer(snowBlock, layerTicks, tasks, torchLocation);
            }
        }, layerTicks);
        tasks.add(task);
    }

    private void startMelt(Block torch) {
        int radius = plugin.getConfig().getInt("snorch-radius", 10);
        int ringDelay = plugin.getConfig().getInt("melt-ring-delay-ticks", 10);
        int layerTicks = plugin.getConfig().getInt("melt-layer-ticks", 4);

        World world = torch.getWorld();
        int cx = torch.getX(), cy = torch.getY(), cz = torch.getZ();

        Map<Integer, List<Block>> rings = new HashMap<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int blockX = cx + dx, blockZ = cz + dz;
                if (!world.isChunkLoaded(blockX >> 4, blockZ >> 4)) continue;
                for (int dy = -radius; dy <= radius; dy++) {
                    Block b = world.getBlockAt(blockX, cy + dy, blockZ);
                    if (b.getType() != Material.SNOW) continue;
                    int dist = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
                    rings.computeIfAbsent(dist, k -> new ArrayList<>()).add(b);
                }
            }
        }

        if (rings.isEmpty()) return;

        Location torchLocation = torch.getLocation();
        List<BukkitTask> tasks = new ArrayList<>();
        for (Map.Entry<Integer, List<Block>> entry : rings.entrySet()) {
            long startDelay = (long) entry.getKey() * ringDelay;
            List<Block> ringBlocks = entry.getValue();
            BukkitTask ringTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                for (Block snowBlock : ringBlocks) {
                    if (!snowBlock.getChunk().isLoaded()) continue;
                    meltLayer(snowBlock, layerTicks, tasks, torchLocation);
                }
            }, startDelay);
            tasks.add(ringTask);
        }

        List<BukkitTask> existing = activeMelts.put(torchLocation, tasks);
        if (existing != null) existing.forEach(BukkitTask::cancel);
    }

    public void shutdown() {
        activeMelts.values().forEach(list -> list.forEach(BukkitTask::cancel));
        activeMelts.clear();
    }
}
