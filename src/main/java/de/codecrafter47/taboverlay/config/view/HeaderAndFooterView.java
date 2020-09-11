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

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.AbstractTabOverlayTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewAnimated;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterHandle;

import java.util.List;

class HeaderAndFooterView extends AbstractActiveElement<Void> implements TextViewUpdateListener {

    private final TextView header;
    private final TextView footer;
    private final HeaderAndFooterHandle headerAndFooterHandle;

    public HeaderAndFooterView(AbstractTabOverlayTemplate tabOverlayTemplate, HeaderAndFooterHandle headerAndFooterHandle) {
        this.headerAndFooterHandle = headerAndFooterHandle;
        if (tabOverlayTemplate.getHeader() != null) {
            header = constructTextView(tabOverlayTemplate.getHeader(), tabOverlayTemplate.getHeaderAnimationUpdateInterval());
        } else {
            header = null;
        }
        if (tabOverlayTemplate.getFooter() != null) {
            footer = constructTextView(tabOverlayTemplate.getFooter(), tabOverlayTemplate.getFooterAnimationUpdateInterval());
        } else {
            footer = null;
        }
    }

    @Override
    protected void onActivation() {
        if (header != null) {
            header.activate(getContext(), this);
        }
        if (footer != null) {
            footer.activate(getContext(), this);
        }
        updateHeaderAndFooter();

    }

    @Override
    protected void onDeactivation() {
        if (header != null) {
            header.deactivate();
        }
        if (footer != null) {
            footer.deactivate();
        }
    }

    private void updateHeaderAndFooter() {
        headerAndFooterHandle.setHeaderFooter(header != null ? header.getText() : null, footer != null ? footer.getText() : null, '&');
    }

    private static TextView constructTextView(List<TextTemplate> templates, float interval) {
        if (templates == null || templates.isEmpty()) {
            return TextView.EMPTY;
        } else if (templates.size() == 1) {
            return templates.get(0).instantiate();
        } else {
            return new TextViewAnimated(interval, templates);
        }
    }

    @Override
    public void onTextUpdated() {
        updateHeaderAndFooter();
    }
}
