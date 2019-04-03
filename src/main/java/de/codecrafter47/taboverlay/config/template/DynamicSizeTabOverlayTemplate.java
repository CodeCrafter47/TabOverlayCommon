package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.DynamicSizeContentView;
import de.codecrafter47.taboverlay.config.view.TabOverlayView;
import de.codecrafter47.taboverlay.handler.ContentOperationMode;
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
    public AbstractActiveElement<?> createContentView(TabOverlayHandler handler) {
        return new DynamicSizeContentView(this, handler.enterContentOperationMode(ContentOperationMode.SIMPLE));
    }
}
