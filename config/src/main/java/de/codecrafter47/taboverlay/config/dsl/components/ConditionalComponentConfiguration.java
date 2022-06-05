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
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedPropertyBase;
import de.codecrafter47.taboverlay.config.dsl.yaml.MarkedStringProperty;
import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;
import de.codecrafter47.taboverlay.config.template.component.ConditionalComponentTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConditionalComponentConfiguration extends MarkedPropertyBase implements ComponentConfiguration {

    private MarkedStringProperty condition;
    private ComponentConfiguration trueReplacement;
    private ComponentConfiguration falseReplacement;

    public ComponentConfiguration getTrue() {
        return trueReplacement;
    }

    public void setTrue(ComponentConfiguration trueReplacement) {
        this.trueReplacement = trueReplacement;
    }

    public ComponentConfiguration getFalse() {
        return falseReplacement;
    }

    public void setFalse(ComponentConfiguration falseReplacement) {
        this.falseReplacement = falseReplacement;
    }

    @Override
    public ComponentTemplate toTemplate(TemplateCreationContext tcc) {
        ExpressionTemplate expressionTemplate = ConstantExpressionTemplate.of(true); // dummy value
        if (ConfigValidationUtil.checkNotNull(tcc, "!conditional component", "condition", condition, getStartMark())) {
            try {
                expressionTemplate = tcc.getExpressionEngine().compile(tcc, condition.getValue(), condition.getStartMark());
            } catch (Throwable th) {

                // Usually the expression parser will record errors. If that is not the case raise a generic error here.
                if (!tcc.getErrorHandler().hasErrors()) {
                    tcc.getErrorHandler().addError("Failed to configure conditional component. Failed to parse condition: " + th.getMessage(), condition.getStartMark());
                }
            }
        }

        return ConditionalComponentTemplate.builder()
                .condition(expressionTemplate)
                .trueReplacement(trueReplacement != null ? trueReplacement.toTemplate(tcc) : tcc.emptyComponent())
                .falseReplacement(falseReplacement != null ? falseReplacement.toTemplate(tcc) : tcc.emptyComponent())
                .build();
    }
}
