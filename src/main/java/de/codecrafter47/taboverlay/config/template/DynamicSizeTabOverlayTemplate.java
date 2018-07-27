package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.TabOverlayView;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicSizeTabOverlayTemplate extends AbstractTabOverlayTemplate {
    private String playerSet;

    private PlayerOrderTemplate playerOrder;

    private ComponentTemplate playerComponent;

    @Override
    public TabOverlayView<? extends AbstractTabOverlayTemplate, ?> instantiate(TabView tabView, TabOverlayHandler handler, Context context) {
        // todo
        return null;
    }
}
