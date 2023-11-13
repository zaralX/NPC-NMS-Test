package ru.zaralx.npctest2.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import ru.zaralx.npctest2.NPCtest2;
import ru.zaralx.npctest2.custom.ActionsRecord;
import ru.zaralx.npctest2.custom.records.OriginalBlock;
import ru.zaralx.npctest2.custom.records.PlaceBlockRecord;

public class onPlayerBlock implements Listener {
    public onPlayerBlock(NPCtest2 plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        System.err.println("Interact");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        // e.getBlockReplacedState() // Предыдущий блок
        // e.getBlock(); // Новый блок
        // e.getBlockAgainst(); // Блок на который ставим

        Player player = e.getPlayer();
        ActionsRecord actionsRecord = NPCtest2.getInstance().getActionRecord(player);
        if (actionsRecord != null) {
            if (actionsRecord.getRecording()) {
                actionsRecord.addOriginalBlock(new OriginalBlock(e.getBlock().getLocation(), e.getBlockReplacedState().getBlockData()));
                actionsRecord.addPlaceBlockRecord(e.getBlock().getLocation(), e.getBlock().getBlockData(), e.getHand());
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        System.err.println("Break");
        Player player = e.getPlayer();
        ActionsRecord actionsRecord = NPCtest2.getInstance().getActionRecord(player);
        if (actionsRecord != null) {
            if (actionsRecord.getRecording()) {
                actionsRecord.addOriginalBlock(new OriginalBlock(e.getBlock().getLocation(), e.getBlock().getBlockData()));
                actionsRecord.addDestroyBlockRecord(e.getBlock().getLocation(), e.getBlock().getBlockData());
            }
        }
    }
}
