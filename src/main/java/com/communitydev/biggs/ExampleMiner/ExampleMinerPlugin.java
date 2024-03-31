package com.communitydev.biggs.ExampleMiner;


import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.time.Instant;

@PluginDescriptor(
        name = "<html><font color=#48f542>[LC]</font><font color=#b3f073>[B]</font> Miner</html>",
        description = "Mines Tin & Copper @ Lumby Swamp, and Banks.",
        enabledByDefault = false,
        tags = {"uz", "polar", "combat"})
@Slf4j
@PluginDependency(EthanApiPlugin.class)
@PluginDependency(PacketUtilsPlugin.class)
public class ExampleMinerPlugin extends Plugin {
    /*This is where you code shit noob*/
    @Inject
    private Client client;
    @Inject ConfigManager configManager;
    @Inject
    ExampleMinerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    OverlayManager overlayManager;
    @Inject
    Overlay overlay;
    boolean enablePlugin;
    public int timeout = 0;
    private int tickCount = 0;
    State state;
    State lastState;
    private Player player;
    int notAnimatingTicks = 0;
    Instant botTimer;

    @Provides
    ExampleMinerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleMinerConfig.class);
    }

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };
    public void togglePlugin() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        enablePlugin = !enablePlugin;
        if (!enablePlugin) {
            EthanApiPlugin.sendClientMessage("Example Miner Disabled.");
        } else {
            EthanApiPlugin.sendClientMessage("Example Miner Enabled.");
        }
    }
    State getState() {
        if (timeout > 0) {
            return State.TIMEOUT;
        }
        return getStates();
    }

        @Override
        protected void startUp() {
            botTimer = Instant.now();
            keyManager.registerKeyListener(pluginToggle);
            overlayManager.add(overlay);
        }

        @Override
        protected void shutDown() {
            overlayManager.remove(overlay);
            botTimer = null;
        }
    @Subscribe
    private void onGameTick(GameTick event) {
        if (!enablePlugin || !EthanApiPlugin.loggedIn()) {
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            return; //hi this is a push
        }
        tickCount++;
        player = client.getLocalPlayer();
        if (player != null && client != null) {
            state = getState();
        }
        if (state != State.TIMEOUT)
            lastState = state;
        switch (state) {
            case TIMEOUT:
                timeout--;
                break;
        }
    }

    State getStates() {
       // log.info("Current task: TIMEOUT"); ill add that later
        return State.TIMEOUT;
    }

    }