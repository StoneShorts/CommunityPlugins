package com.communitydev.biggs.ExampleMiner;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

@Slf4j
@Singleton
class ExampleMinerOverlay extends OverlayPanel {
    private ExampleMinerPlugin plugin;
    private ExampleMinerConfig config;

    public String infoStatus = "Starting...";
    String timeFormat;

    @Inject
    private ExampleMinerOverlay(final Client client, final ExampleMinerPlugin plugin, final ExampleMinerConfig config) {
        super(plugin);
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.config = config;
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "EX overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {

        if (plugin.botTimer == null) {
            log.debug("Overlay conditions not met, not starting overlay");
            return null;
        }
        Color backgroundColor = new Color(0,98,255, 125); // #66ff00 with transparency
        panelComponent.setBackgroundColor(backgroundColor);
        Duration duration = Duration.between(plugin.botTimer, Instant.now());
        timeFormat = (duration.toHours() < 1) ? "mm:ss" : "HH:mm:ss";
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("[LC] Example Miner")
                .color(ColorUtil.fromHex("#0e1111"))
                .build());
        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.enablePlugin ? "Running" : "Paused")
                .color(plugin.enablePlugin ? Color.GREEN : Color.RED)
                .build());
        if (plugin.enablePlugin && plugin.state != null) {
            panelComponent.setPreferredSize(new Dimension(250, 200));
            panelComponent.setBorder(new Rectangle(5, 5, 5, 5));
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Status:")
                    .leftColor(Color.WHITE)
                    .right(plugin.state.toString())
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Time running:")
                    .leftColor(Color.WHITE)
                    .right(formatDuration(duration.toMillis(), timeFormat))
                    .rightColor(Color.WHITE)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Example Counter:")
                    .leftColor(Color.WHITE)
                    .right(String.valueOf(plugin.notAnimatingTicks))
                    .rightColor(Color.RED)
                    .build());

        }
        return super.render(graphics);
    }
}


