package de.codecrafter47.taboverlay;

import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nonnull;

public abstract class TabOverlayProvider {
    @Getter
    protected final String name;
    @Getter
    protected final int priority;

    protected TabOverlayProvider(@Nonnull @NonNull String name, int priority) {
        this.priority = priority;
        this.name = name;
    }

    /**
     * Called after this {@link TabOverlayProvider} has been added to a
     * tab view.
     *
     * @param tabView the tab view
     */
    protected abstract void attach(TabView tabView);

    /**
     * Called after this {@link TabOverlayProvider} has been removed
     * from a tab view.
     *
     * @param tabView the tab view
     */
    protected abstract void detach(TabView tabView);

    protected abstract void activate(TabView tabView, TabOverlayHandler tabOverlayHandler);

    /**
     * Called when this {@link TabOverlayProvider} is removed from a specific {@link TabView}.
     *
     * @param tabView the tab view
     */
    protected abstract void deactivate(TabView tabView);

    /**
     * Called to check whether this {@link TabOverlayProvider} should be activated for a specific
     * {@link TabView}.
     * <p>
     * Note that this method is <em>not</em> invoked periodically. The code implementing this interface is expected to
     * call {@link TabOverlayProviderSet#update()} when its state is changed.
     *
     * @param tabView the tab view
     * @return true if it should be activated, false otherwise
     */
    protected abstract boolean shouldActivate(TabView tabView);
}
