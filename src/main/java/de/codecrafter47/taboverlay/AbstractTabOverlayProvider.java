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

import de.codecrafter47.taboverlay.handler.ContentOperationMode;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandle;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;
import lombok.NonNull;

import javax.annotation.Nonnull;

/**
 * All methods of this class can be expected to be called in a thread safe manner.
 */
public abstract class AbstractTabOverlayProvider<T1 extends TabOverlayHandle, T2 extends TabOverlayHandle> extends TabOverlayProvider {
    private final ContentOperationMode<? extends T1> contentMode;
    private final HeaderAndFooterOperationMode<? extends T2> headerAndFooterMode;

    protected AbstractTabOverlayProvider(@Nonnull @NonNull String name, int priority, @Nonnull @NonNull ContentOperationMode<? extends T1> contentMode, @Nonnull @NonNull HeaderAndFooterOperationMode<? extends T2> headerAndFooterMode) {
        super(name, priority);
        this.contentMode = contentMode;
        this.headerAndFooterMode = headerAndFooterMode;
    }

    /**
     * Called when this {@link AbstractTabOverlayProvider} is activated for a specific {@link TabView}.
     *  @param tabView           the tab view
     * @param tabOverlayHandler the tab list handler providing access to the tab list
     */
    @Override
    protected final void activate(TabView tabView, TabOverlayHandler tabOverlayHandler) {
        activate(tabView, tabOverlayHandler.enterContentOperationMode(contentMode), tabOverlayHandler.enterHeaderAndFooterOperationMode(headerAndFooterMode));
    }

    /**
     * Called when this {@link AbstractTabOverlayProvider} is activated for a specific {@link TabView}.
     *
     * @param tabView               the tab view
     * @param contentHandle         the handle providing access to the tab list content
     * @param headerAndFooterHandle the handle providing access to the tab overlay header and footer
     */
    protected abstract void activate(TabView tabView, T1 contentHandle, T2 headerAndFooterHandle);

}
