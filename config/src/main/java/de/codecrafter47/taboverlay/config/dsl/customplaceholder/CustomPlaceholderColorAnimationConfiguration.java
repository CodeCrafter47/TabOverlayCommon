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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedIntegerProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedListProperty;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.misc.TextColor;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderColorAnimation;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class CustomPlaceholderColorAnimationConfiguration extends CustomPlaceholderConfiguration {

    @Getter
    @Setter
    private MarkedListProperty<MarkedStringProperty> colors;

    @Getter
    @Setter
    private MarkedIntegerProperty distance;

    @Getter
    @Setter
    private MarkedFloatProperty speed;

    public CustomPlaceholderColorAnimationConfiguration() {
        setParameters(new MarkedIntegerProperty(1));
    }

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
        List<TextColor> colors = new ArrayList<>();

        if (ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "colors", this.colors, getStartMark())
                && ConfigValidationUtil.checkNotEmpty(tcc, "!color_animation custom placeholder", "colors", this.colors, this.colors.getStartMark())) {

            for (MarkedStringProperty color : this.colors) {
                if (color != null) {
                    colors.add(TextColor.parse(color.getValue(), tcc, color.getStartMark()));
                }
            }
        }

        OptionalInt distance = OptionalInt.empty();
        if (this.distance != null) {
            distance = OptionalInt.of(this.distance.getValue());
        }

        float speed = 2.0f;
        if (this.speed != null) {
            speed = this.speed.getValue();
        }

        TextTemplate textTemplate = TextTemplate.parse(replaceParameters("%0", args), getStartMark(), tcc);

        OptionalInt finalDistance = distance;
        float finalSpeed = speed;
        return builder.acquireData(() -> new CustomPlaceholderColorAnimation(textTemplate, colors, finalDistance, finalSpeed), TypeToken.STRING, textTemplate.requiresViewerContext());
    }
}
