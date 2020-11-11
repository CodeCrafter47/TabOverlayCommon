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
import de.codecrafter47.taboverlay.config.placeholder.*;
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
    private MarkedStringProperty effect;

    @Getter
    @Setter
    private MarkedListProperty<MarkedStringProperty> colors;

    @Getter
    @Setter
    private MarkedStringProperty baseColor;

    @Getter
    @Setter
    private MarkedStringProperty effectColor;

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

        String effect = this.effect != null ? this.effect.getValue() : null;
        if (effect == null) {
            effect = "rainbow";
        }

        boolean requireColors = false;
        boolean requireBaseColor = false;
        boolean requireEffectColor = false;

        switch (effect) {
            case "rainbow":
            case "random":
                requireColors = true;
                break;
            case "wave":
            case "waveCenter":
            case "glitter":
                requireBaseColor = true;
                requireEffectColor = true;
                break;
            default:
                tcc.getErrorHandler().addWarning("Unknown effect in !color_animation custom placeholder", this.effect.getStartMark());
        }

        List<TextColor> colors = new ArrayList<>();
        if (requireColors
                && ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "colors", this.colors, getStartMark())
                && ConfigValidationUtil.checkNotEmpty(tcc, "!color_animation custom placeholder", "colors", this.colors, this.colors.getStartMark())) {

            for (MarkedStringProperty color : this.colors) {
                if (color != null) {
                    colors.add(TextColor.parse(color.getValue(), tcc, color.getStartMark()));
                }
            }
        }

        TextColor baseColor = TextColor.COLOR_BLACK;
        if (requireBaseColor
                && ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "baseColor", this.baseColor, getStartMark())
                && ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "baseColor", this.baseColor.getValue(), this.baseColor.getStartMark())) {
            baseColor = TextColor.parse(this.baseColor.getValue(), tcc, this.baseColor.getStartMark());
        }

        TextColor effectColor = TextColor.COLOR_BLACK;
        if (requireEffectColor
                && ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "effectColor", this.effectColor, getStartMark())
                && ConfigValidationUtil.checkNotNull(tcc, "!color_animation custom placeholder", "effectColor", this.effectColor.getValue(), this.effectColor.getStartMark())) {
            effectColor = TextColor.parse(this.effectColor.getValue(), tcc, this.effectColor.getStartMark());
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
        TextColor finalBaseColor = baseColor;
        TextColor finalEffectColor = effectColor;


        switch (effect) {
            case "rainbow":
                return builder.acquireData(() -> {
                    return new CustomPlaceholderColorAnimationRainbow(textTemplate, colors, finalDistance, finalSpeed);
                }, TypeToken.STRING, textTemplate.requiresViewerContext());
            case "random":
                return builder.acquireData(() -> {
                    return new CustomPlaceholderColorAnimationRandom(textTemplate, colors);
                }, TypeToken.STRING, textTemplate.requiresViewerContext());
            case "wave":
                return builder.acquireData(() -> {
                    return new CustomPlaceholderColorAnimationWave(textTemplate, finalBaseColor, finalEffectColor, finalSpeed);
                }, TypeToken.STRING, textTemplate.requiresViewerContext());
            case "waveCenter":
                return builder.acquireData(() -> {
                    return new CustomPlaceholderColorAnimationWaveCenter(textTemplate, finalBaseColor, finalEffectColor, finalSpeed);
                }, TypeToken.STRING, textTemplate.requiresViewerContext());
            case "glitter":
                return builder.acquireData(() -> {
                    return new CustomPlaceholderColorAnimationGlitter(textTemplate, finalBaseColor, finalEffectColor);
                }, TypeToken.STRING, textTemplate.requiresViewerContext());
            default:
                return builder;
        }
    }
}
