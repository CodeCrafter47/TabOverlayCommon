package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.view.components.AnimatedComponentView;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class AnimatedComponentTemplate implements ComponentTemplate {
    List<ComponentTemplate> components;
    float interval;
    boolean randomize;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(true)
                .minSize(components.get(0).getLayoutInfo().getMinSize())
                .blockAligned(components.get(0).getLayoutInfo().isBlockAligned())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        List<ComponentView> componentViews = new ArrayList<>(components.size());
        for (ComponentTemplate component : components) {
            componentViews.add(component.instantiate());
        }
        return new AnimatedComponentView(componentViews, interval, components.get(0).getLayoutInfo().getMinSize(), randomize);
    }
}
