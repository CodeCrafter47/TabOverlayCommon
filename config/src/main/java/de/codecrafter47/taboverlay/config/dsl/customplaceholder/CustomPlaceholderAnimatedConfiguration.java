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

package de.codecrafter47.taboverlay.config.dsl.customplaceholder;

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.util.ConfigValidationUtil;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedFloatProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedListProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderAnimated;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CustomPlaceholderAnimatedConfiguration extends CustomPlaceholderConfiguration {

    @Getter
    @Setter
    private MarkedListProperty<MarkedStringProperty> elements;

    @Getter
    @Setter
    private MarkedFloatProperty interval;

    @Getter
    @Setter
    private boolean randomize = false;

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
        List<TextTemplate> elementTemplates = new ArrayList<>(elements.size());
        if ((ConfigValidationUtil.checkNotNull(tcc, "!animated custom placeholder", "elements", elements, getStartMark())
                && ConfigValidationUtil.checkNotEmpty(tcc, "!animated custom placeholder", "elements", elements, elements.getStartMark()))
                & ConfigValidationUtil.checkNotNull(tcc, "!animated custom placeholder", "interval", interval, getStartMark())
                && ConfigValidationUtil.checkRange(tcc, "!animated custom placeholder", "interval", interval.getValue(), 0.05f, 9999f, interval.getStartMark())) {

            for (MarkedStringProperty element : elements) {
                if (element == null) {
                    elementTemplates.add(TextTemplate.EMPTY);
                } else {
                    elementTemplates.add(TextTemplate.parse(replaceParameters(element.getValue(), args), element.getStartMark(), tcc));
                }
            }
        }
        return builder.acquireData(() -> new CustomPlaceholderAnimated(elementTemplates, interval.getValue(), randomize), TypeToken.STRING, elementTemplates.stream().anyMatch(TextTemplate::requiresViewerContext));
    }
}
