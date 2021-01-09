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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderSelect;
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderSwitch;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class CustomPlaceholderSelectConfiguration extends CustomPlaceholderConfiguration {
    private Map<MarkedStringProperty, MarkedStringProperty> replacements;
    private MarkedStringProperty defaultReplacement;

    // alias defaultReplacement as default
    public MarkedStringProperty getDefault() {
        return defaultReplacement;
    }

    public void setDefault(MarkedStringProperty defaultReplacement) {
        this.defaultReplacement = defaultReplacement;
    }

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {

        Map<ExpressionTemplate, TextTemplate> replacementMap = new LinkedHashMap<>(replacements.size());
        if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !select", "replacements", replacements, getStartMark())) {
            for (val entry : replacements.entrySet()) {
                if (entry.getKey() == null) {
                    tcc.getErrorHandler().addWarning("Replacement with missing or null key in !select custom placeholder", entry.getValue() != null ? entry.getValue().getStartMark() : getStartMark());
                } else if (entry.getKey().getValue() == null) {
                    tcc.getErrorHandler().addWarning("Replacement with missing or null key in !select custom placeholder", entry.getKey().getStartMark());
                } else {
                    // Parse expression
                    ExpressionTemplate expression = tcc.getExpressionEngine().compile(tcc, entry.getKey().getValue(), entry.getKey().getStartMark());

                    // Parse Replacement
                    TextTemplate replacement = TextTemplate.EMPTY;
                    if (entry.getValue() != null) {
                        try {
                            replacement = TextTemplate.parse(replaceParameters(entry.getValue().getValue(), args), entry.getValue().getStartMark(), tcc);
                        } catch (Exception e) {
                            tcc.getErrorHandler().addError("Failed to parse replacement for custom placeholder: " + e.getMessage(), entry.getValue().getStartMark());
                        }
                    }
                    replacementMap.put(expression, replacement);
                }
            }
        }
        TextTemplate defaultReplacement = TextTemplate.EMPTY;
        if (this.defaultReplacement != null) {
            defaultReplacement = TextTemplate.parse(replaceParameters(this.defaultReplacement.getValue(), args), this.defaultReplacement.getStartMark(), tcc);
        }
        TextTemplate finalDefaultReplacement = defaultReplacement;
        return builder.acquireData(() -> new CustomPlaceholderSelect(replacementMap, finalDefaultReplacement), TypeToken.STRING,
                finalDefaultReplacement.requiresViewerContext()
                        || replacementMap.keySet().stream().anyMatch(ExpressionTemplate::requiresViewerContext)
                        || replacementMap.values().stream().anyMatch(TextTemplate::requiresViewerContext));
    }
}
