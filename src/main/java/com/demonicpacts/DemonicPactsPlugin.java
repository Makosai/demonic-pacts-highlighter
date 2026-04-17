package com.demonicpacts;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@PluginDescriptor(
    name = "Demonic Pacts Task Highlighter",
    description = "Highlights NPCs, items, and objects related to Demonic Pacts League tasks with tooltips showing task details",
    tags = {"league", "demonic", "pacts", "tasks", "highlight", "overlay"}
)
public class DemonicPactsPlugin extends Plugin
{
    private static final String MARK_COMPLETE = "Mark Task Complete";
    private static final String MARK_INCOMPLETE = "Unmark Task Complete";

    @Inject
    private Client client;

    @Inject
    private DemonicPactsConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private DemonicPactsNpcOverlay npcOverlay;

    @Inject
    private DemonicPactsItemOverlay itemOverlay;

    @Inject
    private DemonicPactsGroundOverlay groundOverlay;

    @Inject
    private DemonicPactsTooltipOverlay tooltipOverlay;

    @Inject
    private DemonicPactsObjectOverlay objectOverlay;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private EventBus eventBus;

    @Getter
    @Inject
    private CompletedTaskManager completedTaskManager;

    @Inject
    private LeagueTaskCompletionTracker taskTracker;

    @Override
    protected void startUp() throws Exception
    {
        log.debug("Demonic Pacts Task Highlighter started - loaded {} tasks", TaskDatabase.getAllTasks().size());
        overlayManager.add(npcOverlay);
        overlayManager.add(itemOverlay);
        overlayManager.add(groundOverlay);
        overlayManager.add(tooltipOverlay);
        overlayManager.add(objectOverlay);

        // Register the widget-based tracker to receive WidgetLoaded events
        eventBus.register(taskTracker);

        if (client.getGameState() == GameState.LOGGED_IN)
        {
            completedTaskManager.loadForCurrentProfile();
        }
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(npcOverlay);
        overlayManager.remove(itemOverlay);
        overlayManager.remove(groundOverlay);
        overlayManager.remove(tooltipOverlay);
        overlayManager.remove(objectOverlay);
        eventBus.unregister(taskTracker);
        taskTracker.clear();
        completedTaskManager.onLogout();
        log.debug("Demonic Pacts Task Highlighter stopped");
    }

    @Provides
    DemonicPactsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DemonicPactsConfig.class);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGGED_IN)
        {
            completedTaskManager.loadForCurrentProfile();
        }
        else if (event.getGameState() == GameState.LOGIN_SCREEN)
        {
            taskTracker.clear();
            completedTaskManager.onLogout();
        }
    }

    /**
     * Every game tick, sync widget tracker completions into CompletedTaskManager.
     * Also handles chat-based detection via NOTIFICATION_MAIN.
     */
    @Subscribe
    public void onGameTick(net.runelite.api.events.GameTick event)
    {
        if (!config.autoDetectCompletion() || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        // Sync any completions found by the widget tracker into our persistence layer
        for (String completed : taskTracker.getCompleted())
        {
            for (DemonicPactsTask task : TaskDatabase.getAllTasks())
            {
                if (task.getName().equalsIgnoreCase(completed))
                {
                    completedTaskManager.markCompleted(task.getName());
                }
            }
        }
    }

    /**
     * Auto-detect task completion from league chat messages.
     * Game format: "Congratulations, you've completed an easy task: TASK NAME"
     */
    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (!config.autoDetectCompletion())
        {
            return;
        }

        if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
        {
            return;
        }

        String message = Text.removeTags(event.getMessage());

        if ((message.contains("you've completed") || message.contains("you have completed")) && message.contains("task"))
        {
            int colonIdx = message.lastIndexOf(':');
            if (colonIdx >= 0 && colonIdx < message.length() - 1)
            {
                String taskName = message.substring(colonIdx + 1).trim();
                if (taskName.endsWith("."))
                {
                    taskName = taskName.substring(0, taskName.length() - 1).trim();
                }

                // Try exact match
                for (DemonicPactsTask task : TaskDatabase.getAllTasks())
                {
                    if (task.getName().equalsIgnoreCase(taskName))
                    {
                        completedTaskManager.markCompleted(task.getName());
                        taskTracker.addCompleted(task.getName());
                        log.debug("Chat-detected task completion: {}", task.getName());
                        return;
                    }
                }

                // Fuzzy match
                for (DemonicPactsTask task : TaskDatabase.getAllTasks())
                {
                    if (message.toLowerCase().contains(task.getName().toLowerCase()))
                    {
                        completedTaskManager.markCompleted(task.getName());
                        taskTracker.addCompleted(task.getName());
                        log.debug("Chat-detected task completion (fuzzy): {}", task.getName());
                        return;
                    }
                }
            }
        }
    }

    /**
     * Add right-click menu entries on NPCs/items that are task targets.
     */
    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event)
    {
        String target = Text.removeTags(event.getTarget()).trim();
        if (target.isEmpty())
        {
            return;
        }

        List<DemonicPactsTask> npcTasks = TaskDatabase.findNpcTasks(target);
        if (!npcTasks.isEmpty())
        {
            addTaskMenuEntries(npcTasks, event.getTarget());
            return;
        }

        List<DemonicPactsTask> itemTasks = TaskDatabase.findItemTasks(target);
        if (!itemTasks.isEmpty())
        {
            addTaskMenuEntries(itemTasks, event.getTarget());
            return;
        }

        List<DemonicPactsTask> objectTasks = TaskDatabase.findObjectTasks(target);
        if (!objectTasks.isEmpty())
        {
            addTaskMenuEntries(objectTasks, event.getTarget());
        }
    }

    private void addTaskMenuEntries(List<DemonicPactsTask> tasks, String target)
    {
        for (DemonicPactsTask task : tasks)
        {
            boolean completed = completedTaskManager.isCompleted(task)
                || taskTracker.isComplete(task.getName());
            String option = completed ? MARK_INCOMPLETE : MARK_COMPLETE;

            client.createMenuEntry(-1)
                .setOption(option)
                .setTarget(target)
                .setType(MenuAction.RUNELITE)
                .onClick(e -> {
                    completedTaskManager.toggleCompleted(task.getName());
                });
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        // Handled by the onClick lambda above
    }
}
