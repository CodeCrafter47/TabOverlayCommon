/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.codecrafter47.taboverlay;

import com.google.common.base.Preconditions;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * {@link AbstractTabOverlayProvider} bound to a single {@link TabView}.
 */
public abstract class AbstractPlayerTabOverlayProvider extends TabOverlayProvider {
    private final TabView tabView;

    /**
     * Constructor.
     *
     * @param tabView the {@link TabView}
     */
    public AbstractPlayerTabOverlayProvider(@Nonnull @NonNull TabView tabView, @Nonnull @NonNull String name, int priority) {
        super(name, priority);
        this.tabView = tabView;
    }

    public final TabView getTabView() {
        return tabView;
    }

    @Override
    protected final void attach(TabView tabView) {
        Preconditions.checkState(tabView == getTabView(), "An AbstractPlayerTabOverlayProvider cannot be shared among multiple tab views.");
        onAttach();
    }

    @Override
    protected final void detach(TabView tabView) {
        Preconditions.checkState(tabView == getTabView(), "An AbstractPlayerTabOverlayProvider cannot be shared among multiple tab views.");
        onDetach();
    }

    @Override
    protected final void activate(TabView tabView, TabOverlayHandler handler) {
        Preconditions.checkState(tabView == getTabView(), "An AbstractPlayerTabOverlayProvider cannot be shared among multiple tab views.");
        onActivate(handler);
    }

    @Override
    protected final void deactivate(TabView tabView) {
        Preconditions.checkState(tabView == getTabView(), "An AbstractPlayerTabOverlayProvider cannot be shared among multiple tab views.");
        onDeactivate();
    }

    @Override
    protected final boolean shouldActivate(TabView tabView) {
        Preconditions.checkState(tabView == getTabView(), "An AbstractPlayerTabOverlayProvider cannot be shared among multiple tab views.");
        return shouldActivate();
    }

    /**
     * Called after this {@link AbstractPlayerTabOverlayProvider} has been added to the tab view.
     */
    protected abstract void onAttach();

    /**
     * Called after this {@link AbstractPlayerTabOverlayProvider} has been activated.
     *
     * @param handler the tab overlay handler
     */
    protected abstract void onActivate(TabOverlayHandler handler);

    /**
     * Called after this {@link AbstractPlayerTabOverlayProvider} has been deactivated. At this point calls to the tab
     * overlay handle passed to {@link #onActivate(TabOverlayHandler)} no longer modify the players tab list.
     */
    protected abstract void onDeactivate();

    /**
     * Called after this {@link AbstractPlayerTabOverlayProvider} has been removed from the tab view.
     * <p>
     * This method should free any resources associated with this {@link AbstractPlayerTabOverlayProvider}.
     */
    protected abstract void onDetach();

    /**
     * Called to check whether this {@link AbstractPlayerTabOverlayProvider} should be activated.
     * <p>
     * Note that this method is <em>not</em> invoked periodically. The code implementing this interface is expected to
     * call {@link TabOverlayProviderSet#scheduleUpdate()} when its state is changed.
     *
     * @return true if it should be activated, false otherwise
     */
    protected abstract boolean shouldActivate();
}
