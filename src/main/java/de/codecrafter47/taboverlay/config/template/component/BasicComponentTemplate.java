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
    TextTemplate leftText;
    TextTemplate centerText;
    TextTemplate rightText;
    PingTemplate ping;
    IconTemplate icon;
    BasicComponentConfiguration.LongTextBehaviour longText;

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
        return new BasicComponentView(leftText != null ? leftText.instantiate() : null,
                centerText != null ? centerText.instantiate() : null,
                rightText != null ? rightText.instantiate() : null,
                ping.instantiate(), icon.instantiate(), longText);
    }
}
