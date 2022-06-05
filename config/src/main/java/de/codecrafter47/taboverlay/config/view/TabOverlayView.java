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
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandler;

public class TabOverlayView {

    private final AbstractActiveElement<?> contentView;
    private final AbstractActiveElement<?> headerFooterView;

    public static TabOverlayView create(TabView tabView, TabOverlayHandler handler, Context context, AbstractTabOverlayTemplate template) {
        AbstractActiveElement<?> headerFooterView;
        if (template.showHeaderAndFooter()) {
            headerFooterView = new HeaderAndFooterView(template, handler.enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode.CUSTOM));
        } else {
            handler.enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode.PASS_TROUGH);
            headerFooterView = null;
        }
        AbstractActiveElement<?> contentView = template.createContentView(tabView, handler);

        return new TabOverlayView(context, contentView, headerFooterView);
    }

    private TabOverlayView(Context context, AbstractActiveElement<?> contentView, AbstractActiveElement<?> headerFooterView) {
        this.contentView = contentView;
        this.headerFooterView = headerFooterView;

        this.contentView.activate(context, null);
        if (headerFooterView != null) {
            this.headerFooterView.activate(context, null);
        }

    }

    public void deactivate() {
        contentView.deactivate();
        if (headerFooterView != null) {
            headerFooterView.deactivate();
        }
    }
}
