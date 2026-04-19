package com.demonicpacts;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Reads task completion status from the League Tasks panel (widget 657).
 * When the player opens the task log, child 18 = task names, child 19 = status with color.
 * Color 0xF47113 (orange) = complete, 0x9F9F9F (grey) = incomplete.
 */
@Slf4j
@Singleton
public class LeagueTaskCompletionTracker
{
    private static final int TASK_LOG_GROUP_ID = 657;
    private static final int NAME_COLUMN_CHILD = 18;
    private static final int STATUS_COLUMN_CHILD = 19;
    private static final int COLOR_COMPLETE = 0xF47113;

    private final Set<String> completedTaskNames = new HashSet<>();

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private DemonicPactsConfig config;

    @Subscribe
    public void onGameTick(GameTick event)
    {
        // Continuously scan while the interface is actively open.
        // This catches tasks that appear when the user scrolls or changes filters.
        Widget nameCol = client.getWidget(TASK_LOG_GROUP_ID, NAME_COLUMN_CHILD);
        if (nameCol != null && !nameCol.isHidden())
        {
            syncFromTaskLog();
        }
    }

    private void syncFromTaskLog()
    {
        Widget nameCol = client.getWidget(TASK_LOG_GROUP_ID, NAME_COLUMN_CHILD);
        Widget statusCol = client.getWidget(TASK_LOG_GROUP_ID, STATUS_COLUMN_CHILD);
        if (nameCol == null || statusCol == null)
        {
            return;
        }

        Widget[] names = nameCol.getDynamicChildren();
        Widget[] statuses = statusCol.getDynamicChildren();
        if (names == null || statuses == null)
        {
            return;
        }

        int count = Math.min(names.length, statuses.length);
        int newlyFound = 0;
        for (int i = 0; i < count; i++)
        {
            if (statuses[i].getTextColor() == COLOR_COMPLETE)
            {
                String rawName = names[i].getText();
                if (rawName != null && !rawName.isEmpty())
                {
                    String cleanName = Text.removeTags(rawName).replace('\u00A0', ' ').trim();
                    if (completedTaskNames.add(cleanName))
                    {
                        newlyFound++;
                    }
                }
            }
        }

        // Only log if we actually found new tasks this tick to prevent chat spam
        if (newlyFound > 0)
        {
            log.debug("Task log synced. Total complete: {} (new this pass: {})",
                    completedTaskNames.size(), newlyFound);

            if (config.showSyncMessage())
            {
                String message = new ChatMessageBuilder()
                        .append(Color.MAGENTA, "[Demonic Pacts] ")
                        .append(Color.WHITE, "Synced " + newlyFound + " new tasks.")
                        .build();

                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
            }
        }
    }

    /**
     * Check if a task is completed (case-insensitive).
     */
    public boolean isComplete(String taskName)
    {
        if (taskName == null)
        {
            return false;
        }
        // Check exact match first
        if (completedTaskNames.contains(taskName))
        {
            return true;
        }
        // Case-insensitive fallback
        for (String completed : completedTaskNames)
        {
            if (completed.equalsIgnoreCase(taskName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all completed task names.
     */
    public Set<String> getCompleted()
    {
        return completedTaskNames;
    }

    /**
     * Add a task as completed (e.g. from notification detection).
     */
    public void addCompleted(String taskName)
    {
        if (taskName != null)
        {
            completedTaskNames.add(taskName.trim());
        }
    }

    /**
     * Clear on logout.
     */
    public void clear()
    {
        completedTaskNames.clear();
    }

    public int getCompletedCount()
    {
        return completedTaskNames.size();
    }
}
