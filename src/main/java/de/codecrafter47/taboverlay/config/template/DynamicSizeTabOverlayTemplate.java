package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.token.PatternTokenReader;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.view.DynamicSizeTabOverlayView;
import de.codecrafter47.taboverlay.config.view.TabOverlayView;
import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicSizeTabOverlayTemplate extends AbstractTabOverlayTemplate {
    private PlayerSetTemplate playerSet;

    private PlayerOrderTemplate playerOrder;

    private ComponentTemplate playerComponent;

    private ComponentTemplate morePlayersComponent;

    @Override
    public TabOverlayView<? extends AbstractTabOverlayTemplate, ?> instantiate(TabView tabView, TabOverlayHandler handler, Context context) {
        return new DynamicSizeTabOverlayView<>(tabView, this, handler.enterOperationMode(OperationMode.SIMPLE_WITH_HEADER_AND_FOOTER), context);
    }
}
