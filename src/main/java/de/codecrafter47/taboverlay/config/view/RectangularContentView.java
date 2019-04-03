package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.RectangularTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.handler.RectangularTabOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RectangularContentView extends ComponentView {
    private final ComponentView content;
    private final RectangularTabOverlay contentHandle;

    private List<RectangularTabOverlay.Dimension> possibleSizes = null;

    public RectangularContentView(RectangularTabOverlayTemplate template, RectangularTabOverlay contentHandle) {
        this.contentHandle = contentHandle;
        if (template.getSize() != -1) {
            for (RectangularTabOverlay.Dimension dimension : contentHandle.getSupportedSizes()) {
                if (dimension.getSize() == template.getSize()) {
                    possibleSizes = Collections.singletonList(dimension);
                    break;
                }
            }
            // todo better error handling
            if (possibleSizes == null) {
                throw new IllegalArgumentException("Unsupported size " + template.getSize());
            }
        } else {
            possibleSizes = new ArrayList<>();
            for (RectangularTabOverlay.Dimension dimension : contentHandle.getSupportedSizes()) {
                if (dimension.getColumns() == template.getColumns()) {
                    possibleSizes.add(dimension);
                }
            }
        }
        this.content = template.getContentRoot().instantiate();
    }

    @Override
    protected void onActivation() {
        content.activate(getContext(), this);

        if (!updateTabListSize()) {
            this.updateArea(RectangularArea.of(contentHandle));
        }
    }

    @Override
    protected void onAreaUpdated() {
        content.updateArea(getArea());
    }

    private boolean updateTabListSize() {

        RectangularTabOverlay.Dimension bestFit = contentHandle.getSize();
        // todo make more efficient
        for (RectangularTabOverlay.Dimension size : possibleSizes) {
            if (size.getSize() < this.getPreferredSize() && size.getSize() > bestFit.getSize()) {
                bestFit = size;
            }
            if (size.getSize() >= this.getPreferredSize() && bestFit.getSize() < this.getPreferredSize()) {
                bestFit = size;
            }
            if (size.getSize() < bestFit.getSize() && size.getSize() >= this.getPreferredSize()) {
                bestFit = size;
            }
        }

        if (bestFit != contentHandle.getSize()) {
            contentHandle.setSize(bestFit);
            this.updateArea(RectangularArea.of(contentHandle));
            return true;
        }
        return false;
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        if (!updateTabListSize()) {
            this.updateArea(this.getArea());
        }
    }

    @Override
    public int getMinSize() {
        return content.getMinSize();
    }

    @Override
    public int getPreferredSize() {
        return content.getPreferredSize();
    }

    @Override
    public int getMaxSize() {
        return content.getMaxSize();
    }

    @Override
    public boolean isBlockAligned() {
        return content.isBlockAligned();
    }

    @Override
    protected void onDeactivation() {
        content.deactivate();
    }
}
