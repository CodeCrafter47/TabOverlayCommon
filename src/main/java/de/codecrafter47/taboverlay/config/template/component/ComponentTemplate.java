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

        /**
         * True if the component may require block alignment.
         */
        boolean blockAligned;

        /**
         * Minimum number of slots required for the component. Should be conservative, i.e. this should always be enough
         * to render the component.
         */
        int minSize;
    }
}
