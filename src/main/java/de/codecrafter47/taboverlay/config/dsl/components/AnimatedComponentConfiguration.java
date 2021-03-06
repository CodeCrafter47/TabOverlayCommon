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

package de.codecrafter47.taboverlay.config.dsl.components;

import de.codecrafter47.taboverlay.config.dsl.ComponentConfiguration;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedFloatProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedListProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.AnimatedComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AnimatedComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private MarkedListProperty<ComponentConfiguration> components;
    private boolean randomize = false;
    private MarkedFloatProperty interval;

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {

        if ((ConfigValidationUtil.checkNotNull(tcc, "!animated component", "components", components, getStartMark())
                && ConfigValidationUtil.checkNotEmpty(tcc, "!animated component", "components", components, components.getStartMark()))
                & ConfigValidationUtil.checkNotNull(tcc, "!animated component", "interval", interval, getStartMark())
                && ConfigValidationUtil.checkRange(tcc, "!animated component", "interval", interval.getValue(), 0.05f, 9999f, interval.getStartMark())) {

            List<ComponentTemplate> componentTemplates = new ArrayList<>(components.size());
            for (ComponentConfiguration component : components) {
                if (component == null) {
                    componentTemplates.add(tcc.emptySlot());
                } else {
                    componentTemplates.add(component.toTemplate(tcc));
                }
            }

            // check child templates for constant size
            if (!componentTemplates.get(0).getLayoutInfo().isConstantSize()) {
                tcc.getErrorHandler().addError("Animated components can only contain components of constant size.", components.get(0).getStartMark());
            }
            int size = componentTemplates.get(0).getLayoutInfo().getMinSize();
            boolean blockAligned = componentTemplates.get(0).getLayoutInfo().isBlockAligned();

            for (int i = 1; i < componentTemplates.size(); i++) {
                if (!componentTemplates.get(i).getLayoutInfo().isConstantSize()) {
                    tcc.getErrorHandler().addError("Animated components can only contain components of constant size.", (components.get(i) != null ? components.get(i) : components).getStartMark());
                }
                if (componentTemplates.get(i).getLayoutInfo().getMinSize() != size) {
                    tcc.getErrorHandler().addError("Animated components can only contain components of the same size.", (components.get(i) != null ? components.get(i) : components).getStartMark());
                }
                if (componentTemplates.get(i).getLayoutInfo().isBlockAligned() != blockAligned) {
                    tcc.getErrorHandler().addError("Animated components can only contain components with the same alignment requirement.", (components.get(i) != null ? components.get(i) : components).getStartMark());
                }
            }

            return AnimatedComponentTemplate.builder()
                    .components(componentTemplates)
                    .interval(interval.getValue())
                    .randomize(randomize)
                    .build();
        } else {
            return tcc.emptyComponent(); // dummy component
        }
    }
}
