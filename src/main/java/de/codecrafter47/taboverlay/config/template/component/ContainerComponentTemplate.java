package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ContainerComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ContainerComponentTemplate implements ComponentTemplate {

    private ComponentTemplate content;
    private boolean fillSlotsVertical;
    private int minSize;
    private int maxSize; // -1 is used to denote note limit
    private int columns;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(content.getLayoutInfo().isConstantSize() || minSize == maxSize)
                .size(maxSize != -1 ? Integer.max(content.getLayoutInfo().getSize(), maxSize) : content.getLayoutInfo().getSize())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new ContainerComponentView(content.instantiate(), fillSlotsVertical, minSize, maxSize, columns);
    }
}
