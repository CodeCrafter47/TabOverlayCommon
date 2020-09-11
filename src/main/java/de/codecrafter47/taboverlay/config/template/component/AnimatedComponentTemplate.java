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

import de.codecrafter47.taboverlay.config.view.components.AnimatedComponentView;
import de.codecrafter47.taboverlay.config.view.components.ComponentView;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
public class AnimatedComponentTemplate implements ComponentTemplate {
    List<ComponentTemplate> components;
    float interval;
    boolean randomize;

    @Override
    public LayoutInfo getLayoutInfo() {
        return LayoutInfo.builder()
                .constantSize(true)
                .minSize(components.get(0).getLayoutInfo().getMinSize())
                .blockAligned(components.get(0).getLayoutInfo().isBlockAligned())
                .build();
    }

    @Override
    public ComponentView instantiate() {
        List<ComponentView> componentViews = new ArrayList<>(components.size());
        for (ComponentTemplate component : components) {
            componentViews.add(component.instantiate());
        }
        return new AnimatedComponentView(componentViews, interval, components.get(0).getLayoutInfo().getMinSize(), randomize);
    }
}
