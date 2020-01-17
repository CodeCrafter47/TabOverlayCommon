package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.dsl.components.BasicComponentConfiguration;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.BasicComponentView;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BasicComponentTemplate implements ComponentTemplate {
    TextTemplate text;
    PingTemplate ping;
    IconTemplate icon;
    BasicComponentConfiguration.Alignment alignment;
    BasicComponentConfiguration.LongTextBehaviour longText;
    int slotWidth;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(true)
                .minSize(1)
                .blockAligned(false)
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new BasicComponentView(text.instantiate(), ping.instantiate(), icon.instantiate(), alignment, longText, slotWidth);
    }
}
