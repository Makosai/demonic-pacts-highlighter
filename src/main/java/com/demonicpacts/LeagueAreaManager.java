package com.demonicpacts;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class LeagueAreaManager
{
    @Inject
    private Client client;

    /**
     * Estimates the player's current League Area based on Region IDs.
     */
    public String getActiveArea()
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return "Unknown";
        }

        WorldPoint wp = player.getWorldLocation();
        int regionId = wp.getRegionID();

        switch (regionId)
        {
            // Asgarnia Dungeons / Instances
            case 11578: // Taverley Dungeon
            case 11601: // Giant Mole
            case 11330: // God Wars Dungeon
            case 11603: // God Wars Dungeon
            case 11424: // Royal Titans
                return "Asgarnia";

            // Desert Dungeons / Instances
            case 13936: // Kalphite Lair
            case 14171: // Tombs of Amascut
            case 12078: // Tempoross
            case 13358: // Pyramid Plunder
                return "Desert";

            // Fremennik Dungeons / Instances
            case 11340: // Dagannoth Kings
            case 9023:  // Vorkath
            case 11683: // Phantom Muspah
                return "Fremennik";

            // Kandarin Dungeons / Instances
            case 11150: // Demonic Gorillas
            case 9116:  // Cave Kraken
            case 9371:  // Thermonuclear Smoke Devil
                return "Kandarin";

            // Karamja Dungeons / Instances
            case 11339: // Brimhaven Dungeon
            case 11338: // TzHaar City
            case 9551:  // Fight Caves
            case 9043:  // Inferno
                return "Karamja";

            // Kourend Dungeons / Instances
            case 5536:  // Wintertodt
            case 13136: // Chambers of Xeric
            case 6810:  // Mount Karuulm (Hydra)
            case 7322:  // Forthos Dungeon
            case 6716:  // Catacombs of Kourend
                return "Kourend";

            // Morytania Dungeons / Instances
            case 14642: // Barrows Underground
            case 13622: // Nightmare of Ashihama
            case 14385: // Theatre of Blood
            case 14386: // Theatre of Blood
            case 14643: // Araxxor
                return "Morytania";

            // Tirannwn Dungeons / Instances
            case 13396: // Zulrah
            case 7492:  // Gauntlet
            case 12894: // Prifddinas Underground
            case 12895: // Zalcano
                return "Tirannwn";

            // Varlamore Dungeons / Instances
            case 7216:  // Fortis Colosseum
            case 6962:  // Moons of Peril
                return "Varlamore";
        }

        // 2. Broad Overworld Bounding Boxes
        // Extracts the X and Y region coordinates from the region ID (same as x/64 and y/64)
        int regionX = regionId >> 8;
        int regionY = regionId & 0xFF;

        if (regionY >= 55 && regionX >= 46 && regionX <= 53 && regionY <= 62) return "Wilderness";
        if (regionX >= 37 && regionX <= 41 && regionY >= 49 && regionY <= 54) return "Kandarin";
        if (regionX >= 44 && regionX <= 46 && regionY >= 49 && regionY <= 54) return "Asgarnia";
        if (regionX >= 48 && regionX <= 50 && regionY >= 49 && regionY <= 54) return "Misthalin"; // Often falls under "General"
        if (regionX >= 49 && regionX <= 53 && regionY >= 43 && regionY <= 48) return "Desert";
        if (regionX >= 42 && regionX <= 45 && regionY >= 42 && regionY <= 46) return "Karamja";
        if (regionX >= 53 && regionX <= 56 && regionY >= 49 && regionY <= 54) return "Morytania";
        if (regionX >= 32 && regionX <= 36 && regionY >= 48 && regionY <= 52) return "Tirannwn";
        if (regionX >= 40 && regionX <= 43 && regionY >= 55 && regionY <= 60) return "Fremennik";
        if (regionX >= 22 && regionX <= 28 && regionY >= 53 && regionY <= 59) return "Kourend";
        if (regionX >= 24 && regionX <= 28 && regionY >= 44 && regionY <= 50) return "Varlamore";

        return "Unknown";
    }
}