package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.SpacerComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SpacerComponentTemplate implements ComponentTemplate {
    TextTemplate defaultText;
    PingTemplate defaultPing;
    IconTemplate defaultIcon;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(false)
                .minSize(0)
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new SpacerComponentView(defaultText.instantiate(), defaultPing.instantiate(), defaultIcon.instantiate());
    }
}
