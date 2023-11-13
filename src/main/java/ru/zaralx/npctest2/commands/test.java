package ru.zaralx.npctest2.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.zaralx.npctest2.NPCtest2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class test implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("hi");

        if (sender instanceof Player player) {
            Location location = player.getLocation();

            CraftPlayer craftPlayer = (CraftPlayer) player;
            ServerPlayer serverPlayer = craftPlayer.getHandle();

            MinecraftServer server = serverPlayer.getServer();
            ServerLevel level = serverPlayer.getLevel();
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "NpcBase");

            // https://sessionserver.mojang.com/session/minecraft/profile/bc6b10854663491ebdf849c2eaf2d586?unsigned=false
            String signature = "Hk48MZs9GFFqc2rCvS7o11fF150r8nvSlXkEEnMfPzOneAVijwMyftZyDfO72vekANmOBc27acP9Ydq+JluqK5bxNGX7ry0ABqEpOLXqL1e3UfJ+LBobnK+oMEFcskn1+p9ofSHLWfmjQ2JmrjdxxWQdGd4b+OaUfdt+xYEJVhpYqug1of1ASOM5FtU5AcUt91Fn8QaBgw+DAo6aCYDLdFzxQxb9Zbb98d9LzQc2m528W1eos5GVCv/6uYFLqhCx4+2qn0fIQJjbtFFeL0nANvcR1xe+242nAKGTF6hefOH1qGfgk2O0DV47zHXoyM7DvBZ3vxSmYUNPGDEExyG2MRCmX5qnIFYUEQMqLNv85Sq8iHydP732EV1ag7xlbBH+HvhAmYm4iVVTpExwQFLM6zWWS79saRWTz2ChbQTL1F7hGJewSW035z8zvB51p0vxk5TtPS12wA8yssFqr0YhFozrGIQPb3wGR1ZMVhb4Kgkz+JqWs2z2zN9BER2CfvuTBskv4NCIS//lGnF5OdSgnodzXUyOmBBmP8f/LpFMjUpU5RRNhRhxb0I1pm6pIY0pF0IW8A/1v+Jl1pdpNutPTK4Ya0pDhRQ51G2DyadwbFAk8o5JykhMFdbLiYf86lBAGbd9xz0LXbmP/UAw/QlBTRXs9YolS9G8zN4p6ewS0bk=";
            String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY5OTc5NDczODU2NSwKICAicHJvZmlsZUlkIiA6ICJiYzZiMTA4NTQ2NjM0OTFlYmRmODQ5YzJlYWYyZDU4NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJzc3NhYWF5eXl1dXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI1ZDQ1N2JmMjBiNmI3ODZiMDg2OWQzY2E2MDZkZDYxNmMxN2Q5M2M3NTVjZmJjNTMxNzY4YzJjM2JjMjAwZCIKICAgIH0KICB9Cn0=";

            gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

            assert server != null;
            ServerPlayer npc = new ServerPlayer(server, level, gameProfile);
            npc.setPos(location.getX(), location.getY(), location.getZ());

            ServerGamePacketListenerImpl packetListener = serverPlayer.connection;

            // Player Info Packet
            packetListener.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));

            // Spawn Packet
            packetListener.send(new ClientboundAddPlayerPacket(npc));

            // Rotate Packet
            new BukkitRunnable() {
                Location rotation = location.clone();
                @Override
                public void run() {
                    rotation.setYaw(rotation.getYaw()+1F);
                    if (rotation.getYaw() >= 180F) {
                        rotation.setYaw(-180F);
                    }
                    packetListener.send(new ClientboundRotateHeadPacket(npc, (byte) ((rotation.getYaw()%360)*256/360)));
                }
            }.runTaskTimer(NPCtest2.getInstance(), 0, 1);

            sender.sendMessage("sended");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();

        return arguments;
    }
}
