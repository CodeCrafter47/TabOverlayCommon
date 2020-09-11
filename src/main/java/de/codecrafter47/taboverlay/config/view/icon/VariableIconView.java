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

package de.codecrafter47.taboverlay.config.view.icon;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.ErrorHandler;
import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.template.icon.IconTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import lombok.NonNull;

public class VariableIconView extends AbstractActiveElement<IconViewUpdateListener> implements IconView {
    private static final ErrorHandler errorHandlerDummy = new ErrorHandler();
    private final TextView textView;
    private final IconManager iconManager;
    private IconView iconView;

    public VariableIconView(@NonNull TextView textView, IconManager iconManager) {
        this.textView = textView;
        this.iconManager = iconManager;
    }

    @Override
    protected void onActivation() {
        textView.activate(getContext(), this::update);

        IconTemplate iconTemplate = iconManager.createIconTemplate(textView.getText(), null, errorHandlerDummy);
        iconView = iconTemplate.instantiate();
        iconView.activate(getContext(), getListener());
    }

    private void update() {
        iconView.deactivate();

        IconTemplate iconTemplate = iconManager.createIconTemplate(textView.getText(), null, errorHandlerDummy);
        iconView = iconTemplate.instantiate();
        iconView.activate(getContext(), getListener());

        if (hasListener()) {
            getListener().onIconUpdated();
        }
    }

    @Override
    protected void onDeactivation() {
        iconView.deactivate();
        textView.deactivate();
    }

    @Override
    public Icon getIcon() {
        return iconView.getIcon();
    }
}
