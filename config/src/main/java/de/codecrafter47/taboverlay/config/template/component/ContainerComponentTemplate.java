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

import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import de.codecrafter47.taboverlay.config.view.components.ContainerComponentView;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ContainerComponentTemplate implements ComponentTemplate {

    private ComponentTemplate content;
    private boolean fillSlotsVertical;
    private int minSize;
    private int maxSize; // -1 is used to denote no limit
    private int columns;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(content.getLayoutInfo().isConstantSize() || minSize == maxSize)
                .minSize(maxSize != -1
                        ? Integer.max(content.getLayoutInfo().getMinSize(), maxSize)
                        : content.getLayoutInfo().getMinSize())
                .blockAligned(fillSlotsVertical || content.getLayoutInfo().isBlockAligned())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        return new ContainerComponentView(content.instantiate(), fillSlotsVertical, minSize, maxSize, columns, false);
    }
}
