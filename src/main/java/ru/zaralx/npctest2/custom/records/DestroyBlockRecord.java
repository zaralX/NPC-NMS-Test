package ru.zaralx.npctest2.custom.records;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class DestroyBlockRecord {
    private final Location location;
    private final Integer tick;
    private final BlockData blockData;

    public DestroyBlockRecord(Location location, Integer tick, BlockData blockData) {
        this.location = location;
        this.tick = tick;
        this.blockData = blockData;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getTick() {
        return tick;
    }

    public void destroy() {
        location.getBlock().setType(Material.AIR);
    }
}
