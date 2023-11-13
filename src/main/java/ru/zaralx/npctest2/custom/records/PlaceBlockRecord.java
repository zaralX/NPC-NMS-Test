package ru.zaralx.npctest2.custom.records;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

public class PlaceBlockRecord {
    private final Location location;
    private final Integer tick;
    private final BlockData blockData;
    private final Integer hand; // 0 - HAND | 1 - OFFHAND

    public PlaceBlockRecord(Location location, Integer tick, BlockData blockData, Integer hand) {
        this.location = location;
        this.tick = tick;
        this.blockData = blockData;
        this.hand = hand;
    }

    public Location getLocation() {
        return location;
    }

    public Integer getTick() {
        return tick;
    }

    public void place() {
        location.getBlock().setBlockData(blockData);
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public Integer getHand() {
        return hand;
    }
}
