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
