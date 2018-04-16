package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.RectangularTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.RectangularTabOverlay;
import de.codecrafter47.taboverlay.handler.RectangularTabOverlayWithHeaderAndFooter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RectangularTabOverlayView<TabView extends de.codecrafter47.taboverlay.TabView> extends TabOverlayView<RectangularTabOverlayTemplate, RectangularTabOverlayWithHeaderAndFooter> {

    private final ContentView contentView;

    private List<RectangularTabOverlay.Dimension> possibleSizes = null;

    public RectangularTabOverlayView(TabView tabView, RectangularTabOverlayTemplate tabOverlayTemplate, RectangularTabOverlayWithHeaderAndFooter tablist, Context context) {
        super(tabView, tabOverlayTemplate, tablist, context);

        if (tabOverlayTemplate.getSize() != -1) {
            for (RectangularTabOverlay.Dimension dimension : tablist.getSupportedSizes()) {
                if (dimension.getSize() == tabOverlayTemplate.getSize()) {
                    possibleSizes = Collections.singletonList(dimension);
                    break;
                }
            }
            // todo better error handling
            if (possibleSizes == null) {
                throw new IllegalArgumentException("Unsupported size " + tabOverlayTemplate.getSize());
            }
        } else {
            possibleSizes = new ArrayList<>();
            for (RectangularTabOverlay.Dimension dimension : tablist.getSupportedSizes()) {
                if (dimension.getColumns() == tabOverlayTemplate.getColumns()) {
                    possibleSizes.add(dimension);
                }
            }
        }

        contentView = new ContentView(tabOverlayTemplate.getContentRoot().instantiate());
        contentView.activate(context, null);
        if (!updateTabListSize()) {
            contentView.updateArea(RectangularArea.of(tablist));
        }
    }

    @Override
    public void deactivate() {
        contentView.deactivate();
        super.deactivate();
    }

    private boolean updateTabListSize() {

        RectangularTabOverlay.Dimension bestFit = tablist.getSize();
        // todo make more efficient
        for (RectangularTabOverlay.Dimension size : possibleSizes) {
            if (size.getSize() < contentView.getPreferredSize() && size.getSize() > bestFit.getSize()) {
                bestFit = size;
            }
            if (size.getSize() >= contentView.getPreferredSize() && bestFit.getSize() < contentView.getPreferredSize()) {
                bestFit = size;
            }
            if (size.getSize() < bestFit.getSize() && size.getSize() >= contentView.getPreferredSize()) {
                bestFit = size;
            }
        }

        if (bestFit != tablist.getSize()) {
            tablist.setSize(bestFit);
            contentView.updateArea(RectangularArea.of(tablist));
            return true;
        }
        return false;
    }

    private class ContentView extends ComponentView {
        private final ComponentView content;

        private ContentView(ComponentView content) {
            this.content = content;
        }

        @Override
        protected void onActivation() {
            content.activate(context, this);
        }

        @Override
        protected void onAreaUpdated() {
            content.updateArea(getArea());
        }

        @Override
        protected void requestLayoutUpdate(ComponentView source) {
            if (!updateTabListSize()) {
                contentView.updateArea(contentView.getArea());
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
}
