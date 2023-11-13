package ru.zaralx.npctest2.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.zaralx.npctest2.NPCtest2;
import ru.zaralx.npctest2.custom.ActionsRecord;

public class onPlayerMove implements Listener {
    public onPlayerMove(NPCtest2 plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {

    }
}
