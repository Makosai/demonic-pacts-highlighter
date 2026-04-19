package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

public class DemonicPactsItemOverlay extends WidgetItemOverlay
{
    private final Client client;
    private final DemonicPactsConfig config;
    private final ItemManager itemManager;
    private final DemonicPactsPlugin plugin;
    private final LeagueAreaManager areaManager;

    @Inject
    DemonicPactsItemOverlay(Client client, DemonicPactsConfig config, ItemManager itemManager, DemonicPactsPlugin plugin, LeagueAreaManager areaManager)
    {
        this.client = client;
        this.config = config;
        this.itemManager = itemManager;
        this.plugin = plugin;
        this.areaManager = areaManager;
        showOnInventory();
        showOnBank();
        showOnEquipment();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
    {
        if (!config.highlightItems())
        {
            return;
        }

        String itemName = itemManager.getItemComposition(itemId).getName();
        List<DemonicPactsTask> tasks = TaskDatabase.findItemTasks(itemName);

        if (tasks.isEmpty())
        {
            return;
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
                return;
            }
        }

        // Filter out completed tasks if enabled
        if (config.hideCompleted())
        {
            tasks = plugin.getCompletedTaskManager().filterIncomplete(tasks);
            if (tasks.isEmpty())
            {
                return;
            }
        }

        DemonicPactsTask highestTask = getHighestDifficultyTask(tasks);
        Color color = config.useDifficultyColors()
            ? highestTask.getDifficulty().getColor()
            : config.defaultHighlightColor();

        Rectangle bounds = widgetItem.getCanvasBounds();
        if (bounds == null)
        {
            return;
        }

        // Draw a colored border around the item slot
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Draw a small colored dot in the corner to indicate task difficulty
        int dotSize = 8;
        graphics.setColor(color);
        graphics.fillOval(bounds.x + bounds.width - dotSize - 1, bounds.y + 1, dotSize, dotSize);

        // If multiple tasks, show count
        if (tasks.size() > 1)
        {
            graphics.setFont(graphics.getFont().deriveFont(Font.BOLD, 9f));
            graphics.setColor(Color.WHITE);
            String count = String.valueOf(tasks.size());
            graphics.drawString(count, bounds.x + 2, bounds.y + 10);
        }
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
