package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.DynamicSizeContentView;
import de.codecrafter47.taboverlay.handler.ContentOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderFooterOnlyTabOverlayTemplate extends AbstractTabOverlayTemplate {

    @Override
    public AbstractActiveElement<?> createContentView(TabView tabView, TabOverlayHandler handler) {
        handler.enterContentOperationMode(ContentOperationMode.PASS_TROUGH);
        return new AbstractActiveElement<Object>() {
            @Override
            protected void onActivation() {
                // dummy
            }

            @Override
            protected void onDeactivation() {
                // dummy
            }
        };
    }
}
