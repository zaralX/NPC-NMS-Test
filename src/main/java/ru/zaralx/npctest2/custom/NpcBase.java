package ru.zaralx.npctest2.custom;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NpcBase {
    private Location location;
    private Skin skin;
    private ItemStack item;
    private CraftPlayer craftPlayer;
    private ServerPlayer serverPlayer;
    private MinecraftServer server;
    private ServerLevel level;
    private GameProfile gameProfile;
    private ServerPlayer npc;
    private Pose pose = Pose.STANDING;
    private ServerGamePacketListenerImpl packetListener;
    private List<BukkitTask> bukkitTasks = new ArrayList<>();

    public NpcBase(Player player) {
        init(player, "NPC");
    }

    public NpcBase(Player player, String displayName) {
        init(player, displayName);
    }

    public NpcBase(Player player, Location location) {
        this.location = location;
        init(player, "NPC");
        setLocation(location);
    }

    public NpcBase(Player player, String displayName, Location location) {
        this.location = location;
        init(player, displayName);
        setLocation(location);
    }

    public NpcBase(Player player, String displayName, Skin skin, Location location) {
        this.location = location;
        this.skin = skin;
        init(player, displayName);
        setLocation(location);
    }

    public NpcBase(Player player, String displayName, Skin skin, Location location, Pose pose) {
        this.pose = pose;
        this.location = location;
        this.skin = skin;
        init(player, displayName);
        setLocation(location);
        setPose(pose);
    }

    public NpcBase(Player player, String displayName, Skin skin, Location location, Pose pose, List<Pair<EquipmentSlot, ItemStack>> equipment) {
        this.pose = pose;
        this.location = location;
        this.skin = skin;
        init(player, displayName);
        setLocation(location);
        setPose(pose);
        setEquipment(equipment);
    }

    private void init(Player player, String displayName) {
        this.craftPlayer = (CraftPlayer) player;
        this.serverPlayer = craftPlayer.getHandle();
        this.server = serverPlayer.getServer();
        this.level = serverPlayer.getLevel();

        this.gameProfile = new GameProfile(UUID.randomUUID(), displayName);
        if (this.skin != null) {
            gameProfile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));
        }

        this.npc = new ServerPlayer(server, level, gameProfile);
        if (this.location != null) {
            npc.setPos(location.getX(), location.getY(), location.getZ());
        }

        this.packetListener = serverPlayer.connection;

        // Player Info Packet
        packetListener.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));

        // Spawn Packet
        packetListener.send(new ClientboundAddPlayerPacket(npc));

        // Second Skin Layer
        SynchedEntityData watcher = new SynchedEntityData(serverPlayer);
        watcher.define(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

        List<SynchedEntityData.DataValue<?>> l = new ArrayList<>();
        try {
            Field field = watcher.getClass().getDeclaredField("e");
            field.setAccessible(true);
            Int2ObjectMap<SynchedEntityData.DataItem<?>> map = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) field.get(watcher);
            for(SynchedEntityData.DataItem<?> item : map.values())
                l.add(item.value());
        } catch (Exception e) {}

        packetListener.send(new ClientboundSetEntityDataPacket(npc.getId(), l));
    }

    public void swingRightArm() {
        packetListener.send(new ClientboundAnimatePacket(npc, 0));
    }

    public void leaveBed() {
        packetListener.send(new ClientboundAnimatePacket(npc, 2));
    }

    public void swingLeftArm() {
        packetListener.send(new ClientboundAnimatePacket(npc, 3));
    }

    public void criticalEffect() {
        packetListener.send(new ClientboundAnimatePacket(npc, 4));
    }

    public void magicCriticalEffect() {
        packetListener.send(new ClientboundAnimatePacket(npc, 5));
    }

    public void setPose(Pose pose) {
        SynchedEntityData data = npc.getEntityData();
        data.set(EntityDataSerializers.POSE.createAccessor(6),pose);

        List<SynchedEntityData.DataValue<?>> l = new ArrayList<>();
        try {
            Field field = data.getClass().getDeclaredField("e");
            field.setAccessible(true);
            Int2ObjectMap<SynchedEntityData.DataItem<?>> map = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) field.get(data);
            for(SynchedEntityData.DataItem<?> item : map.values())
                l.add(item.value());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        packetListener.send(new ClientboundSetEntityDataPacket(npc.getId(),l));

        // TODO: OPTIMIZE PACKETS
        SynchedEntityData watcher = new SynchedEntityData(serverPlayer);
        watcher.define(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

        l = new ArrayList<>();
        try {
            Field field = watcher.getClass().getDeclaredField("e");
            field.setAccessible(true);
            Int2ObjectMap<SynchedEntityData.DataItem<?>> map = (Int2ObjectMap<SynchedEntityData.DataItem<?>>) field.get(watcher);
            for(SynchedEntityData.DataItem<?> item : map.values())
                l.add(item.value());
        } catch (Exception e) {}

        packetListener.send(new ClientboundSetEntityDataPacket(npc.getId(), l));
    }

    public void setLocation(Location location) {
        // Set Yaw
        packetListener.send(new ClientboundRotateHeadPacket(npc, (byte) ((location.getYaw()%360)*256/360)));

        // Set Position And Rotation
        packetListener.send(new ClientboundMoveEntityPacket.PosRot(
                npc.getBukkitEntity().getEntityId(),
                (short) ((location.getX() * 32 - this.location.getX() * 32) * 128),
                (short) ((location.getY() * 32 - this.location.getY() * 32) * 128),
                (short) ((location.getZ() * 32 - this.location.getZ() * 32) * 128),
                (byte) ((location.getYaw()%360)*256/360),
                (byte) ((location.getPitch()%360)*256/360),
                true));
        this.location = location;
    }

    public void addBukkitRunnable(BukkitTask runnable) {
        this.bukkitTasks.add(runnable);
    }

    public void removeBukkitTask(BukkitTask runnable) {
        if (this.bukkitTasks.remove(runnable)) {
            runnable.cancel();
        }
    }

    public void setEquipment(List<Pair<EquipmentSlot, ItemStack>> equipment) {
        packetListener.send(new ClientboundSetEquipmentPacket(npc.getBukkitEntity().getEntityId(), equipment));
    }

    public void delete() {
        // Despawn Packet
        packetListener.send(new ClientboundRemoveEntitiesPacket(npc.getBukkitEntity().getEntityId()));
        for (BukkitTask task : bukkitTasks) {
            removeBukkitTask(task);
        }
    }
}
