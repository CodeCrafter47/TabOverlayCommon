package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ListComponentView;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
public class ListComponentTemplate implements ComponentTemplate {

    private List<ComponentTemplate> components;
    private int columns;
    TextTemplate defaultText;
    PingTemplate defaultPing;
    IconTemplate defaultIcon;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(components.stream()
                        .map(ComponentTemplate::getLayoutInfo)
                        .allMatch(LayoutInfo::isConstantSize))
                .minSize(components.stream()
                        .map(ComponentTemplate::getLayoutInfo)
                        .mapToInt(LayoutInfo::getMinSize)
                        .sum())
                .blockAligned(components.stream()
                        .map(ComponentTemplate::getLayoutInfo)
                        .anyMatch(LayoutInfo::isBlockAligned))
                .build();
    }

    @Override
    public ComponentView instantiate() {

        return new ListComponentView(components.stream()
                .map(ComponentTemplate::instantiate)
                .collect(Collectors.toList()),
                columns,
                defaultText.instantiate(),
                defaultPing.instantiate(),
                defaultIcon.instantiate());
    }
}
