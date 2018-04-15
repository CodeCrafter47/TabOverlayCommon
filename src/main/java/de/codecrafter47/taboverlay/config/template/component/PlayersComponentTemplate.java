package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.player.PlayerSetFactory;
import de.codecrafter47.taboverlay.config.playerorder.PlayerOrderOptions;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ContainerComponentView;
import de.codecrafter47.taboverlay.config.view.components.PlayersComponentView;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Value
@Builder
public class PlayersComponentTemplate implements ComponentTemplate {
    private PlayerSetTemplate playerSet;

    private PlayerSetFactory playerSetFactory;

    private PlayerOrderOptions playerOrder;

    private ComponentTemplate playerComponent;

    @NonNull @Nonnull
    private ComponentTemplate morePlayersComponent;

    private boolean fillSlotsVertical;
    int minSize;
    /* A value of -1 indicates no limit. */
    int maxSize;
    private int columns;

    TextTemplate defaultText;
    PingTemplate defaultPing;
    IconTemplate defaultIcon;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(false)
                .size(0)
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new ContainerComponentView(new PlayersComponentView(playerSet, playerComponent, playerComponent.getLayoutInfo().getSize(), morePlayersComponent, morePlayersComponent.getLayoutInfo().getSize(), defaultIcon.instantiate(), defaultText.instantiate(), defaultPing.instantiate()),
                fillSlotsVertical, minSize, maxSize, columns);
    }
}
