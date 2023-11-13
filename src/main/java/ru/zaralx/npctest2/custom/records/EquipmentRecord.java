package ru.zaralx.npctest2.custom.records;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.EntityEquipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRecord {
    private final Integer tick;
    private CustomEquipment equipment;

    public EquipmentRecord(Integer tick, EntityEquipment equipment) {
        this.tick = tick;
        this.equipment = new CustomEquipment(equipment);
    }

    public Integer getTick() {
        return tick;
    }

    public List<Pair<EquipmentSlot, ItemStack>> buildEquipment() {
        List<Pair<EquipmentSlot, ItemStack>> list = new ArrayList<>();

        list.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(equipment.HAND)));
        list.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(equipment.OFF_HAND)));
        list.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(equipment.HELMET)));
        list.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(equipment.CHESTPLATE)));
        list.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(equipment.LEGGINGS)));
        list.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(equipment.BOOTS)));

        return list;
    }

    private static class CustomEquipment {
        org.bukkit.inventory.ItemStack HAND;
        org.bukkit.inventory.ItemStack OFF_HAND;
        org.bukkit.inventory.ItemStack HELMET;
        org.bukkit.inventory.ItemStack CHESTPLATE;
        org.bukkit.inventory.ItemStack LEGGINGS;
        org.bukkit.inventory.ItemStack BOOTS;

        public CustomEquipment(EntityEquipment eq) {
            this.HAND = eq.getItemInMainHand().clone();
            this.OFF_HAND = eq.getItemInOffHand().clone();
            if (eq.getArmorContents().length >= 4) {
                if (eq.getArmorContents()[3] != null) {
                    this.HELMET = eq.getArmorContents()[3].clone();
                }
            } if (eq.getArmorContents().length >= 3) {
                if (eq.getArmorContents()[2] != null) {
                    this.CHESTPLATE = eq.getArmorContents()[2].clone();
                }
            } if (eq.getArmorContents().length >= 2) {
                if (eq.getArmorContents()[1] != null) {
                    this.LEGGINGS = eq.getArmorContents()[1].clone();
                }
            } if (eq.getArmorContents().length >= 1) {
                if (eq.getArmorContents()[0] != null) {
                    this.BOOTS = eq.getArmorContents()[0].clone();
                }
            }
        }
    }
}
