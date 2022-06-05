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

package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

public class CustomPlaceholderAlias extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, TextViewUpdateListener {

    private TextView replacement;

    public CustomPlaceholderAlias(TextTemplate replacement) {
        this.replacement = replacement.instantiate();
    }

    @Override
    public String getData() {
        return replacement.getText();
    }

    @Override
    protected void onActivation() {
        replacement.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        replacement.deactivate();
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }
}
