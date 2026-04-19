package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class DemonicPactsNpcOverlay extends Overlay
{
    private final Client client;
    private final DemonicPactsConfig config;
    private final DemonicPactsPlugin plugin;
    private final LeagueAreaManager areaManager;

    @Inject
    DemonicPactsNpcOverlay(Client client, DemonicPactsConfig config, DemonicPactsPlugin plugin, LeagueAreaManager areaManager)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.areaManager = areaManager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightNpcs())
        {
            return null;
        }

        for (NPC npc : client.getNpcs())
        {
            if (npc == null || npc.getName() == null)
            {
                continue;
            }

            List<DemonicPactsTask> tasks = TaskDatabase.findNpcTasks(npc.getName());
            if (tasks.isEmpty())
            {
                continue;
            }

            // Filter out tasks not in the current region
            if (config.filterByCurrentRegion())
            {
                String currentArea = areaManager.getActiveArea();
                tasks = tasks.stream()
                        .filter(t -> t.getArea().equalsIgnoreCase("General") || t.getArea().equalsIgnoreCase(currentArea))
                        .collect(Collectors.toList());

                if (tasks.isEmpty())
                {
                    continue;
                }
            }

            // Filter out completed tasks if enabled
            if (config.hideCompleted())
            {
                tasks = plugin.getCompletedTaskManager().filterIncomplete(tasks);
                if (tasks.isEmpty())
                {
                    continue;
                }
            }

            // Use the highest-difficulty task's color
            DemonicPactsTask highestTask = getHighestDifficultyTask(tasks);
            Color color = config.useDifficultyColors()
                ? highestTask.getDifficulty().getColor()
                : config.defaultHighlightColor();

            Shape hull = npc.getConvexHull();
            if (hull != null)
            {
                // Draw filled hull with transparency
                Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 40);
                graphics.setColor(fillColor);
                graphics.fill(hull);

                // Draw border
                graphics.setColor(color);
                graphics.setStroke(new BasicStroke(config.npcBorderWidth()));
                graphics.draw(hull);
            }

            // Draw task icon/text above NPC
            String label = "\u2694 " + highestTask.getDifficulty().name();
            if (tasks.size() > 1)
            {
                label += " (+" + (tasks.size() - 1) + ")";
            }

            Point textLoc = npc.getCanvasTextLocation(graphics, label, npc.getLogicalHeight() + 40);
            if (textLoc != null)
            {
                OverlayUtil.renderTextLocation(graphics, textLoc, label, color);
            }
        }

        return null;
    }

    private DemonicPactsTask getHighestDifficultyTask(List<DemonicPactsTask> tasks)
    {
        DemonicPactsTask highest = tasks.get(0);
        for (DemonicPactsTask task : tasks)
        {
            if (task.getDifficulty().ordinal() > highest.getDifficulty().ordinal())
            {
                highest = task;
            }
        }
        return highest;
    }
}
