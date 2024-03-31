package com.communitydev.Riv.RivGoblins;

import net.runelite.client.config.*;

@ConfigGroup("ExampleMinerConfig")
public interface RivGoblinsConfig extends Config {
    String GROUP_NAME = "goblinConfig";
    @ConfigSection(
            name = "<html><font color=#b3f073>Riv Goblins v0.1</font></html>",
            description = "",
            position = 0,
            closedByDefault = false
    )
    String Title = "titleSection";
    @ConfigSection(
            name = "<html><font color=#b3f073>Instructions</font></html>",
            description = "",
            position = 0,
            closedByDefault = false
    )
    String instructionsConfig = "instructionsConfig";
    /*    @ConfigSection(
                name = "<html><font color=#b3f073>Consumables Config</font></html>",
                description = "Configuration related to food actions.",
                position = 6
        )
        String foodConfig = "foodConfig";
        @ConfigSection(
                name = "<html><font color=#b3f073>WorldHop Config</font></html>",
                description = "Configuration related WorldHopping.",
                position = 8
        )
        String worldHopConfig = "worldHopConfig";

        @ConfigSection(
                name = "<html><font color=#b3f073>Gear Config</font></html>",
                description = "Config for gear to wear when reaching certain levels..",
                position = 7
        )
        String gearConfig = "gearConfig";*/
    @ConfigItem(
            keyName = "startStopHotkey",
            name = "Start/Stop",
            description = "Hotkey for activating or deactivating the plugin.",
            position = 1,
            section = Title
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }



/*    @Range(min = 10, max = 100) //nice example of a range, though useless in this plugin.
    @ConfigItem(keyName = "minHealthPercent", name = "Min Health %", description = "", position = 2, section = foodConfig)
    default int minHealthPercent() {
        return 65;
    }*/

    /* Nice string example.

    @ConfigItem(
    name = "Spec Weapon",
     description = "",
      position = 3,
      keyName = "specWeapon",
      section = gearConfig)
       default String specWeapon() {
            return "Dragon dagger(p++)";
        }*/
    @ConfigItem(
            keyName = "Instructions",
            name = "",
            description = "Instructions.",
            position = -1,
            section = "instructionsConfig"
    )
    default String instructions() {
        return "Please start the plugin at lumbridge spawn.\n\n" +
                "This plugin will run and kill goblins, and repeat\n\n" +
                "Banking and eating will be added soon."; // we add that later.

    }

}