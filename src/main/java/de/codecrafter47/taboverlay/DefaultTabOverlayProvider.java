package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.ContentOperationMode;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandle;

public final class DefaultTabOverlayProvider extends AbstractTabOverlayProvider<TabOverlayHandle, TabOverlayHandle> {

    private static final DefaultTabOverlayProvider INSTANCE = new DefaultTabOverlayProvider();

    public static DefaultTabOverlayProvider getInstance() {
        return INSTANCE;
    }

    private DefaultTabOverlayProvider() {
        super("default", Integer.MIN_VALUE, ContentOperationMode.PASS_TROUGH, HeaderAndFooterOperationMode.PASS_TROUGH);
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
    protected void activate(TabView tabView, TabOverlayHandle contentHandle, TabOverlayHandle headerAndFooterHandle) {
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
