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

package de.codecrafter47.taboverlay.config.view;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.template.RectangularTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.handler.RectangularTabOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RectangularContentView extends ComponentView {
    private final ComponentView content;
    private final RectangularTabOverlay contentHandle;

    private List<RectangularTabOverlay.Dimension> possibleSizes = null;

    public RectangularContentView(TabView tabView, RectangularTabOverlayTemplate template, RectangularTabOverlay contentHandle) {
        this.contentHandle = contentHandle;
        if (template.getSize() != -1) {
            for (RectangularTabOverlay.Dimension dimension : contentHandle.getSupportedSizes()) {
                if (dimension.getSize() == template.getSize()) {
                    possibleSizes = Collections.singletonList(dimension);
                    break;
                }
            }
            if (possibleSizes == null) {
                tabView.getLogger().severe("Size " + template.getSize() + " defined in " + template.getPath().getFileName().toString() + " is not supported by client.");
                possibleSizes = Collections.emptyList();
            }
        } else {
            possibleSizes = new ArrayList<>();
            for (RectangularTabOverlay.Dimension dimension : contentHandle.getSupportedSizes()) {
                if (dimension.getColumns() == template.getColumns()) {
                    possibleSizes.add(dimension);
                }
            }
        }
        this.content = template.getContentRoot().instantiate();
    }

    @Override
    protected void onActivation() {
        content.activate(getContext(), this);

        if (!updateTabListSize()) {
            this.updateArea(RectangularArea.of(contentHandle));
        }
    }

    @Override
    protected void onAreaUpdated() {
        content.updateArea(getArea());
    }

    private boolean updateTabListSize() {

        RectangularTabOverlay.Dimension bestFit = contentHandle.getSize();
        // todo make more efficient
        for (RectangularTabOverlay.Dimension size : possibleSizes) {
            if (size.getSize() < this.getPreferredSize() && size.getSize() > bestFit.getSize()) {
                bestFit = size;
            }
            if (size.getSize() >= this.getPreferredSize() && bestFit.getSize() < this.getPreferredSize()) {
                bestFit = size;
            }
            if (size.getSize() < bestFit.getSize() && size.getSize() >= this.getPreferredSize()) {
                bestFit = size;
            }
        }

        if (bestFit != contentHandle.getSize()) {
            contentHandle.setSize(bestFit);
            this.updateArea(RectangularArea.of(contentHandle));
            return true;
        }
        return false;
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        if (!updateTabListSize()) {
            this.updateArea(this.getArea());
        }
    }

    @Override
    public int getMinSize() {
        return content.getMinSize();
    }

    @Override
    public int getPreferredSize() {
        return content.getPreferredSize();
    }

    @Override
    public int getMaxSize() {
        return content.getMaxSize();
    }

    @Override
    public boolean isBlockAligned() {
        return content.isBlockAligned();
    }

    @Override
    protected void onDeactivation() {
        content.deactivate();
    }
}
