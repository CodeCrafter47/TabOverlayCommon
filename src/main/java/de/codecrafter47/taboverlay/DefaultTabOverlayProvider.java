package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlay;

public final class DefaultTabOverlayProvider extends AbstractTabOverlayProvider<TabOverlay> {

    private static final DefaultTabOverlayProvider INSTANCE = new DefaultTabOverlayProvider();

    public static DefaultTabOverlayProvider getInstance() {
        return INSTANCE;
    }

    private DefaultTabOverlayProvider() {
        super("default", Integer.MIN_VALUE, OperationMode.PASS_TROUGH);
    }

    @Override
    public void attach(TabView tabView) {
        // nothing to do here
    }

    @Override
    public void detach(TabView tabView) {
        // nothing to do here
    }

    @Override
    protected void activate(TabView tabView, TabOverlay tabOverlay) {
        // nothing to do here
    }

    @Override
    public void deactivate(TabView tabView) {
        // nothing to do here
    }

    @Override
    public boolean shouldActivate(TabView tabView) {
        // Always!
        return true;
    }
}
