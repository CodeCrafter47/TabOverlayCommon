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

package de.codecrafter47.taboverlay.config.template.component;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ConditionalComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConditionalComponentTemplate implements ComponentTemplate {
    private ExpressionTemplate condition;
    private ComponentTemplate trueReplacement;
    private ComponentTemplate falseReplacement;

    @Override
    public LayoutInfo getLayoutInfo() {
        LayoutInfo layout1 = trueReplacement.getLayoutInfo();
        LayoutInfo layout2 = falseReplacement.getLayoutInfo();
        return LayoutInfo.builder()
                .constantSize(layout1.isConstantSize() && layout2.isConstantSize() && layout1.getMinSize() == layout2.getMinSize())
                .minSize(Integer.max(layout1.getMinSize(), layout2.getMinSize()))
                .blockAligned(layout1.isBlockAligned() || layout2.isBlockAligned())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new ConditionalComponentView(condition.instantiateWithBooleanResult(), trueReplacement, falseReplacement);
    }
}
