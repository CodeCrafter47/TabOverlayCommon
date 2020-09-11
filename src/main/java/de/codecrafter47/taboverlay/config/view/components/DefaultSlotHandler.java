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
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

class DefaultSlotHandler extends AbstractActiveElement<DefaultSlotHandler.Listener> implements ActiveElement, TextViewUpdateListener, PingViewUpdateListener, IconViewUpdateListener {

    private final TextView defaultTextView;
    private final PingView defaultPingView;
    private final IconView defaultIconView;

    DefaultSlotHandler(TextView defaultTextView, PingView defaultPingView, IconView defaultIconView) {
        this.defaultTextView = defaultTextView;
        this.defaultPingView = defaultPingView;
        this.defaultIconView = defaultIconView;
    }

    @Override
    protected void onActivation() {
        defaultTextView.activate(getContext(), this);
        defaultPingView.activate(getContext(), this);
        defaultIconView.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        defaultTextView.deactivate();
        defaultPingView.deactivate();
        defaultIconView.deactivate();
    }

    String getText() {
        return defaultTextView.getText();
    }

    int getPing() {
        return defaultPingView.getPing();
    }

    Icon getIcon() {
        return defaultIconView.getIcon();
    }

    @Override
    public void onTextUpdated() {
        getListener().onDefaultSlotTextUpdated();
    }

    @Override
    public void onPingUpdated() {
        getListener().onDefaultSlotPingUpdated();
    }

    @Override
    public void onIconUpdated() {
        getListener().onDefaultSlotIconUpdated();
    }

    interface Listener {
        void onDefaultSlotTextUpdated();
        void onDefaultSlotPingUpdated();
        void onDefaultSlotIconUpdated();
    }
}
