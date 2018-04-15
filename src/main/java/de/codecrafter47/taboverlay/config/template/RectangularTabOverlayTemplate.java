package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.RectangularTabOverlayView;
import de.codecrafter47.taboverlay.config.view.TabOverlayView;
import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RectangularTabOverlayTemplate extends AbstractTabOverlayTemplate {

    private int size;
    private int columns;

    private ComponentTemplate contentRoot;

    @Override
    public TabOverlayView<? extends AbstractTabOverlayTemplate, ?> instantiate(TabView tabView, TabOverlayHandler handler, Context context) {
        return new RectangularTabOverlayView<>(tabView, this, handler.enterOperationMode(OperationMode.RECTANGULAR_WITH_HEADER_AND_FOOTER), context);
    }
}
