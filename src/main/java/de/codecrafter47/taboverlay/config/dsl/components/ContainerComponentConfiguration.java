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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ContainerComponentTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ContainerComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    @Getter
    @Setter
    private boolean fillSlotsVertical = false;
    @Getter
    @Setter
    MarkedIntegerProperty minSize;
    @Getter
    @Setter
    MarkedIntegerProperty maxSize;
    private ListComponentConfiguration components;

    public List<ComponentConfiguration> getComponents() {
        // dummy method to support bean property recognition
        throw new UnsupportedOperationException("dummy method");
    }

    public void setComponents(List<ComponentConfiguration> components) {
        this.components = new ListComponentConfiguration();
        this.components.addAll(components);
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {

        if (minSize != null && minSize.getValue() < 0) {
            tcc.getErrorHandler().addError("Failed to configure container component. MinSize is negative", minSize.getStartMark());
        }

        if (maxSize != null && (minSize != null ? minSize.getValue() : 0) > maxSize.getValue()) {
            tcc.getErrorHandler().addError("Failed to configure container component. MaxSize is lower than minSize", maxSize.getStartMark());
        }

        TemplateCreationContext childContext;
        if (fillSlotsVertical) {
            childContext = tcc.clone();
            childContext.setColumns(1);
        } else {
            childContext = tcc;
        }

        ComponentTemplate content = tcc.emptyComponent(); // dummy
        if (ConfigValidationUtil.checkNotNull(tcc, "!container component", "components", components, getStartMark())) {
            content = this.components.toTemplate(childContext);
        }

        int contentMinSize = content.getLayoutInfo().getMinSize();
        if (maxSize != null && contentMinSize > maxSize.getValue()) {
            tcc.getErrorHandler().addWarning("maxSize of !container set to " + maxSize.getValue() + " but content will require at least " + contentMinSize + " slots.", maxSize.getStartMark());
        }

        return ContainerComponentTemplate.builder()
                .content(content)
                .fillSlotsVertical(fillSlotsVertical)
                .minSize(this.minSize != null ? this.minSize.getValue() : 0)
                .maxSize(maxSize != null ? maxSize.getValue() : -1)
                .columns(tcc.getColumns().orElse(1))
                .build();
    }
}
