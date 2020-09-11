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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ListComponentTemplate;
import lombok.val;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class ListComponentConfiguration extends ArrayList<ComponentConfiguration> implements ComponentConfiguration {

    private final MarkedPropertyBase delegate = new MarkedPropertyBase();

    @Override
    public Mark getStartMark() {
        return delegate.getStartMark();
    }

    @Override
    public void setStartMark(Mark startMark) {
        delegate.setStartMark(startMark);
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {

        val componentTemplates = new ArrayList<ComponentTemplate>(size());

        for (val componentConfiguration : this) {
            if (componentConfiguration != null) {
                componentTemplates.add(componentConfiguration.toTemplate(tcc));
            } else {
                componentTemplates.add(tcc.emptySlot());
            }
        }

        return ListComponentTemplate.builder()
                .components(componentTemplates)
                .columns(tcc.getColumns().orElse(1))
                .defaultIcon(tcc.getDefaultIcon())
                .defaultText(tcc.getDefaultText())
                .defaultPing(tcc.getDefaultPing())
                .build();
    }
}
