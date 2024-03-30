package com.communitydev.biggs.BiggFires;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

public class BiggFiresOverlay extends OverlayPanel {
    private final BiggFiresPlugin plugin;
    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    public BiggFiresOverlay(Client client, final BiggFiresPlugin plugin) {
        setPosition(OverlayPosition.BOTTOM_LEFT);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        panelComponent.getChildren().clear();

        Duration runtime = Duration.between(plugin.getStartTime(), Instant.now());

        String overlayTitle = "Biggs Lumby Logs";

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(overlayTitle)
                .color(ColorUtil.fromHex("#0e1111"))
                .build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(overlayTitle) + 45,
                0));

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(plugin.isStarted() ? "Running" : "Paused")
                .color(plugin.isStarted() ? Color.GREEN : Color.RED)
                .build());

        String status = plugin.getStatus();
        int logsLit = plugin.getLogsLit();
        int woodcuttingLevel = plugin.getFiremakingLevel();
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Firemaking Level:")
                .right(String.valueOf(woodcuttingLevel))
                .rightColor(Color.green)
                .build());
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Status:")
                .right(status)
                .rightColor(Color.green)
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Fires lit:")
                .right(String.valueOf(logsLit))
                .rightColor(Color.green)
                .build());


        if (runtime != null) {
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Runtime: " + formatDuration(runtime))
                    .build());
        }

        panelComponent.setBackgroundColor(new Color(30, 30, 30, 175)); //money

        return panelComponent.render(graphics);
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String positive = String.format(
                "%d:%02d:%02d",
                absSeconds / 3600,
                (absSeconds % 3600) / 60,
                absSeconds % 60);
        return seconds < 0 ? "-" + positive : positive;
    }
}
