package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import lombok.Builder;
import lombok.Value;

public interface ComponentTemplate {

    LayoutInfo getLayoutInfo();

    ComponentView instantiate();

    @Builder
    @Value
    class LayoutInfo {

        boolean constantSize;

        int size;
    }
}
