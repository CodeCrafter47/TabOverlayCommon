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

    public TabView(Logger logger, Executor updateExecutor) {
        this.logger = logger;
        tabOverlayProviders = new TabOverlayProviderSet(this, updateExecutor);
    }

    public TabView(TabOverlayHandler tabOverlayHandler, Logger logger, Executor updateExecutor) {
        this(logger, updateExecutor);
        tabOverlayProviders.activate(tabOverlayHandler);
    }

    /**
     * Deactivates the tab view.
     */
    public void deactivate() {
        tabOverlayProviders.deactivate();
    }
}
