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

package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import javax.annotation.Nullable;
import java.util.concurrent.Future;

/**
 * Base class for all ComponentViews, implements some common functionality.
 */
public abstract class ComponentView extends AbstractActiveElement<ComponentView> {

    @Nullable
    private Area area = null;

    @Override
    protected void onActivation() {

    }

    protected abstract void onAreaUpdated();

    protected abstract void requestLayoutUpdate(ComponentView source);

    public abstract int getMinSize();

    public abstract int getPreferredSize();

    public abstract int getMaxSize();

    public abstract boolean isBlockAligned();

    @Override
    protected void onDeactivation() {
        this.area = null;
    }

    public final void updateArea(Area area) {
        this.area = area;
        onAreaUpdated();
    }

    @Nullable
    protected final Area getArea() {
        return this.area;
    }
}
