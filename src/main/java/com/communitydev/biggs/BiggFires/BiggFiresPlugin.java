package com.communitydev.biggs.BiggFires;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.query.TileItemQuery;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.Packets.TileItemPackets;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.EthanApiPlugin.Collections.TileItems.tileItems;

@PluginDependency(PacketUtilsPlugin.class)
@PluginDependency(EthanApiPlugin.class)
@PluginDescriptor(
        name = "<html><font color=\"#bf8e08\">[BIGGS]</font> Lumby Logs</html>",
        description = "Lights fires using the logs at lumbridge bank, if not found - we hop worlds.",
        enabledByDefault = false,
        tags = {"biggs", "polar", "skilling"})
@Slf4j

public class BiggFiresPlugin extends Plugin {
    @Inject
    OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    private BiggFiresOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private BiggFiresConfig config;
    private State previousState = null;

    private Instant startTime;
    public Instant breakTime;
    public int timeout;
    public int getFiremakingLevel() {
        return client.getRealSkillLevel(Skill.FIREMAKING);
    }
    private int logsLit = 0;

    private State state;
    public boolean pluginRunning;

    private final HotkeyListener pluginToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            togglePlugin();
        }
    };

    public int tickDelay() {
        return config.tickDelay() ? ThreadLocalRandom.current().nextInt(config.tickDelayMin() + 2, config.tickDelayMax() + 3) : 0;
    }

    public void togglePlugin() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        pluginRunning = !pluginRunning;
        if(timeout > 0) {
            timeout--;
        }
    }
    @Provides
    private BiggFiresConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(BiggFiresConfig.class);
    }
    @Override
    protected void startUp() throws Exception {
        pluginRunning = false;
        keyManager.registerKeyListener(pluginToggle);
        System.out.println("Registered Key Listeners!");
        System.out.println("Registered plugin with break handler!");
        overlayManager.add(overlay);
        System.out.println("Registered overlays!");
        startTime = Instant.now();
        logsLit = 0;
        ////verified = licenseVerificationHandler.verifyKey(config.userID(), config.accessKey());
        System.out.println("Started Bigg FireMaker!!");
    }
    @Override
    protected void shutDown() throws Exception {
        pluginRunning = false;
        logsLit = 0;
        keyManager.unregisterKeyListener(pluginToggle);
        overlayManager.remove(overlay);
        System.out.println("unregistered plugin with break handler!");
        System.out.println("unregistered Key Listeners!");
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !pluginRunning) {
            return;
        }
        if (timeout > 0) {
            timeout--;
        }
        state = getNextState();
        handleState(state);

    }
    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) {
            if (event.getMessage().equals("You can't light a fire here.")) {
                // Handle this specific situation
                System.out.println("HOPPING. LOG FOUND ON FIRE. ");
            }
        }
    }
    private void handleState(State state) {
        System.out.println("state: " + state);
        switch (state) {
            case ANIMATE:
                break;
            case LIGHT_FIRE:
                lightFire();
                break;
            case TIMEOUT:
                timeout--;
                break;
        }
    }
    public State getNextState() {
        if (EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            timeout = tickDelay();
            return State.ANIMATE;
        }
        if (!hasTinderbox()) {
            shutDownPlugin();
            return State.IDLE;
            }
        if (config.stopAt50Fm() && hasFifty()) {
            shutDownPlugin();
            return State.IDLE;
        }
        if (!pluginRunning) {
            return State.IDLE;
        }

        if (timeout  > 0) {
            return State.TIMEOUT;
        }

        TileItemQuery query = new TileItemQuery(tileItems);
        Optional<ETileItem> nearestLog = query.withName("Logs").nearestToPlayer();
        if (hasTinderbox() && nearestLog.isPresent()) {
            return State.LIGHT_FIRE;
        }
        return State.HOPPING;
    }
    private void shutDownPlugin() {
        pluginRunning = false;
    }
    //26185 fire object ID
    private void lightFire() {
        System.out.println("Attempting to light a fire");

        TileItemQuery logQuery = new TileItemQuery(tileItems);
        Optional<ETileItem> nearestLog = logQuery.withName("Logs").nearestToPlayer();

        if (nearestLog.isPresent()) {
            WorldPoint logLocation = nearestLog.get().getLocation();
            int id = nearestLog.get().getTileItem().getId();
            timeout = tickDelay();
            System.out.println("Found a log, attempting to light at location: " + logLocation);

            TileItemPackets.queueTileItemAction(4, id, logLocation.getX(), logLocation.getY(), false);
            logsLit++;
        } else {
            System.out.println("No logs found, hopping worlds");
            //No logs found, hop world
            timeout = tickDelay();
        }
    }
    private boolean hasFifty() {
        int firemakingXp = client.getSkillExperience(Skill.FIREMAKING);
        int level50Xp = Experience.getXpForLevel(50);
        return firemakingXp >= level50Xp;
    }
        private boolean hasTinderbox() {
        return Inventory.search()
                .filter(item -> item.getName().contains("Tinderbox"))
                .result().size() > 0;
    }
    public String getStatus() {
        if (!hasTinderbox()) {
            return "Error: No Tinderbox!";
        }
        if (state == null) {
            return "Ready to start.";
        }
        if (config.stopAt50Fm() && hasFifty()) {
            return "Reached Lvl 50. Stopped.";
        }
        return state.toString();
    }


        private void sendKey(int key) {
        keyEvent(KeyEvent.KEY_PRESSED, key);
        keyEvent(KeyEvent.KEY_RELEASED, key);
    }
    private void keyEvent(int id, int key) {
        KeyEvent e = new KeyEvent(
                client.getCanvas(), id, System.currentTimeMillis(),
                0, key, KeyEvent.CHAR_UNDEFINED
        );

        client.getCanvas().dispatchEvent(e);
    }
    public boolean isStarted() {
        return pluginRunning;
    }
    public Instant getStartTime() {
        return startTime;
    }
    public int getLogsLit() {
        return logsLit;
    }
}
    enum State {
    ANIMATE,
    HOPPING,
    LIGHT_FIRE,
    BREAKING,
    TIMEOUT,
        IDLE
    }


