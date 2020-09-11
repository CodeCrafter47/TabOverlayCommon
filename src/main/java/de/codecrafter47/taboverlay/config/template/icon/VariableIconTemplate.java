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

package de.codecrafter47.taboverlay.config.template.icon;

import de.codecrafter47.taboverlay.config.icon.IconManager;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.VariableIconView;

public class VariableIconTemplate implements IconTemplate {
    private final TextTemplate template;
    private final IconManager iconManager;

    public VariableIconTemplate(TextTemplate template, IconManager iconManager) {
        this.template = template;
        this.iconManager = iconManager;
    }

    @Override
    public IconView instantiate() {
        return new VariableIconView(template.instantiate(), iconManager);
    }
}
