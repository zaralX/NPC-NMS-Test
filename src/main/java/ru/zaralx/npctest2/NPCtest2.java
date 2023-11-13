package ru.zaralx.npctest2;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.zaralx.npctest2.commands.npc;
import ru.zaralx.npctest2.commands.test;
import ru.zaralx.npctest2.custom.ActionsRecord;
import ru.zaralx.npctest2.events.onPlayerBlock;
import ru.zaralx.npctest2.events.onPlayerMove;

import java.util.ArrayList;
import java.util.List;

public final class NPCtest2 extends JavaPlugin {
    private static NPCtest2 instance;
    private List<ActionsRecord> actionsRecords = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        getCommand("test").setExecutor(new test());
        getCommand("npc").setExecutor(new npc());

        new onPlayerMove(this);
        new onPlayerBlock(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NPCtest2 getInstance() {
        return instance;
    }

    public void setActionsRecords(List<ActionsRecord> actionsRecords) {
        this.actionsRecords = actionsRecords;
    }

    public void addActionsRecords(ActionsRecord actionsRecord) {
        this.actionsRecords.add(actionsRecord);
    }

    public void removeActionsRecords(ActionsRecord actionsRecord) {
        this.actionsRecords.remove(actionsRecord);
    }

    public List<ActionsRecord> getActionsRecords() {
        return actionsRecords;
    }

    public ActionsRecord getActionRecord(Player player) {
        for (ActionsRecord actionsRecord : actionsRecords) {
            if (actionsRecord.getOwner() == player) {
                return actionsRecord;
            }
        }
        return null;
    }
}
