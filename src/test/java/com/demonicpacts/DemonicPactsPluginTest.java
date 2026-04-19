package com.demonicpacts;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DemonicPactsPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(DemonicPactsPlugin.class);

        RuneLite.main(args);
    }
}