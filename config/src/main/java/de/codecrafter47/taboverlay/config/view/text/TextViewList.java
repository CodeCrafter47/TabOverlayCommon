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

package de.codecrafter47.taboverlay.config.view.text;

import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A Text view composed of multiple text views
 */
public class TextViewList extends AbstractActiveElement<TextViewUpdateListener> implements TextView {
    private final List<TextView> children;
    private final StringBuilder builder = new StringBuilder();

    public TextViewList(ArrayList<TextView> children) {
        this.children = children;
    }

    @Override
    public String getText() {
        builder.setLength(0);
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            builder.append(child.getText());
        }
        return builder.toString();
    }

    @Override
    protected void onActivation() {
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            child.activate(getContext(), hasListener() ? getListener() : null);
        }
    }

    @Override
    protected void onDeactivation() {
        for (int i = 0; i < children.size(); i++) {
            TextView child = children.get(i);
            child.deactivate();
        }
    }
}
