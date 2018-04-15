package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Getter;

import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * A tab view.
 */
public class TabView {

    @Getter
    private final Logger logger;

    @Getter
    private final TabOverlayProviderSet tabOverlayProviders;

    public TabView(TabOverlayHandler tabOverlayHandler, Logger logger, Executor updateExecutor) {
        this.logger = logger;
        tabOverlayProviders = new TabOverlayProviderSet(this, updateExecutor, tabOverlayHandler);
    }

    /**
     * Deactivates the tab view.
     */
    protected void deactivate() {
        tabOverlayProviders.deactivate();
    }
}
