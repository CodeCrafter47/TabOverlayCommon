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
    private List<ComponentTemplate> components;
    private float interval;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(true)
                .size(components.get(0).getLayoutInfo().getSize())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        List<ComponentView> componentViews = new ArrayList<>(components.size());
        for (ComponentTemplate component : components) {
            componentViews.add(component.instantiate());
        }
        return new AnimatedComponentView(componentViews, interval, components.get(0).getLayoutInfo().getSize());
    }
}
