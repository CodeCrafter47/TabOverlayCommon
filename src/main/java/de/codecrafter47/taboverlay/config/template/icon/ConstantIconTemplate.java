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

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.view.icon.IconViewConstant;
import de.codecrafter47.taboverlay.config.view.icon.IconView;

public class ConstantIconTemplate implements IconTemplate {

    private final IconViewConstant view;

    public ConstantIconTemplate(Icon icon) {
        view = new IconViewConstant(icon);
    }
    @Override
    public IconView instantiate() {
        return view;
    }
}
