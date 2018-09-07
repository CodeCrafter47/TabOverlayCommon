package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.DynamicSizeTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.PlayersComponentView;
import de.codecrafter47.taboverlay.handler.SimpleTabOverlayWithHeaderAndFooter;

import java.util.concurrent.Future;

public class DynamicSizeTabOverlayView<TabView extends de.codecrafter47.taboverlay.TabView> extends TabOverlayView<DynamicSizeTabOverlayTemplate, SimpleTabOverlayWithHeaderAndFooter> {

    private final ContentView contentView;

    public DynamicSizeTabOverlayView(de.codecrafter47.taboverlay.TabView tabView, DynamicSizeTabOverlayTemplate tabOverlayTemplate, SimpleTabOverlayWithHeaderAndFooter simpleTabOverlayWithHeaderAndFooter, Context context) {
        super(tabView, tabOverlayTemplate, simpleTabOverlayWithHeaderAndFooter, context);

        contentView = new ContentView(new PlayersComponentView(tabOverlayTemplate.getPlayerSet(),
                tabOverlayTemplate.getPlayerComponent(),
                tabOverlayTemplate.getPlayerComponent().getLayoutInfo().getMinSize(),
                tabOverlayTemplate.getMorePlayersComponent(),
                tabOverlayTemplate.getMorePlayersComponent().getLayoutInfo().getMinSize(),
                IconTemplate.STEVE.instantiate(),
                TextTemplate.EMPTY.instantiate(),
                PingTemplate.ZERO.instantiate(),
                tabOverlayTemplate.getPlayerOrder()));
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

        int newSize = Integer.min(contentView.getPreferredSize(), tablist.getMaxSize());

        if (newSize != tablist.getSize()) {
            tablist.setSize(newSize);
            contentView.updateArea(RectangularArea.of(tablist));
            return true;
        }
        return false;
    }

    private class ContentView extends ComponentView {
        private final ComponentView content;
        private Future<?> updateFuture = null;

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
            if (updateFuture == null || updateFuture.isDone()) {
                updateFuture = getContext().getTabEventQueue().submit(this::update);
            }
        }

        private void update() {
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
            if (updateFuture != null) {
                updateFuture.cancel(false);
            }
        }
    }
}
