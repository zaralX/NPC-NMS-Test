package ru.zaralx.npctest2.custom;

import net.minecraft.world.entity.Pose;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.zaralx.npctest2.NPCtest2;
import ru.zaralx.npctest2.custom.records.DestroyBlockRecord;
import ru.zaralx.npctest2.custom.records.EquipmentRecord;
import ru.zaralx.npctest2.custom.records.OriginalBlock;
import ru.zaralx.npctest2.custom.records.PlaceBlockRecord;
import ru.zaralx.npctest2.utils.NmsConverts;

import java.util.ArrayList;
import java.util.List;

public class ActionsRecord {
    Player owner;
    Boolean recording;
    Integer recordedTicks = 0;
    BukkitTask recordTask;
    BukkitTask playTask;

    List<OriginalBlock> originalBlocks = new ArrayList<>();

    List<Location> locRecords = new ArrayList<>();
    List<Pose> poseRecords = new ArrayList<>();
    List<PlaceBlockRecord> placeBlockRecords = new ArrayList<>();
    List<DestroyBlockRecord> destroyBlockRecords = new ArrayList<>();
    List<EquipmentRecord> equipmentRecords = new ArrayList<>();

    public ActionsRecord(Player owner, Boolean recording) {
        this.owner = owner;
        this.recording = recording;
        locRecords.add(owner.getLocation());
        poseRecords.add(NmsConverts.convertPose(owner.getPose()));
        equipmentRecords.add(new EquipmentRecord(recordedTicks, owner.getEquipment()));
        recordTask = new BukkitRunnable() {
            Location prevLoc = owner.getLocation();
            Pose prevPose = NmsConverts.convertPose(owner.getPose());
            EntityEquipment prevEquipment = owner.getEquipment();

            @Override
            public void run() {

                if (prevLoc.equals(owner.getLocation())) {
                    locRecords.add(null);
                } else {
                    locRecords.add(owner.getLocation());
                    prevLoc = owner.getLocation();
                }

                if (prevPose.equals(NmsConverts.convertPose(owner.getPose()))) {
                    poseRecords.add(null);
                } else {
                    poseRecords.add(NmsConverts.convertPose(owner.getPose()));
                    prevPose = NmsConverts.convertPose(owner.getPose());
                }

                equipmentRecords.add(new EquipmentRecord(recordedTicks, owner.getEquipment()));


                recordedTicks++;
            }
        }.runTaskTimer(NPCtest2.getInstance(), 0, 1);
    }

    public boolean play() {
        if (playTask != null) {
            if (!playTask.isCancelled()) return false;
        }
        new BukkitRunnable() {
            Integer curTick = 0;
            final NpcBase npcBase = new NpcBase(owner, "runner", new Skin("Elsham1r"), getLocRecords().get(curTick));

            @Override
            public void run() {
                try {
                    if (getLocRecords().get(curTick) != null) {
                        npcBase.setLocation(getLocRecords().get(curTick));
                    }

                    if (getPoseRecords().get(curTick) != null) {
                        npcBase.setPose(getPoseRecords().get(curTick));
                    }

                    for (PlaceBlockRecord placeRecord : getPlaceBlockRecords()) {
                        if (placeRecord.getTick().equals(curTick)) {
                            if (placeRecord.getHand() == 0) {
                                npcBase.swingRightArm();
                            } else if (placeRecord.getHand() == 1) {
                                npcBase.swingLeftArm();
                            }
                            placeRecord.place();
                            owner.playSound(placeRecord.getLocation().clone().add(0.5,0.5,0.5), placeRecord.getBlockData().getSoundGroup().getPlaceSound(), 1, 1);
                            break;
                        }
                    }

                    for (DestroyBlockRecord destroyRecord : getDestroyBlockRecords()) {
                        if (destroyRecord.getTick().equals(curTick)) {
                            npcBase.swingRightArm();
                            destroyRecord.destroy();
                            owner.spawnParticle(Particle.ITEM_CRACK, destroyRecord.getLocation().clone().add(0.5,0.5,0.5), 20, 0.2, 0.2, 0.2, 0.1, new ItemStack(destroyRecord.getBlockData().getMaterial()));
                            owner.playSound(destroyRecord.getLocation().clone().add(0.5,0.5,0.5), destroyRecord.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
                            break;
                        }
                    }

                    if (getEquipmentRecords().get(curTick) != null) {
                        npcBase.setEquipment(getEquipmentRecords().get(curTick).buildEquipment());
                    }

                    curTick++;
                    if (curTick > recordedTicks) {
                        owner.sendMessage("§aFinished");
                        this.cancel();
                        npcBase.delete();
                        placeOriginalBlocks();
                    }
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                    this.cancel();
                    npcBase.delete();
                    placeOriginalBlocks();
                }
            }
        }.runTaskTimer(NPCtest2.getInstance(), 0, 1);
        return true;
    }

    public List<Location> getLocRecords() {
        return locRecords;
    }

    public void setRecording(Boolean recording) {
        this.recording = recording;
        if (!recording && recordTask != null) {
            if (!recordTask.isCancelled()) {
                recordTask.cancel();
            }
        }
    }

    public List<Pose> getPoseRecords() {
        return poseRecords;
    }

    public Boolean getRecording() {
        return recording;
    }

    public Player getOwner() {
        return owner;
    }

    public Integer getRecordedTicks() {
        return recordedTicks;
    }

    public void addRecordedTick() {
        this.recordedTicks++;
    }

    public List<PlaceBlockRecord> getPlaceBlockRecords() {
        return placeBlockRecords;
    }

    public void addPlaceBlockRecord(Location loc, BlockData blockData, EquipmentSlot slot) {
        if (slot == EquipmentSlot.HAND) {
            this.placeBlockRecords.add(new PlaceBlockRecord(loc, recordedTicks, blockData, 0));
        } else if (slot == EquipmentSlot.OFF_HAND) {
            this.placeBlockRecords.add(new PlaceBlockRecord(loc, recordedTicks, blockData, 1));
        }
    }

    public void addDestroyBlockRecord(Location loc, BlockData blockData) {
        this.destroyBlockRecords.add(new DestroyBlockRecord(loc, recordedTicks, blockData));
    }

    public void placeOriginalBlocks() {
        for (OriginalBlock originalBlock : originalBlocks) {
            originalBlock.place();
        }
    }

    public boolean addOriginalBlock(OriginalBlock originalBlock) {
        for (OriginalBlock ob : this.originalBlocks) {
            if (ob.getLocation().equals(originalBlock.getLocation())) {
                return false;
            }
        }
        this.originalBlocks.add(originalBlock);
        return true;
    }

    public List<OriginalBlock> getOriginalBlocks() {
        return originalBlocks;
    }

    public List<DestroyBlockRecord> getDestroyBlockRecords() {
        return destroyBlockRecords;
    }

    public List<EquipmentRecord> getEquipmentRecords() {
        return equipmentRecords;
    }
}
