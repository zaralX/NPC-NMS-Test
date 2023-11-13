package ru.zaralx.npctest2.utils;

import org.bukkit.entity.Pose;

public class NmsConverts {

    public static net.minecraft.world.entity.Pose convertPose(Pose pose) {
        return switch (pose) {
            case FALL_FLYING -> net.minecraft.world.entity.Pose.FALL_FLYING;
            case SLEEPING -> net.minecraft.world.entity.Pose.SLEEPING;
            case SWIMMING -> net.minecraft.world.entity.Pose.SWIMMING;
            case SPIN_ATTACK -> net.minecraft.world.entity.Pose.SPIN_ATTACK;
            case SNEAKING -> net.minecraft.world.entity.Pose.CROUCHING;
            case DYING -> net.minecraft.world.entity.Pose.DYING;
            default -> net.minecraft.world.entity.Pose.STANDING;
        };
    }
}
