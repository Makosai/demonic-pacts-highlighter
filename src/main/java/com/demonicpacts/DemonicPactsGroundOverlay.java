package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class DemonicPactsGroundOverlay extends Overlay
{
    private final Client client;
    private final DemonicPactsConfig config;
    private final ItemManager itemManager;
    private final DemonicPactsPlugin plugin;

    @Inject
    DemonicPactsGroundOverlay(Client client, DemonicPactsConfig config, ItemManager itemManager, DemonicPactsPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.itemManager = itemManager;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightGroundItems())
        {
            return null;
        }

        Tile[][][] tiles = client.getScene().getTiles();
        int z = client.getPlane();

        for (int x = 0; x < tiles[z].length; x++)
        {
            for (int y = 0; y < tiles[z][x].length; y++)
            {
                Tile tile = tiles[z][x][y];
                if (tile == null)
                {
                    continue;
                }

                List<TileItem> groundItems = tile.getGroundItems();
                if (groundItems == null)
                {
                    continue;
                }

                for (TileItem item : groundItems)
                {
                    if (item == null)
                    {
                        continue;
                    }

                    String itemName = itemManager.getItemComposition(item.getId()).getName();
                    List<DemonicPactsTask> tasks = TaskDatabase.findItemTasks(itemName);

                    // Filter out completed tasks if enabled
                    if (config.hideCompleted() && !tasks.isEmpty())
                    {
                        tasks = plugin.getCompletedTaskManager().filterIncomplete(tasks);
                    }

                    if (!tasks.isEmpty())
                    {
                        DemonicPactsTask highestTask = getHighestDifficultyTask(tasks);
                        Color color = config.useDifficultyColors()
                            ? highestTask.getDifficulty().getColor()
                            : config.defaultHighlightColor();

                        LocalPoint lp = tile.getLocalLocation();
                        if (lp != null)
                        {
                            Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);
                            if (tilePoly != null)
                            {
                                Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 30);
                                graphics.setColor(fillColor);
                                graphics.fill(tilePoly);

                                graphics.setColor(color);
                                graphics.setStroke(new BasicStroke(1));
                                graphics.draw(tilePoly);
                            }

                            Point textLoc = Perspective.getCanvasTextLocation(client, graphics, lp,
                                "\u2694 " + itemName, 0);
                            if (textLoc != null)
                            {
                                OverlayUtil.renderTextLocation(graphics, textLoc, "\u2694 " + itemName, color);
                            }
                        }
                    }
                }
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
