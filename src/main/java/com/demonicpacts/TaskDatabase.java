package com.demonicpacts;

import java.util.*;
import java.util.stream.Collectors;

public class TaskDatabase
{
    private static final List<DemonicPactsTask> ALL_TASKS = new ArrayList<>();

    // Reverse lookup maps built at init
    private static final Map<String, List<DemonicPactsTask>> NPC_TASKS = new HashMap<>();
    private static final Map<String, List<DemonicPactsTask>> ITEM_TASKS = new HashMap<>();
    private static final Map<String, List<DemonicPactsTask>> OBJECT_TASKS = new HashMap<>();

    /**
     * Maps world object names (rocks, trees, fishing spots, patches) to their
     * corresponding item-based task keywords so we can highlight the source objects.
     */
    private static final Map<String, String[]> OBJECT_TO_TASK_KEYWORDS = new HashMap<>();
    static
    {
        // Mining rocks -> ore item keywords
        OBJECT_TO_TASK_KEYWORDS.put("clay rocks", new String[]{"clay"});
        OBJECT_TO_TASK_KEYWORDS.put("tin rocks", new String[]{"tin ore"});
        OBJECT_TO_TASK_KEYWORDS.put("copper rocks", new String[]{"copper ore"});
        OBJECT_TO_TASK_KEYWORDS.put("iron rocks", new String[]{"iron ore"});
        OBJECT_TO_TASK_KEYWORDS.put("silver rocks", new String[]{"silver ore"});
        OBJECT_TO_TASK_KEYWORDS.put("coal rocks", new String[]{"coal"});
        OBJECT_TO_TASK_KEYWORDS.put("gold rocks", new String[]{"gold ore"});
        OBJECT_TO_TASK_KEYWORDS.put("mithril rocks", new String[]{"mithril ore"});
        OBJECT_TO_TASK_KEYWORDS.put("adamantite rocks", new String[]{"adamantite ore"});
        OBJECT_TO_TASK_KEYWORDS.put("runite rocks", new String[]{"runite ore"});

        // Trees -> log item keywords
        OBJECT_TO_TASK_KEYWORDS.put("tree", new String[]{"logs"});
        OBJECT_TO_TASK_KEYWORDS.put("oak tree", new String[]{"oak logs"});
        OBJECT_TO_TASK_KEYWORDS.put("oak", new String[]{"oak logs"});
        OBJECT_TO_TASK_KEYWORDS.put("willow tree", new String[]{"willow logs"});
        OBJECT_TO_TASK_KEYWORDS.put("willow", new String[]{"willow logs"});
        OBJECT_TO_TASK_KEYWORDS.put("maple tree", new String[]{"maple logs"});
        OBJECT_TO_TASK_KEYWORDS.put("yew tree", new String[]{"yew logs"});
        OBJECT_TO_TASK_KEYWORDS.put("yew", new String[]{"yew logs"});
        OBJECT_TO_TASK_KEYWORDS.put("magic tree", new String[]{"magic logs"});

        // Farming patches
        OBJECT_TO_TASK_KEYWORDS.put("allotment", new String[]{"allotment"});
        OBJECT_TO_TASK_KEYWORDS.put("flower patch", new String[]{"flower patch"});
    }

    static
    {
        // =====================================================================
        // EASY TASKS (10 pts)
        // =====================================================================
        // -- Defeat NPCs --
        defeatNpc("Defeat a Troll in Asgarnia", "Defeat a Troll in Asgarnia. Earns one Demonic Pact.", "Asgarnia", TaskDifficulty.EASY, "", "Troll");
        defeatNpc("Defeat a Hill Giant", "Defeat a Hill Giant. Earns one Demonic Pact.", "General", TaskDifficulty.EASY, "", "Hill Giant");
        defeatNpc("Defeat a Chicken", "Defeat a Chicken.", "General", TaskDifficulty.EASY, "", "Chicken");
        defeatNpc("Defeat a Frog", "Defeat a Frog.", "General", TaskDifficulty.EASY, "", "Frog");
        defeatNpc("Defeat a Rat", "Defeat a Rat.", "General", TaskDifficulty.EASY, "", "Rat");
        defeatNpc("Defeat a scorpion", "Defeat a Scorpion.", "General", TaskDifficulty.EASY, "", "Scorpion");
        defeatNpc("Defeat an Imp with an earth spell", "Defeat an Imp with an earth spell.", "General", TaskDifficulty.EASY, "Magic 9", "Imp");
        defeatNpc("Defeat a Cockatrice in the Fremennik Province", "Defeat a Cockatrice in the Fremennik Province. Earns one Demonic Pact.", "Fremennik", TaskDifficulty.EASY, "Slayer 25", "Cockatrice");
        defeatNpc("Defeat a Werewolf in Morytania", "Defeat a Werewolf in Morytania. Earns one Demonic Pact.", "Morytania", TaskDifficulty.EASY, "", "Werewolf");
        defeatNpc("Defeat a Chaos Dwarf in the Wilderness", "Defeat a Chaos Dwarf in the Wilderness. Earns one Demonic Pact.", "Wilderness", TaskDifficulty.EASY, "", "Chaos Dwarf");
        defeatNpc("Set a Mummy ablaze", "Set a Mummy on Fire with fire damage. Earns one Demonic Pact.", "Desert", TaskDifficulty.EASY, "Magic 13 or Tirannwn for oily cloth", "Mummy");

        // -- Equip Items --
        equipItem("Equip an Elemental Staff", "Equip a basic elemental staff.", "General", TaskDifficulty.EASY, "",
            "Staff of water", "Staff of fire", "Staff of earth", "Staff of air");
        equipItem("Equip an Iron dagger", "Equip an Iron dagger.", "General", TaskDifficulty.EASY, "", "Iron dagger");
        equipItem("Equip a Tyras helm", "Equip a Tyras helm.", "General", TaskDifficulty.EASY, "Defence 5", "Tyras helm");

        // -- Skilling --
        addTask("Achieve Your First Level Up", "Level up any of your skills for the first time.", "General", TaskDifficulty.EASY, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 5", "Reach level 5 in any skill.", "General", TaskDifficulty.EASY, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 10", "Reach level 10 in any skill.", "General", TaskDifficulty.EASY, TaskType.SKILL_LEVEL, "", 1);
        addTask("Reach Combat Level 25", "Reach Combat Level 25.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);

        // -- Fishing --
        catchItem("Catch a Shrimp", "Catch Raw Shrimp while Fishing.", "General", TaskDifficulty.EASY, "", "Raw shrimps");
        catchItem("Catch a Herring", "Catch a Raw Herring whilst Fishing.", "General", TaskDifficulty.EASY, "Fishing 10", "Raw herring");
        catchItem("Catch an Anchovy", "Catch a Raw Anchovy whilst Fishing.", "General", TaskDifficulty.EASY, "Fishing 15", "Raw anchovies");

        // -- Mining --
        mineItem("Mine 5 Tin Ore", "Mine 5 Tin Ore.", "General", TaskDifficulty.EASY, "", 5, "Tin ore");
        mineItem("Mine some Clay", "Mine some Clay.", "General", TaskDifficulty.EASY, "", 1, "Clay");

        // -- Woodcutting --
        chopItem("Chop Some Logs", "Chop any kind of logs.", "General", TaskDifficulty.EASY, "", 1, "Logs", "Oak logs", "Willow logs", "Maple logs", "Yew logs", "Magic logs");

        // -- Cooking --
        cookItem("Cook Shrimp", "Cook Raw Shrimp.", "General", TaskDifficulty.EASY, "", 1, "Raw shrimps");
        addTask("Burn Some Food", "Burn any kind of food while trying to cook it.", "General", TaskDifficulty.EASY, TaskType.COOK_ITEM, "", 1);

        // -- Firemaking --
        burnItem("Burn Some Normal Logs", "Burn some Normal Logs.", "General", TaskDifficulty.EASY, "", 1, "Logs");
        burnItem("Burn Some Oak Logs", "Burn some Oak Logs.", "General", TaskDifficulty.EASY, "Firemaking 15", 1, "Oak logs");

        // -- Herblore --
        cleanItem("Clean a Grimy Guam", "Clean a Grimy Guam.", "General", TaskDifficulty.EASY, "", 1, "Grimy guam leaf");
        cleanItem("Clean 25 Grimy Guam Leafs", "Clean 25 Grimy Guam Leafs.", "General", TaskDifficulty.EASY, "", 25, "Grimy guam leaf");
        cleanItem("Clean 15 Grimy Tarromin", "Clean 15 Grimy Tarromin.", "General", TaskDifficulty.EASY, "Herblore 11", 15, "Grimy tarromin");

        // -- Crafting --
        craftItem("Craft Leather chaps", "Craft Leather chaps.", "General", TaskDifficulty.EASY, "Crafting 18", "Leather chaps");
        craftItem("Cut a Ruby", "Cut a Ruby.", "General", TaskDifficulty.EASY, "Crafting 34", "Ruby");
        craftItem("Successfully Cut a Red Topaz", "Successfully Cut a Red Topaz.", "General", TaskDifficulty.EASY, "Crafting 16", "Red topaz");

        // -- Fletching --
        craftItem("Fletch Some Arrow Shafts", "Fletch some Arrow Shafts.", "General", TaskDifficulty.EASY, "", "Arrow shaft");
        craftItem("Fletch an Oak Shortbow", "Fletch an Oak Shortbow.", "General", TaskDifficulty.EASY, "Fletching 20", "Oak shortbow");

        // -- Herblore potions --
        craftItem("Make an Attack Potion", "Make an Attack Potion.", "General", TaskDifficulty.EASY, "", "Attack potion");
        craftItem("Create an Antipoison", "Create an Antipoison.", "General", TaskDifficulty.EASY, "Herblore 5", "Antipoison");

        // -- Smithing --
        craftItem("Smelt a Bronze Bar", "Use a Furnace to smelt a Bronze Bar.", "General", TaskDifficulty.EASY, "", "Bronze bar");
        craftItem("Smelt an Iron Bar", "Use a Furnace to smelt an Iron Bar.", "General", TaskDifficulty.EASY, "Smithing 15", "Iron bar");

        // -- Thieving --
        addTask("Pickpocket a Citizen", "Pickpocket a Man or a Woman.", "General", TaskDifficulty.EASY, TaskType.DEFEAT_NPC, "", 1, "Man", "Woman");

        // -- Hunter --
        catchItem("Catch a Baby Impling", "Catch a Baby Impling.", "General", TaskDifficulty.EASY, "Hunter 17", "Baby impling");
        catchItem("Snare a Bird", "Catch any bird with a Bird Snare.", "General", TaskDifficulty.EASY, "", "Crimson swift", "Golden warbler", "Copper longtail", "Cerulean twitch", "Tropical wagtail");
        catchItem("Snare 5 Crimson Swifts", "Snare 5 Crimson Swifts.", "General", TaskDifficulty.EASY, "", 5, "Crimson swift");
        catchItem("Snare 15 Tropical Wagtails", "Snare 15 Tropical Wagtails.", "General", TaskDifficulty.EASY, "Hunter 19", 15, "Tropical wagtail");

        // -- Prayer --
        addTask("Bury 6 bones", "Bury 6 bones of any kind.", "General", TaskDifficulty.EASY, TaskType.PRAYER, "", 6, "Bones", "Big bones", "Dragon bones");

        // -- Misc --
        addTask("Complete the Leagues Tutorial", "Complete the Leagues Tutorial and begin your adventure.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Open the Leagues Menu", "Open the Leagues Menu found within the Journal Panel.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Cast Home Teleport", "Cast the Home Teleport spell.", "General", TaskDifficulty.EASY, TaskType.SPELL, "", 1);
        addTask("Perform a Special Attack", "Perform any special attack.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Enter your Player Owned House", "Enter your Player Owned House.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Purchase a Player Owned House", "Purchase a Player Owned House.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Spin a Ball of Wool", "Use a Spinning Wheel to spin a Ball of Wool.", "General", TaskDifficulty.EASY, TaskType.CRAFT_ITEM, "", 1, "Ball of wool");
        addTask("Drink a Strength Potion", "Drink a Strength Potion.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Strength potion");
        addTask("Eat a Rabbit", "Eat a cooked rabbit.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Cooked rabbit");
        addTask("Eat an Onion", "Eat an Onion, raw.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Onion");
        addTask("Light a Torch", "Light a Torch.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Torch");
        addTask("Dye a cape Purple", "Dye a cape Purple.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Purple dye");
        addTask("Cook something with an apron", "Cook something with an apron equipped.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Apron");
        addTask("Feed a dog some bones", "Feed a dog some bones.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Bones");
        addTask("Sell some silk to a silk trader", "Sell some silk to a silk trader.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Silk");
        addTask("Get a haircut", "Go and get a haircut.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Turn off your run", "Turn off your run.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Activate a prayer near an altar", "Activate a prayer near an altar.", "General", TaskDifficulty.EASY, TaskType.PRAYER, "", 1);
        addTask("Attack a dummy", "Attack a dummy.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Dummy");
        addTask("Plant Seeds in an Allotment Patch", "Plant some seeds in an Allotment patch.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Protect Your Crops", "Pay a farmer to protect any of your crops.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Rake a Flower Patch", "Rake a Flower Patch.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Obtain a Bird Nest", "Obtain a Bird Nest whilst cutting down trees.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Bird nest");
        addTask("Obtain a Casket from Fishing", "Obtain a Casket from Fishing.", "General", TaskDifficulty.EASY, TaskType.MISC, "Fishing 16", 1, "Casket");
        addTask("Shoot 6 iron arrows", "Shoot 6 iron arrows.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 6, "Iron arrow");
        addTask("Pick 6 flax", "Pick 6 flax.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 6, "Flax");
        addTask("Turn any Logs Into a Plank", "Use a Sawmill to turn Logs into a Plank.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1, "Plank");
        addTask("Talk to any Port master", "Talk to any Port master.", "General", TaskDifficulty.EASY, TaskType.MISC, "", 1);
        addTask("Steal some bread", "Steal some bread from a Bakery Stall.", "General", TaskDifficulty.EASY, TaskType.MISC, "Thieving 5", 1);
        addTask("Defeat 8 penguins within 5 seconds", "Defeat 8 penguins within 5 seconds.", "General", TaskDifficulty.EASY, TaskType.DEFEAT_NPC, "", 8, "Penguin");

        // Kourend easy
        addTask("Open 1 Grubby Chest", "Open the grubby chest in the Forthos Dungeon. Earns one Demonic Pact.", "Kourend", TaskDifficulty.EASY, TaskType.MISC, "Thieving 57", 1, "Grubby chest");

        // =====================================================================
        // MEDIUM TASKS (30 pts)
        // =====================================================================
        addTask("Achieve Your First Level 20", "Reach level 20 in any skill.", "General", TaskDifficulty.MEDIUM, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 30", "Reach level 30 in any skill.", "General", TaskDifficulty.MEDIUM, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 40", "Reach level 40 in any skill.", "General", TaskDifficulty.MEDIUM, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 50", "Reach level 50 in any skill.", "General", TaskDifficulty.MEDIUM, TaskType.SKILL_LEVEL, "", 1);
        addTask("Achieve Your First Level 60", "Reach level 60 in any skill.", "General", TaskDifficulty.MEDIUM, TaskType.SKILL_LEVEL, "", 1);

        // -- Prayers --
        addTask("Use the Protect from Melee Prayer", "Use the Protect from Melee Prayer.", "General", TaskDifficulty.MEDIUM, TaskType.PRAYER, "Prayer 43", 1);
        addTask("Activate Smite", "Activate Smite in your prayer book.", "General", TaskDifficulty.MEDIUM, TaskType.PRAYER, "Prayer 52", 1);

        // -- Clue scrolls --
        addTask("1 Easy Clue Scroll", "Open a Reward casket for completing an Easy clue scroll.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("1 Medium Clue Scroll", "Open a Reward casket for completing a Medium clue scroll.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("1 Hard Clue Scroll", "Open a Reward casket for completing a Hard clue scroll.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("1 Elite Clue Scroll", "Open a Reward casket for completing an Elite clue scroll.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("25 Easy Clue Scrolls", "Open 25 Reward caskets for completing Easy clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 25);
        addTask("25 Medium Clue Scrolls", "Open 25 Reward caskets for completing Medium clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 25);
        addTask("25 Hard Clue Scrolls", "Open 25 Reward caskets for completing Hard clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 25);
        addTask("25 Elite Clue Scrolls", "Open 25 Reward caskets for completing Elite clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 25);
        addTask("75 Easy Clue Scrolls", "Open 75 Reward caskets for completing Easy clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 75);
        addTask("75 Medium Clue Scrolls", "Open 75 Reward caskets for completing Medium clue scrolls.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 75);

        // -- Collection log --
        addTask("5 Collection log slots", "Obtain 5 unique Collection Log slots.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 5);
        addTask("15 Collection log slots", "Obtain 15 unique Collection Log slots.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 15);
        addTask("30 Collection log slots", "Obtain 30 unique Collection Log slots.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 30);
        addTask("50 Collection log slots", "Obtain 50 unique Collection Log slots.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 50);

        // -- Slayer --
        addTask("Complete 1 Slayer Task", "Complete 1 Slayer Task.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        defeatNpc("Defeat a Superior slayer creature", "Defeat a Superior slayer creature.", "General", TaskDifficulty.MEDIUM, "Slayer 5 + Bigger and Badder or Tier 3 Relic", "Superior");
        defeatNpc("Defeat 25 Superior slayer creatures", "Defeat 25 Superior slayer monsters.", "General", TaskDifficulty.MEDIUM, "Slayer 5 + Bigger and Badder or Tier 3 Relic", "Superior");
        defeatNpc("Defeat 75 Superior slayer creatures", "Defeat 75 Superior slayer monsters.", "General", TaskDifficulty.MEDIUM, "Slayer 5 + Bigger and Badder or Tier 3 Relic", "Superior");

        // -- Defeat NPCs --
        defeatNpc("Defeat a Steel Dragon on Karamja", "Defeat a Steel Dragon on Karamja.", "Karamja", TaskDifficulty.MEDIUM, "", "Steel dragon");
        defeatNpc("Defeat 150 Lizardmen Shaman", "Help the Shayzien House by killing 150 Lizardmen shamans.", "General", TaskDifficulty.MEDIUM, "", "Lizardman shaman");
        defeatNpc("Defeat a Black Dragon in Tirannwn", "Defeat a Black Dragon in Tirannwn.", "Tirannwn", TaskDifficulty.MEDIUM, "", "Black dragon");

        // -- Fishing --
        catchItem("Catch 10 Cod", "Catch 10 Cod.", "General", TaskDifficulty.MEDIUM, "Fishing 23", 10, "Raw cod");
        catchItem("Catch 20 mackerel", "Catch 20 Raw mackerel whilst Fishing.", "General", TaskDifficulty.MEDIUM, "Fishing 16", 20, "Raw mackerel");
        catchItem("Catch 50 Tuna", "Catch 50 Tuna.", "General", TaskDifficulty.MEDIUM, "Fishing 35", 50, "Raw tuna");
        catchItem("Catch 75 Trout", "Catch 75 Raw Trout whilst Fishing.", "General", TaskDifficulty.MEDIUM, "Fishing 20", 75, "Raw trout");
        catchItem("Catch 75 Lobsters", "Catch 75 Lobsters.", "General", TaskDifficulty.MEDIUM, "Fishing 40", 75, "Raw lobster");
        catchItem("Catch 100 Tuna", "Catch 100 Tuna.", "General", TaskDifficulty.MEDIUM, "Fishing 35", 100, "Raw tuna");
        catchItem("Catch 100 Swordfish", "Catch 100 Swordfish.", "General", TaskDifficulty.MEDIUM, "Fishing 50", 100, "Raw swordfish");

        // -- Cooking --
        cookItem("Cook 50 Tuna", "Cook 50 Raw Tuna.", "General", TaskDifficulty.MEDIUM, "Cooking 35", 50, "Raw tuna");
        cookItem("Cook 100 Swordfish", "Cook 100 Swordfish.", "General", TaskDifficulty.MEDIUM, "Cooking 45", 100, "Raw swordfish");
        addTask("Butter a potato", "Make a potato with butter.", "General", TaskDifficulty.MEDIUM, TaskType.COOK_ITEM, "Cooking 39", 1, "Potato with butter");

        // -- Woodcutting --
        chopItem("Chop 100 Willow Logs", "Chop 100 Willow Logs from Willow Trees.", "General", TaskDifficulty.MEDIUM, "Woodcutting 30", 100, "Willow logs");
        chopItem("Chop 50 Maple Logs", "Chop 50 Maple Logs.", "General", TaskDifficulty.MEDIUM, "Woodcutting 45", 50, "Maple logs");
        addTask("Chop some Rising Roots", "Chop some Rising Roots spawned via Forestry.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);

        // -- Firemaking --
        burnItem("Burn 100 Willow Logs", "Burn 100 Willow Logs.", "General", TaskDifficulty.MEDIUM, "Firemaking 30", 100, "Willow logs");
        burnItem("Burn 25 Maple Logs", "Burn 25 Maple Logs.", "General", TaskDifficulty.MEDIUM, "Firemaking 45", 25, "Maple logs");
        addTask("Burn Some Coloured Logs", "Burn some logs that have been coloured with a firelighter.", "General", TaskDifficulty.MEDIUM, TaskType.BURN_ITEM, "", 1, "Firelighter");

        // -- Herblore --
        cleanItem("Clean 50 Grimy Ranarr Weed", "Clean 50 Grimy Ranarr Weed.", "General", TaskDifficulty.MEDIUM, "Herblore 25", 50, "Grimy ranarr weed");
        cleanItem("Clean 50 Grimy Cadantine", "Clean 50 Grimy Cadantine.", "General", TaskDifficulty.MEDIUM, "Herblore 65", 50, "Grimy cadantine");
        cleanItem("Clean a Grimy Avantoe", "Clean a Grimy Avantoe.", "General", TaskDifficulty.MEDIUM, "Herblore 48", 1, "Grimy avantoe");

        // -- Magic --
        addTask("Cast an Earth Blast Spell", "Cast an Earth Blast Spell.", "General", TaskDifficulty.MEDIUM, TaskType.SPELL, "Magic 53", 1);
        addTask("Cast Low Level Alchemy", "Cast the Low Level Alchemy spell.", "General", TaskDifficulty.MEDIUM, TaskType.SPELL, "Magic 21", 1);
        addTask("Convert an item into at least 500 coins", "Cast High Level Alchemy to convert an item into 500+ coins.", "General", TaskDifficulty.MEDIUM, TaskType.SPELL, "Magic 55", 1);

        // -- Crafting --
        craftItem("Craft 200 Essence Into Runes", "Use Runecrafting Altars to craft 200 essence into runes.", "General", TaskDifficulty.MEDIUM, "", "Rune essence", "Pure essence");
        craftItem("Craft a Sapphire Amulet", "Craft a Sapphire amulet.", "General", TaskDifficulty.MEDIUM, "Crafting 24", "Sapphire amulet");
        craftItem("Craft an Emerald Ring", "Craft an Emerald Ring.", "General", TaskDifficulty.MEDIUM, "Crafting 27", "Emerald ring");
        addTask("Craft 20 Silver items", "Craft 20 Silver items using Silver bars.", "General", TaskDifficulty.MEDIUM, TaskType.CRAFT_ITEM, "", 20, "Silver bar");
        addTask("Craft Any Combination Rune", "Use a Runecrafting Altar to craft any type of combination rune.", "General", TaskDifficulty.MEDIUM, TaskType.CRAFT_ITEM, "Runecraft 6+", 1, "Mist rune", "Dust rune", "Mud rune", "Smoke rune", "Steam rune", "Lava rune");

        // -- Equip sets --
        equipItem("Equip a Full Bronze Set", "Equip a Bronze Platebody, Bronze Full Helm and Bronze Platelegs/Plateskirt.", "General", TaskDifficulty.MEDIUM, "",
            "Bronze platebody", "Bronze full helm", "Bronze platelegs", "Bronze plateskirt");
        equipItem("Equip a Full Adamant Set", "Equip an Adamant Platebody, Adamant Full Helm and Adamant Platelegs/Plateskirt.", "General", TaskDifficulty.MEDIUM, "Defence 30",
            "Adamant platebody", "Adamant full helm", "Adamant platelegs", "Adamant plateskirt");
        equipItem("Equip a Full Blue Dragonhide Set", "Equip Blue d'hide body, chaps, and vambraces.", "General", TaskDifficulty.MEDIUM, "Ranged 50, Defence 40",
            "Blue d'hide body", "Blue d'hide chaps", "Blue d'hide vambs");
        equipItem("Equip a Full Red Dragonhide Set", "Equip Red d'hide body, chaps, and vambraces.", "General", TaskDifficulty.MEDIUM, "Ranged 60, Defence 40",
            "Red d'hide body", "Red d'hide chaps", "Red d'hide vambs");
        equipItem("Equip a Mithril Weapon", "Equip any Mithril weapon.", "General", TaskDifficulty.MEDIUM, "Attack 20",
            "Mithril scimitar", "Mithril sword", "Mithril longsword", "Mithril battleaxe", "Mithril mace", "Mithril dagger", "Mithril warhammer");

        // -- Hunter --
        catchItem("Catch a Butterfly", "Catch any butterfly.", "General", TaskDifficulty.MEDIUM, "Hunter 15", "Ruby harvest", "Sapphire glacialis", "Snowy knight", "Black warlock");
        catchItem("Catch a Swamp Lizard or Salamander", "Catch either a Swamp Lizard or any kind of Salamander.", "General", TaskDifficulty.MEDIUM, "Hunter 29+", "Swamp lizard", "Orange salamander", "Red salamander", "Black salamander");
        catchItem("Catch 50 Implings in Puro-Puro", "Catch 50 Implings in Puro-Puro.", "General", TaskDifficulty.MEDIUM, "Hunter 17", 50, "Baby impling", "Young impling", "Gourmet impling", "Earth impling", "Essence impling", "Eclectic impling");

        // -- Farming --
        addTask("Check a grown Tree", "Check the health of any regular Tree you've grown.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "Farming 15", 1);
        addTask("Check a grown Fruit Tree", "Check the health of any Fruit Tree you've grown.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "Farming 27", 1);
        addTask("Churn some butter", "Use a churn to make some butter.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "Cooking 38", 1, "Butter");

        // -- Construction --
        addTask("Build a Room in Your Player Owned House", "Build a room in your Player Owned House.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Build an Oak Larder", "Build an Oak Larder in a Kitchen.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "Construction 33", 1);
        addTask("Build a Mahogany Portal", "Build a Mahogany Portal in a Portal Chamber.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "Construction 65", 1);

        // -- Prayer --
        addTask("Bury Some Wyvern or Dragon Bones", "Bury either some Wyvern Bones or some Dragon Bones.", "General", TaskDifficulty.MEDIUM, TaskType.PRAYER, "", 1, "Wyvern bones", "Dragon bones");
        addTask("Eat some Purple Sweets", "Eat some Purple Sweets.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1, "Purple sweets");

        // -- Random events / Forestry --
        addTask("Complete the Evil Bob random event", "Complete the Evil Bob random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Maze random event", "Complete the Maze random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Pillory random event", "Complete the Pillory random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Pinball random event", "Complete the Pinball random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Postie Pete random event", "Complete the Postie Pete random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Prison Pete random event", "Complete the Prison Pete random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Surprise Exam random event", "Complete the Surprise Exam random event.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Flowering Bush event", "Complete the Flowering Bush event spawned via Forestry.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Pheasant Forestry Event", "Complete the Pheasant event spawned via Forestry.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Ritual Forestry Event", "Complete the Ritual event spawned via Forestry.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);
        addTask("Complete the Struggling Sapling event", "Complete the Struggling Sapling event spawned via Forestry.", "General", TaskDifficulty.MEDIUM, TaskType.MISC, "", 1);

        // =====================================================================
        // HARD TASKS (80 pts)
        // =====================================================================
        defeatNpc("Defeat a Demonic Gorilla", "Defeat a Demonic Gorilla in the Crash Site Cavern.", "General", TaskDifficulty.HARD, "", "Demonic gorilla");
        defeatNpc("Defeat a Mithril Dragon", "Defeat a Mithril Dragon in the Ancient Cavern.", "General", TaskDifficulty.HARD, "", "Mithril dragon");
        defeatNpc("Defeat a Basilisk Knight", "Defeat a Basilisk Knight in Jormungand's Prison.", "Fremennik", TaskDifficulty.HARD, "Slayer 60 + The Fremennik Exiles", "Basilisk Knight");
        defeatNpc("Defeat Phantom Muspah", "Defeat Phantom Muspah.", "General", TaskDifficulty.HARD, "", "Phantom Muspah");
        defeatNpc("Defeat the Kraken Boss 50 Times", "Defeat the Kraken boss in Kraken Cove 50 times.", "General", TaskDifficulty.HARD, "Slayer 87", "Kraken");
        defeatNpc("Defeat Skotizo 1 Time", "Defeat Skotizo beneath the Catacombs of Kourend.", "Kourend", TaskDifficulty.HARD, "", "Skotizo");
        defeatNpc("Defeat Araxxor 1 Time", "Defeat the Araxxor in Morytania Spider cave.", "Morytania", TaskDifficulty.HARD, "Slayer 92", "Araxxor");
        defeatNpc("Defeat the Royal Titans solo", "Defeat the Royal Titans without any other player joining.", "General", TaskDifficulty.HARD, "", "Royal Titans");
        defeatNpc("Defeat the Corporeal Beast", "Defeat the Corporeal Beast in the Wilderness.", "Wilderness", TaskDifficulty.HARD, "", "Corporeal Beast");
        defeatNpc("Defeat Zalcano", "Defeat Zalcano in Prifddinas.", "Tirannwn", TaskDifficulty.HARD, "", "Zalcano");

        equipItem("Equip a Dragon Defender", "Equip a Dragon Defender.", "General", TaskDifficulty.HARD, "Defence 60 + Warriors' Guild access", "Dragon defender");
        equipItem("Equip a Trident of the Seas", "Equip a Trident of the Seas.", "General", TaskDifficulty.HARD, "Magic 75, Slayer 87", "Trident of the seas");
        equipItem("Equip any Full Barrows Armour Set", "Equip a full set of any Barrows armour + weapon.", "Morytania", TaskDifficulty.HARD, "Defence 70",
            "Guthan's helm", "Guthan's platebody", "Guthan's chainskirt", "Guthan's warspear",
            "Dharok's helm", "Dharok's platebody", "Dharok's platelegs", "Dharok's greataxe",
            "Verac's helm", "Verac's brassard", "Verac's plateskirt", "Verac's flail",
            "Torag's helm", "Torag's platebody", "Torag's platelegs", "Torag's hammers",
            "Karil's coif", "Karil's leathertop", "Karil's leatherskirt", "Karil's crossbow",
            "Ahrim's hood", "Ahrim's robetop", "Ahrim's robeskirt", "Ahrim's staff");
        equipItem("Equip a Dark Bow in Tirannwn", "Equip a Dark Bow in Tirannwn.", "Tirannwn", TaskDifficulty.HARD, "Ranged 60, Slayer 90", "Dark bow");
        equipItem("Equip a Dragon 2-Handed Sword in the Wilderness", "Equip a Dragon 2h Sword in the Wilderness.", "Wilderness", TaskDifficulty.HARD, "Attack 60", "Dragon 2h sword");
        equipItem("Equip a Malediction Ward", "Equip a Malediction Ward.", "Wilderness", TaskDifficulty.HARD, "Defence 60", "Malediction ward");

        craftItem("Assemble a Slayer Helm", "Assemble a Slayer Helm.", "General", TaskDifficulty.HARD, "Slayer 58, Crafting 55 + Malevolent Masquerade", "Slayer helmet");
        craftItem("Create an Amulet of Blood Fury", "Create an Amulet of Blood Fury.", "Morytania", TaskDifficulty.HARD, "Crafting 90, Magic 87 + Sins of the Father", "Amulet of blood fury");

        addTask("Complete the Corrupted Gauntlet", "Complete the Corrupted Gauntlet in Prifddinas.", "Tirannwn", TaskDifficulty.HARD, TaskType.ACTIVITY, "", 1);
        addTask("Complete Tzhaar-Ket-Rak's third challenge", "Complete Tzhaar-Ket-Rak's third challenge.", "General", TaskDifficulty.HARD, TaskType.ACTIVITY, "", 1);
        addTask("25 Chambers of Xeric", "Complete the Chambers of Xeric Normal or Challenge Mode 25 times.", "Kourend", TaskDifficulty.HARD, TaskType.ACTIVITY, "", 25);

        // =====================================================================
        // ELITE TASKS (200 pts)
        // =====================================================================
        defeatNpc("Defeat Awakened Whisperer", "Defeat Awakened Whisperer.", "General", TaskDifficulty.ELITE, "", "Whisperer");
        defeatNpc("Defeat Awakened Leviathan", "Defeat Awakened Leviathan.", "General", TaskDifficulty.ELITE, "", "Leviathan");
        defeatNpc("Defeat Awakened Duke Sucellus", "Defeat Awakened Duke Sucellus.", "General", TaskDifficulty.ELITE, "", "Duke Sucellus");
        defeatNpc("Defeat Awakened Vardorvis", "Defeat Awakened Vardorvis.", "General", TaskDifficulty.ELITE, "", "Vardorvis");
        defeatNpc("Defeat Vorkath 5 Times Without Special Damage", "Defeat Vorkath 5 times in a row without taking special attack damage.", "Fremennik", TaskDifficulty.ELITE, "", "Vorkath");
        defeatNpc("Defeat Phosani's Nightmare", "Defeat Phosani's Nightmare in the Sisterhood Sanctuary.", "Morytania", TaskDifficulty.ELITE, "", "Phosani's Nightmare");

        equipItem("Equip a Godsword", "Equip any Godsword.", "General", TaskDifficulty.ELITE, "Attack 75, Smithing 80",
            "Armadyl godsword", "Bandos godsword", "Saradomin godsword", "Zamorak godsword");
        equipItem("Equip the Bellator Ring", "Equip the Bellator Ring.", "General", TaskDifficulty.ELITE, "", "Bellator ring");
        equipItem("Equip the Venator Ring", "Equip the Venator Ring.", "General", TaskDifficulty.ELITE, "", "Venator ring");
        equipItem("Equip the Magus Ring", "Equip the Magus Ring.", "General", TaskDifficulty.ELITE, "", "Magus ring");
        equipItem("Equip a Piece of Masori Armour", "Equip a Masori Mask, Masori Body or Masori chaps.", "Desert", TaskDifficulty.ELITE, "Ranged 80, Defence 30 + Beneath Cursed Sands",
            "Masori mask", "Masori body", "Masori chaps");
        equipItem("Equip an Abyssal Tentacle", "Equip an Abyssal Tentacle.", "General", TaskDifficulty.ELITE, "Magic 50, Attack 75, Slayer 87", "Abyssal tentacle");
        equipItem("Equip an Occult Necklace", "Equip an Occult Necklace.", "General", TaskDifficulty.ELITE, "Magic 70, Slayer 93", "Occult necklace");
        equipItem("Equip Some Zenyte Jewelry", "Equip any piece of Zenyte Jewelry.", "General", TaskDifficulty.ELITE, "Crafting 89",
            "Necklace of anguish", "Tormented bracelet", "Ring of suffering", "Amulet of torture");
        equipItem("Equip a Fire Cape", "Equip a Fire Cape.", "General", TaskDifficulty.ELITE, "TzHaar Fight Cave completion", "Fire cape");
        equipItem("Equip a piece of Radiant Oathplate", "Equip a piece of Radiant oathplate armour.", "General", TaskDifficulty.ELITE, "Defence 78",
            "Radiant oathplate helm", "Radiant oathplate body", "Radiant oathplate legs");
        equipItem("Equip any Ancestral piece", "Equip an Ancestral Hat, robe top or robe bottom.", "General", TaskDifficulty.ELITE, "Magic 75, Defence 65",
            "Ancestral hat", "Ancestral robe top", "Ancestral robe bottom");
        equipItem("Equip a Dragon Hunter Lance", "Equip a Dragon hunter lance.", "General", TaskDifficulty.ELITE, "Slayer 95, Attack 78", "Dragon hunter lance");
        equipItem("Equip a Dragon Chainbody in the Kharidian Desert", "Equip a Dragon Chainbody in the Kharidian Desert.", "Desert", TaskDifficulty.ELITE, "Defence 60", "Dragon chainbody");
        equipItem("Equip a Piece of the Dagon'Hai Set", "Equip any piece of the Dagon'hai robe set.", "Wilderness", TaskDifficulty.ELITE, "Magic 70, Defence 40",
            "Dagon'hai hat", "Dagon'hai robe top", "Dagon'hai robe bottom");
        equipItem("Equip the Voidwaker", "Equip the Voidwaker.", "Wilderness", TaskDifficulty.ELITE, "Attack 75", "Voidwaker");
        equipItem("Equip Avernic Treads", "Equip Avernic treads.", "General", TaskDifficulty.ELITE, "Defence 80, Magic 80, Ranged 80, Strength 80", "Avernic treads");

        craftItem("Craft a Toxic Blowpipe", "Craft a Toxic Blowpipe.", "General", TaskDifficulty.ELITE, "Fletching 78", "Toxic blowpipe");

        addTask("Cast Ice Barrage", "Cast the Ice Barrage spell.", "General", TaskDifficulty.ELITE, TaskType.SPELL, "Magic 94", 1);
        addTask("Use a prayer altar to restore 90 prayer in Prifddinas", "Use a prayer altar to restore 90 prayer points in Prifddinas.", "Tirannwn", TaskDifficulty.ELITE, TaskType.PRAYER, "Prayer 90", 1);
        addTask("Imbue a God Cape", "Imbue a Saradomin, Guthix or Zamorak Cape.", "Wilderness", TaskDifficulty.ELITE, TaskType.MISC, "Magic 75 + Mage Arena II",
            1, "Imbued saradomin cape", "Imbued guthix cape", "Imbued zamorak cape");
        addTask("Complete the Theatre of Blood 25 Times", "Complete the Theatre of Blood on Normal or Hard Mode 25 times.", "Morytania", TaskDifficulty.ELITE, TaskType.ACTIVITY, "", 25);
        addTask("Complete Wave 12 of Fortis Colosseum", "Complete Wave 12 of Fortis Colosseum.", "Varlamore", TaskDifficulty.ELITE, TaskType.ACTIVITY, "", 1);

        // =====================================================================
        // MASTER TASKS (400 pts)
        // =====================================================================
        defeatNpc("Defeat Nex Solo", "Defeat Nex in a private instance without help from any other player.", "General", TaskDifficulty.MASTER, "", "Nex");

        equipItem("Equip the Osmumten's Fang (or)", "Equip Osmumten's Fang (or).", "Desert", TaskDifficulty.MASTER, "Attack 82 + Beneath Cursed Sands", "Osmumten's fang (or)");
        equipItem("Equip an Infernal Cape", "Equip an Infernal Cape.", "General", TaskDifficulty.MASTER, "The Inferno completion", "Infernal cape");
        equipItem("Equip a Corrupted Weapon", "Equip a Corrupted Blade of Saeldor or Bow of Faerdhinen.", "Tirannwn", TaskDifficulty.MASTER, "Attack 80 or Ranged 80 + Agility 70",
            "Blade of saeldor (c)", "Bow of faerdhinen (c)");

        addTask("Complete Tzhaar-Ket-Rak's Special challenge", "Complete Tzhaar-Ket-Rak's league-only challenge.", "General", TaskDifficulty.MASTER, TaskType.ACTIVITY, "", 1);

        // Build reverse lookup maps
        buildLookupMaps();
    }

    // =========================================================================
    // Helper methods for adding tasks
    // =========================================================================

    private static void defeatNpc(String name, String desc, String area, TaskDifficulty diff, String reqs, String... npcs)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.DEFEAT_NPC).requirements(reqs).matchKeywords(npcs).quantity(1)
            .build());
    }

    private static void equipItem(String name, String desc, String area, TaskDifficulty diff, String reqs, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.EQUIP_ITEM).requirements(reqs).matchKeywords(items).quantity(1)
            .build());
    }

    private static void craftItem(String name, String desc, String area, TaskDifficulty diff, String reqs, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.CRAFT_ITEM).requirements(reqs).matchKeywords(items).quantity(1)
            .build());
    }

    private static void catchItem(String name, String desc, String area, TaskDifficulty diff, String reqs, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.CATCH_ITEM).requirements(reqs).matchKeywords(items).quantity(1)
            .build());
    }

    private static void catchItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.CATCH_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void cookItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.COOK_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void mineItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.MINE_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void chopItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.CHOP_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void burnItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.BURN_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void cleanItem(String name, String desc, String area, TaskDifficulty diff, String reqs, int qty, String... items)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(TaskType.CLEAN_ITEM).requirements(reqs).matchKeywords(items).quantity(qty)
            .build());
    }

    private static void addTask(String name, String desc, String area, TaskDifficulty diff, TaskType type, String reqs, int qty, String... keywords)
    {
        ALL_TASKS.add(DemonicPactsTask.builder()
            .name(name).description(desc).area(area).difficulty(diff)
            .type(type).requirements(reqs).matchKeywords(keywords).quantity(qty)
            .build());
    }

    // =========================================================================
    // Lookup map construction
    // =========================================================================

    private static void buildLookupMaps()
    {
        for (DemonicPactsTask task : ALL_TASKS)
        {
            if (task.getMatchKeywords() == null) continue;

            Map<String, List<DemonicPactsTask>> targetMap;
            switch (task.getType())
            {
                case DEFEAT_NPC:
                    targetMap = NPC_TASKS;
                    break;
                case EQUIP_ITEM:
                case CRAFT_ITEM:
                case CATCH_ITEM:
                case COOK_ITEM:
                case MINE_ITEM:
                case CHOP_ITEM:
                case BURN_ITEM:
                case CLEAN_ITEM:
                    targetMap = ITEM_TASKS;
                    break;
                default:
                    // Put misc keyword-bearing tasks into both maps for broader matching
                    for (String kw : task.getMatchKeywords())
                    {
                        String key = kw.toLowerCase();
                        ITEM_TASKS.computeIfAbsent(key, k -> new ArrayList<>()).add(task);
                        NPC_TASKS.computeIfAbsent(key, k -> new ArrayList<>()).add(task);
                    }
                    continue;
            }

            for (String kw : task.getMatchKeywords())
            {
                String key = kw.toLowerCase();
                targetMap.computeIfAbsent(key, k -> new ArrayList<>()).add(task);
            }
        }

        // Build OBJECT_TASKS by mapping world object names to their corresponding tasks
        for (Map.Entry<String, String[]> entry : OBJECT_TO_TASK_KEYWORDS.entrySet())
        {
            String objectName = entry.getKey();
            for (String itemKeyword : entry.getValue())
            {
                List<DemonicPactsTask> tasks = ITEM_TASKS.get(itemKeyword.toLowerCase());
                if (tasks != null)
                {
                    OBJECT_TASKS.computeIfAbsent(objectName, k -> new ArrayList<>()).addAll(tasks);
                }
            }
        }
    }

    // =========================================================================
    // Public API
    // =========================================================================

    public static List<DemonicPactsTask> getAllTasks()
    {
        return Collections.unmodifiableList(ALL_TASKS);
    }

    /**
     * Find tasks matching an NPC name (case-insensitive).
     */
    public static List<DemonicPactsTask> findNpcTasks(String npcName)
    {
        if (npcName == null) return Collections.emptyList();
        List<DemonicPactsTask> tasks = NPC_TASKS.get(npcName.toLowerCase());
        return tasks != null ? tasks : Collections.emptyList();
    }

    /**
     * Find tasks matching an item name (case-insensitive).
     */
    public static List<DemonicPactsTask> findItemTasks(String itemName)
    {
        if (itemName == null) return Collections.emptyList();
        List<DemonicPactsTask> tasks = ITEM_TASKS.get(itemName.toLowerCase());
        return tasks != null ? tasks : Collections.emptyList();
    }

    /**
     * Check if any task matches this NPC name.
     */
    public static boolean isTaskNpc(String npcName)
    {
        return npcName != null && NPC_TASKS.containsKey(npcName.toLowerCase());
    }

    /**
     * Check if any task matches this item name.
     */
    public static boolean isTaskItem(String itemName)
    {
        return itemName != null && ITEM_TASKS.containsKey(itemName.toLowerCase());
    }

    /**
     * Find tasks matching a world object name (rocks, trees, patches).
     * Case-insensitive.
     */
    public static List<DemonicPactsTask> findObjectTasks(String objectName)
    {
        if (objectName == null) return Collections.emptyList();
        List<DemonicPactsTask> tasks = OBJECT_TASKS.get(objectName.toLowerCase());
        return tasks != null ? tasks : Collections.emptyList();
    }

    /**
     * Check if any task matches this world object name.
     */
    public static boolean isTaskObject(String objectName)
    {
        return objectName != null && OBJECT_TASKS.containsKey(objectName.toLowerCase());
    }

    /**
     * Get tasks filtered by difficulty.
     */
    public static List<DemonicPactsTask> getTasksByDifficulty(TaskDifficulty difficulty)
    {
        return ALL_TASKS.stream()
            .filter(t -> t.getDifficulty() == difficulty)
            .collect(Collectors.toList());
    }

    /**
     * Get tasks filtered by area.
     */
    public static List<DemonicPactsTask> getTasksByArea(String area)
    {
        return ALL_TASKS.stream()
            .filter(t -> t.getArea().equalsIgnoreCase(area))
            .collect(Collectors.toList());
    }
}
