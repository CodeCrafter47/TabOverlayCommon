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

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.text.TextView;

public final class SpacerComponentView extends ComponentView implements DefaultSlotHandler.Listener {

    private final DefaultSlotHandler defaultSlotHandler;

    public SpacerComponentView(TextView textView, PingView pingView, IconView iconView) {
        this.defaultSlotHandler = new DefaultSlotHandler(textView, pingView, iconView);
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        defaultSlotHandler.activate(getContext(), this);
    }

    @Override
    protected void onAreaUpdated() {
        updateSlots();
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        throw new AssertionError("There ain't no one calling this method");
    }

    private void updateSlots() {
        Area area = getArea();
        if (area != null) {
            String text = defaultSlotHandler.getText();
            int ping = defaultSlotHandler.getPing();
            Icon icon = defaultSlotHandler.getIcon();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setSlot(i, icon, text, ping);
            }
        }
    }

    @Override
    public int getMinSize() {
        return 0;
    }

    @Override
    public int getPreferredSize() {
        return 0;
    }

    @Override
    public int getMaxSize() {
        // just return a high value, higher than the maximum number of slots on the tab list,
        // but not high enough to possibly cause an integer overflow in other components doing
        // calculations with the value.
        return 360;
    }

    @Override
    public boolean isBlockAligned() {
        return false;
    }

    @Override
    protected void onDeactivation() {
        super.onDeactivation();
        defaultSlotHandler.deactivate();
    }

    @Override
    public void onDefaultSlotTextUpdated() {
        Area area = getArea();
        if (area != null) {
            String text = defaultSlotHandler.getText();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setText(i, text);
            }
        }

    }

    @Override
    public void onDefaultSlotPingUpdated() {
        Area area = getArea();
        if (area != null) {
            int ping = defaultSlotHandler.getPing();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setPing(i, ping);
            }
        }
    }

    @Override
    public void onDefaultSlotIconUpdated() {
        Area area = getArea();
        if (area != null) {
            Icon icon = defaultSlotHandler.getIcon();
            // reverse direction improves efficiency as getSize is constant
            for (int i = area.getSize() - 1; i >= 0; i--) {
                area.setIcon(i, icon);
            }
        }
    }
}
