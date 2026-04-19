package com.demonicpacts;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Alpha;

import java.awt.Color;

@ConfigGroup("demonicpactstaskhighlighter")
public interface DemonicPactsConfig extends Config
{
    @ConfigSection(
        name = "Highlights",
        description = "Configure what gets highlighted",
        position = 0
    )
    String highlightSection = "highlights";

    @ConfigSection(
        name = "Tooltips",
        description = "Configure tooltip display",
        position = 1
    )
    String tooltipSection = "tooltips";

    @ConfigSection(
        name = "Completion",
        description = "Configure task completion tracking",
        position = 2
    )
    String completionSection = "completion";

    @ConfigItem(
        keyName = "hideCompleted",
        name = "Hide Completed Tasks",
        description = "Stop highlighting NPCs/items once their tasks are marked complete",
        section = completionSection,
        position = 0
    )
    default boolean hideCompleted()
    {
        return true;
    }

    @ConfigItem(
        keyName = "autoDetectCompletion",
        name = "Auto-Detect Completion",
        description = "Automatically mark tasks complete when the league completion message appears in chat",
        section = completionSection,
        position = 1
    )
    default boolean autoDetectCompletion()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showCompletedInTooltip",
        name = "Show Completed In Tooltip",
        description = "Show a checkmark on completed tasks in tooltips instead of hiding them entirely",
        section = completionSection,
        position = 2
    )
    default boolean showCompletedInTooltip()
    {
        return false;
    }

    @ConfigItem(
        keyName = "showLoginHint",
        name = "Show Login Hint",
        description = "Show a one-time chat reminder on login to open the Leagues task menu for autocomplete",
        section = completionSection,
        position = 3
    )
    default boolean showLoginHint()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showSyncMessage",
            name = "Show Sync Message",
            description = "Show a chat message indicating how many new tasks were synced from the task log",
            section = completionSection,
            position = 4
    )
    default boolean showSyncMessage()
    {
        return true;
    }

    @ConfigItem(
        keyName = "highlightNpcs",
        name = "Highlight NPCs",
        description = "Highlight NPCs that are part of a league task",
        section = highlightSection,
        position = 0
    )
    default boolean highlightNpcs()
    {
        return true;
    }

    @ConfigItem(
        keyName = "highlightItems",
        name = "Highlight Items",
        description = "Highlight items in inventory/bank that are part of a league task",
        section = highlightSection,
        position = 1
    )
    default boolean highlightItems()
    {
        return true;
    }

    @ConfigItem(
        keyName = "highlightGroundItems",
        name = "Highlight Ground Items",
        description = "Highlight ground items that are part of a league task",
        section = highlightSection,
        position = 2
    )
    default boolean highlightGroundItems()
    {
        return true;
    }

    @ConfigItem(
        keyName = "highlightObjects",
        name = "Highlight World Objects",
        description = "Highlight mining rocks, trees, and other world objects related to league tasks",
        section = highlightSection,
        position = 3
    )
    default boolean highlightObjects()
    {
        return true;
    }

    @ConfigItem(
        keyName = "useDifficultyColors",
        name = "Use Difficulty Colors",
        description = "Color highlights based on task difficulty (Easy=Green, Medium=Orange, Hard=Red, Elite=Purple, Master=Cyan)",
        section = highlightSection,
        position = 4
    )
    default boolean useDifficultyColors()
    {
        return true;
    }

    @Alpha
    @ConfigItem(
        keyName = "defaultHighlightColor",
        name = "Default Highlight Color",
        description = "Color used when difficulty colors are disabled",
        section = highlightSection,
        position = 4
    )
    default Color defaultHighlightColor()
    {
        return new Color(255, 0, 255, 150);
    }

    @ConfigItem(
        keyName = "npcBorderWidth",
        name = "NPC Border Width",
        description = "Width of the highlight border on NPCs",
        section = highlightSection,
        position = 5
    )
    default int npcBorderWidth()
    {
        return 2;
    }

    @ConfigItem(
        keyName = "maxHighlightedObjects",
        name = "Max Highlighted Objects",
        description = "Limit world-object highlights to the closest N objects (trees, rocks, etc.) to reduce lag. Set to 0 to highlight all.",
        section = highlightSection,
        position = 6
    )
    default int maxHighlightedObjects()
    {
        return 10;
    }

    @ConfigItem(
        keyName = "showTooltips",
        name = "Show Tooltips",
        description = "Show task details when hovering over highlighted entities",
        section = tooltipSection,
        position = 0
    )
    default boolean showTooltips()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showRequirements",
        name = "Show Requirements",
        description = "Show task requirements in tooltips",
        section = tooltipSection,
        position = 1
    )
    default boolean showRequirements()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showPoints",
        name = "Show Points",
        description = "Show point values in tooltips",
        section = tooltipSection,
        position = 2
    )
    default boolean showPoints()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showArea",
        name = "Show Area",
        description = "Show task area in tooltips",
        section = tooltipSection,
        position = 3
    )
    default boolean showArea()
    {
        return true;
    }
}
