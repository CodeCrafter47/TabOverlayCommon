package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.OperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import de.codecrafter47.taboverlay.handler.TabOverlay;
import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * All methods of this class can be expected to be called in a thread safe manner.
 */
public abstract class AbstractTabOverlayProvider<T extends TabOverlay> extends TabOverlayProvider {
    private final OperationMode<? extends T> mode;

    protected AbstractTabOverlayProvider(@Nonnull @NonNull String name, int priority, @Nonnull @NonNull OperationMode<? extends T> mode) {
        super(name, priority);
        this.mode = mode;
    }

    /**
     * Called when this {@link AbstractTabOverlayProvider} is activated for a specific {@link TabView}.
     *
     * @param tabView        the tab view
     * @param tabOverlayHandler the tab list handler providing access to the tab list
     */
    @Override
    protected final void activate(TabView tabView, TabOverlayHandler tabOverlayHandler) {
        activate(tabView, tabOverlayHandler.enterOperationMode(mode));
    }

    /**
     * Called when this {@link AbstractTabOverlayProvider} is activated for a specific {@link TabView}.
     *
     * @param tabView    the tab view
     * @param tabOverlay the tab overlay providing access to the tab list
     */
    protected abstract void activate(TabView tabView, T tabOverlay);

}
