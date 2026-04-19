package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DemonicPactsTooltipOverlay extends Overlay
{
    private final Client client;
    private final DemonicPactsConfig config;
    private final TooltipManager tooltipManager;
    private final ItemManager itemManager;
    private final DemonicPactsPlugin plugin;
    private final LeagueAreaManager areaManager;

    @Inject
    DemonicPactsTooltipOverlay(Client client, DemonicPactsConfig config, TooltipManager tooltipManager, ItemManager itemManager, DemonicPactsPlugin plugin, LeagueAreaManager areaManager)
    {
        this.client = client;
        this.config = config;
        this.tooltipManager = tooltipManager;
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.areaManager = areaManager;
        setPosition(OverlayPosition.TOOLTIP);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showTooltips())
        {
            return null;
        }

        MenuEntry[] menuEntries = client.getMenuEntries();
        if (menuEntries.length == 0)
        {
            return null;
        }

        // Check the top (hovered) entry first
        MenuEntry topEntry = menuEntries[menuEntries.length - 1];
        String topTarget = topEntry.getTarget().replaceAll("<[^>]*>", "").trim();

        // 1. Check NPC tasks via entry.getNpc()
        NPC npc = topEntry.getNpc();
        if (npc != null && npc.getName() != null)
        {
            List<DemonicPactsTask> tasks = TaskDatabase.findNpcTasks(npc.getName());
            if (!tasks.isEmpty())
            {
                String text = buildTooltipText(tasks, npc.getName());
                if (text != null)
                {
                    tooltipManager.add(new Tooltip(text));
                    return null;
                }
            }
        }

        // 2. Check top entry target against both databases
        if (!topTarget.isEmpty())
        {
            String text = tryBuildTooltip(topTarget, topEntry);
            if (text != null)
            {
                tooltipManager.add(new Tooltip(text));
                return null;
            }
        }

        // 3. If top entry didn't match (e.g. "Walk here" on stacked ground items),
        //    scan ALL menu entries for task-related items/NPCs
        for (int i = menuEntries.length - 2; i >= 0; i--)
        {
            MenuEntry entry = menuEntries[i];
            String entryTarget = entry.getTarget().replaceAll("<[^>]*>", "").trim();

            if (entryTarget.isEmpty())
            {
                continue;
            }

            // Check NPC
            NPC entryNpc = entry.getNpc();
            if (entryNpc != null && entryNpc.getName() != null)
            {
                List<DemonicPactsTask> tasks = TaskDatabase.findNpcTasks(entryNpc.getName());
                if (!tasks.isEmpty())
                {
                    String text = buildTooltipText(tasks, entryNpc.getName());
                    if (text != null)
                    {
                        tooltipManager.add(new Tooltip(text));
                        return null;
                    }
                }
            }

            // Check items
            String text = tryBuildTooltip(entryTarget, entry);
            if (text != null)
            {
                tooltipManager.add(new Tooltip(text));
                return null;
            }
        }

        return null;
    }

    /**
     * Try to build a tooltip for a given target name, checking item ID, item name, and NPC databases.
     */
    private String tryBuildTooltip(String cleanTarget, MenuEntry entry)
    {
        // Try item ID first (for inventory/bank items)
        MenuAction type = entry.getType();
        if (type == MenuAction.CC_OP || type == MenuAction.CC_OP_LOW_PRIORITY)
        {
            int itemId = entry.getItemId();
            if (itemId >= 0)
            {
                try
                {
                    String itemName = itemManager.getItemComposition(itemId).getName();
                    List<DemonicPactsTask> tasks = TaskDatabase.findItemTasks(itemName);
                    if (!tasks.isEmpty())
                    {
                        String text = buildTooltipText(tasks, itemName);
                        if (text != null) return text;
                    }
                }
                catch (Exception ignored) {}
            }
        }

        // Try ground item actions
        if (isGroundItemAction(entry))
        {
            List<DemonicPactsTask> tasks = TaskDatabase.findItemTasks(cleanTarget);
            if (!tasks.isEmpty())
            {
                String text = buildTooltipText(tasks, cleanTarget);
                if (text != null) return text;
            }
        }

        // Fallback: try clean target against all databases
        List<DemonicPactsTask> npcTasks = TaskDatabase.findNpcTasks(cleanTarget);
        if (!npcTasks.isEmpty())
        {
            String text = buildTooltipText(npcTasks, cleanTarget);
            if (text != null) return text;
        }

        List<DemonicPactsTask> itemTasks = TaskDatabase.findItemTasks(cleanTarget);
        if (!itemTasks.isEmpty())
        {
            String text = buildTooltipText(itemTasks, cleanTarget);
            if (text != null) return text;
        }

        List<DemonicPactsTask> objectTasks = TaskDatabase.findObjectTasks(cleanTarget);
        if (!objectTasks.isEmpty())
        {
            String text = buildTooltipText(objectTasks, cleanTarget);
            if (text != null) return text;
        }

        return null;
    }

    private boolean isGroundItemAction(MenuEntry entry)
    {
        MenuAction type = entry.getType();
        return type == MenuAction.GROUND_ITEM_FIRST_OPTION
            || type == MenuAction.GROUND_ITEM_SECOND_OPTION
            || type == MenuAction.GROUND_ITEM_THIRD_OPTION
            || type == MenuAction.GROUND_ITEM_FOURTH_OPTION
            || type == MenuAction.GROUND_ITEM_FIFTH_OPTION
            || type == MenuAction.EXAMINE_ITEM_GROUND;
    }

    private String buildTooltipText(List<DemonicPactsTask> tasks, String entityName)
    {
        // Filter out tasks not in the current region BEFORE building the string
        if (config.filterByCurrentRegion())
        {
            String currentArea = areaManager.getActiveArea();
            tasks = tasks.stream()
                .filter(t -> t.getArea().equalsIgnoreCase("General") || t.getArea().equalsIgnoreCase(currentArea))
                .collect(Collectors.toList());
        }

        // Filter out completed tasks if hideCompleted enabled
        CompletedTaskManager ctm = plugin.getCompletedTaskManager();
        if (config.hideCompleted() && !config.showCompletedInTooltip())
        {
            tasks = tasks.stream()
                .filter(t -> !ctm.isCompleted(t))
                .collect(Collectors.toList());
        }

        // If all tasks were filtered out (completed or wrong region), don't draw a tooltip
        if (tasks.isEmpty())
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<col=ffaa00>\u2694 Demonic Pacts League Task</col>");

        for (int i = 0; i < tasks.size(); i++)
        {
            DemonicPactsTask task = tasks.get(i);
            boolean completed = ctm.isCompleted(task);

            if (i > 0)
            {
                sb.append("<br>---");
            }

            String hexColor = String.format("%06x", task.getDifficulty().getColor().getRGB() & 0xFFFFFF);

            // Show checkmark or X for completion status
            String statusIcon = completed ? "\u2714 " : "";
            String strikePrefix = completed ? "<col=666666>" : "";
            String strikeSuffix = completed ? "</col>" : "";

            sb.append("<br>").append(strikePrefix)
                .append(statusIcon)
                .append("<col=").append(hexColor).append(">")
                .append("[").append(task.getDifficulty().name()).append("]</col> ")
                .append(task.getName())
                .append(strikeSuffix);

            if (completed)
            {
                sb.append("<br><col=00ff00>COMPLETED</col>");
                continue;
            }

            sb.append("<br><col=b0b0b0>").append(task.getDescription()).append("</col>");

            if (config.showPoints())
            {
                sb.append("<br><col=ffffff>Points: </col><col=00ff00>")
                    .append(task.getDifficulty().getPoints()).append("</col>");
            }

            if (config.showArea() && !task.getArea().isEmpty())
            {
                sb.append("<br><col=ffffff>Area: </col><col=66ccff>")
                    .append(task.getArea()).append("</col>");
            }

            if (config.showRequirements() && task.getRequirements() != null && !task.getRequirements().isEmpty())
            {
                sb.append("<br><col=ffffff>Requires: </col><col=ff6666>")
                    .append(task.getRequirements()).append("</col>");
            }

            if (task.getQuantity() > 1)
            {
                sb.append("<br><col=ffffff>Quantity: </col><col=ffff00>")
                    .append(task.getQuantity()).append("</col>");
            }
        }

        return sb.toString();
    }
}