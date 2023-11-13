package ru.zaralx.npctest2.custom.records;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class OriginalBlock {
    private final Location location;
    private final BlockData blockData;

    public OriginalBlock(Location location, BlockData blockData) {
        this.location = location;
        this.blockData = blockData;
    }

    public void place() {
        System.out.println(blockData);
        location.getBlock().setBlockData(blockData);
    }

    public Location getLocation() {
        return location;
    }

    public BlockData getBlockData() {
        return blockData;
    }
}
