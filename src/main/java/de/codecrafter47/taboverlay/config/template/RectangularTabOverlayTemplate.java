package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.RectangularContentView;
import de.codecrafter47.taboverlay.handler.ContentOperationMode;
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
    public AbstractActiveElement<?> createContentView(TabView tabView, TabOverlayHandler handler) {
        return new RectangularContentView(tabView, this, handler.enterContentOperationMode(ContentOperationMode.RECTANGULAR));
    }
}
