package ru.zaralx.npctest2.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.entity.Pose;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.zaralx.npctest2.NPCtest2;
import ru.zaralx.npctest2.custom.ActionsRecord;
import ru.zaralx.npctest2.custom.NpcBase;
import ru.zaralx.npctest2.custom.Skin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class npc implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2) {
            if (Objects.equals(args[0], "summon")) {
                NpcBase npc = new NpcBase((Player) sender, args[1], new Skin(args[2]), ((Player) sender).getLocation(), Pose.valueOf(args[3]));
            } else if (Objects.equals(args[0], "record")) {
                if (Objects.equals(args[1], "start")) {
                    ActionsRecord actionsRecord = new ActionsRecord((Player) sender, true);
                    NPCtest2.getInstance().addActionsRecords(actionsRecord);
                    sender.sendMessage("§aStarted");
                } else if (Objects.equals(args[1], "stop")) {
                    for (ActionsRecord rec : NPCtest2.getInstance().getActionsRecords()) {
                        if (rec.getOwner() == sender) {
                            rec.setRecording(false);
                            System.err.println(rec.getLocRecords().size());
                            rec.placeOriginalBlocks();
                            sender.sendMessage("§aStopped");
                            break;
                        }
                    }
                } else if (Objects.equals(args[1], "list")) {
                    sender.sendMessage("§f # §8| §aВладелец §8| §6Длительность");
                    int i = 0;
                    for (ActionsRecord rec : NPCtest2.getInstance().getActionsRecords()) {
                        String text = rec.getRecordedTicks() + "t";
                        int seconds = rec.getRecordedTicks() / 20;
                        int minutes = seconds / 60;
                        int hours = minutes / 60;
                        int days = hours / 24;

                        seconds %= 60;
                        minutes %= 60;
                        hours %= 24;

                        if (days > 0) {
                            text = days + "d";
                        } else if (hours > 0) {
                            text = hours + "h";
                        } else if (minutes > 0) {
                            text = minutes + "m";
                        } else if (seconds > 0) {
                            text = seconds + "s";
                        }

                        // Create the clickable message
                        TextComponent message = new TextComponent("§f " + i + " §8| §a" + rec.getOwner().getName() + " §8| §6" + text);
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/npc record play " + i));

                        // Add hover text
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("/run record play "+i).create()));

                        // Send the message to the sender
                        sender.spigot().sendMessage(message);

                        i++;
                    }
                } else if (Objects.equals(args[1], "play")) {
                    sender.sendMessage("§eAttempting to run..");
                    if (NPCtest2.getInstance().getActionsRecords().get(Integer.parseInt(args[2])).play()) {
                        sender.sendMessage("§aRunned");
                    } else {
                        sender.sendMessage("§cFailed to run");
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> arguments = new ArrayList<>();

        arguments.add("ACTION");
        arguments.add("NAME");
        arguments.add("SKIN_NICKNAME");

        return arguments;
    }
}
