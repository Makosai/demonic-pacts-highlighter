package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;

public class DemonicPactsObjectOverlay extends Overlay
{
    private final Client client;
    private final DemonicPactsConfig config;
    private final DemonicPactsPlugin plugin;

    @Inject
    DemonicPactsObjectOverlay(Client client, DemonicPactsConfig config, DemonicPactsPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(PRIORITY_MED);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.highlightObjects())
        {
            return null;
        }

        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
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

                for (GameObject gameObject : tile.getGameObjects())
                {
                    if (gameObject == null)
                    {
                        continue;
                    }

                    String objectName = getObjectName(gameObject);
                    if (objectName == null)
                    {
                        continue;
                    }

                    List<DemonicPactsTask> tasks = TaskDatabase.findObjectTasks(objectName);
                    if (tasks.isEmpty())
                    {
                        continue;
                    }

                    // Filter completed
                    if (config.hideCompleted())
                    {
                        tasks = plugin.getCompletedTaskManager().filterIncomplete(tasks);
                        if (tasks.isEmpty())
                        {
                            continue;
                        }
                    }

                    DemonicPactsTask highestTask = getHighestDifficultyTask(tasks);
                    Color color = config.useDifficultyColors()
                        ? highestTask.getDifficulty().getColor()
                        : config.defaultHighlightColor();

                    // Draw hull around the object
                    Shape hull = gameObject.getConvexHull();
                    if (hull != null)
                    {
                        Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 30);
                        graphics.setColor(fillColor);
                        graphics.fill(hull);

                        graphics.setColor(color);
                        graphics.setStroke(new BasicStroke(config.npcBorderWidth()));
                        graphics.draw(hull);
                    }

                    // Label above object
                    String label = "\u26CF " + highestTask.getDifficulty().name();
                    if (tasks.size() > 1)
                    {
                        label += " (+" + (tasks.size() - 1) + ")";
                    }

                    Point textLoc = gameObject.getCanvasTextLocation(graphics, label, 80);
                    if (textLoc != null)
                    {
                        OverlayUtil.renderTextLocation(graphics, textLoc, label, color);
                    }
                }
            }
        }

        return null;
    }

    private String getObjectName(GameObject gameObject)
    {
        try
        {
            ObjectComposition comp = client.getObjectDefinition(gameObject.getId());
            if (comp == null)
            {
                return null;
            }

            // Handle transformable objects (like depleted rocks)
            if (comp.getImpostorIds() != null)
            {
                ObjectComposition transformed = comp.getImpostor();
                if (transformed != null)
                {
                    String name = transformed.getName();
                    if (name != null && !name.equals("null") && !name.isEmpty())
                    {
                        return name;
                    }
                }
            }

            String name = comp.getName();
            if (name != null && !name.equals("null") && !name.isEmpty())
            {
                return name;
            }
        }
        catch (Exception ignored) {}

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
