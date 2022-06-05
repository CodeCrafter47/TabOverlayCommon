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

import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.template.DynamicSizeTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.template.ping.PingTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.PlayersComponentView;
import de.codecrafter47.taboverlay.handler.SimpleTabOverlay;

import java.util.concurrent.Future;

public class DynamicSizeContentView extends ComponentView {

    private final SimpleTabOverlay contentHandle;
    private final ComponentView content;
    private Future<?> updateFuture = null;

    public DynamicSizeContentView(DynamicSizeTabOverlayTemplate template, SimpleTabOverlay contentHandle) {
        this.contentHandle = contentHandle;
        this.content = new PlayersComponentView(template.getPlayerSet(),
                template.getPlayerComponent(),
                template.getPlayerComponent().getLayoutInfo().getMinSize(),
                template.getMorePlayersComponent(),
                template.getMorePlayersComponent().getLayoutInfo().getMinSize(),
                IconTemplate.STEVE.instantiate(),
                TextTemplate.EMPTY.instantiate(),
                PingTemplate.ZERO.instantiate(),
                template.getPlayerOrder());
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

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        if (updateFuture == null || updateFuture.isDone()) {
            updateFuture = getContext().getTabEventQueue().submit(this::update);
        }
    }

    private boolean updateTabListSize() {

        int newSize = Integer.min(this.getPreferredSize(), contentHandle.getMaxSize());

        if (newSize != contentHandle.getSize()) {
            contentHandle.setSize(newSize);
            this.updateArea(RectangularArea.of(contentHandle));
            return true;
        }
        return false;
    }

    private void update() {
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
        if (updateFuture != null) {
            updateFuture.cancel(false);
        }
    }
}
