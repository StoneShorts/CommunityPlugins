package com.communitydev.biggs.BiggFires;

import net.runelite.client.config.*;
@ConfigGroup("biggfiresplugin")
public interface BiggFiresConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }
    @ConfigItem(
            keyName = "enableTickDelay",
            name = "Enable tick delays?",
            description = "",
            position = 1
    )
    default boolean tickDelay() {
        return true;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMin",
            name = "Game Tick Min",
            description = "",
            position = 2
    )
    default int tickDelayMin() {
        return 1;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "tickDelayMax",
            name = "Game Tick Max",
            description = "",
            position = 3
    )
    default int tickDelayMax() {
        return 3;
    }
    @ConfigItem(
            keyName = "stopAt50Fm",
            name = "Stop at 50 FM?",
            description = "Toggle to stop the script at 50 Firemaking",
            position = 4
    )
    default boolean stopAt50Fm() {
        return false;
    }
    @ConfigItem(
            keyName = "webhookUrl",
            name = "Webhook URL",
            description = "Enter a Disc. Webhook URL to get updates.",
            position = 7
    )
    default String webhookUrl() {
        return "";
    }
    }