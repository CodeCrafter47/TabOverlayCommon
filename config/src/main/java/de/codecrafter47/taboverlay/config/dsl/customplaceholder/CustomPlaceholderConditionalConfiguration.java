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
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderConditional;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class CustomPlaceholderConditionalConfiguration extends CustomPlaceholderConfiguration {
    @Getter
    @Setter
    private MarkedStringProperty condition;
    private MarkedStringProperty trueReplacement;
    private MarkedStringProperty falseReplacement;

    // alias trueReplacement as true
    public MarkedStringProperty getTrue() {
        return trueReplacement;
    }

    public void setTrue(MarkedStringProperty trueReplacement) {
        this.trueReplacement = trueReplacement;
    }

    // alias falseReplacement as false
    public MarkedStringProperty getFalse() {
        return falseReplacement;
    }

    public void setFalse(MarkedStringProperty falseReplacement) {
        this.falseReplacement = falseReplacement;
    }

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
        ExpressionTemplate compiledCondition = ConstantExpressionTemplate.of(true); // dummy value, to continue processing in case of config errors, to find more errors
        if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !conditional", "condition", condition, getStartMark())) {
            try {
                compiledCondition = tcc.getExpressionEngine().compile(tcc, replaceParameters(condition.getValue(), args), condition.getStartMark());
            } catch (Exception e) {
                tcc.getErrorHandler().addError("Failed to compile condition for custom placeholder. " + e.getMessage(), condition.getStartMark());
            }
        }
        if (trueReplacement == null && falseReplacement == null) {
            tcc.getErrorHandler().addWarning("No replacements configured for conditional custom placeholder.", getStartMark());
        }
        TextTemplate trueReplacement = TextTemplate.EMPTY;
        try {
            trueReplacement = this.trueReplacement != null ? TextTemplate.parse(replaceParameters(this.trueReplacement.getValue(), args), this.trueReplacement.getStartMark(), tcc) : TextTemplate.EMPTY;
        } catch (Exception e) {
            tcc.getErrorHandler().addError("Failed to parse true replacement for custom placeholder: " + e.getMessage(), this.trueReplacement.getStartMark());
        }
        TextTemplate falseReplacement = TextTemplate.EMPTY;
        try {
            falseReplacement = this.falseReplacement != null ? TextTemplate.parse(replaceParameters(this.falseReplacement.getValue(), args), this.falseReplacement.getStartMark(), tcc) : TextTemplate.EMPTY;
        } catch (Exception e) {
            tcc.getErrorHandler().addError("Failed to parse false replacement for custom placeholder: " + e.getMessage(), this.falseReplacement.getStartMark());
        }
        ExpressionTemplate finalCompiledCondition = compiledCondition;
        TextTemplate finalTrueReplacement = trueReplacement;
        TextTemplate finalFalseReplacement = falseReplacement;
        return builder.acquireData(() -> new CustomPlaceholderConditional(finalCompiledCondition,
                finalTrueReplacement,
                finalFalseReplacement), TypeToken.STRING, finalCompiledCondition.requiresViewerContext() || finalTrueReplacement.requiresViewerContext() || finalFalseReplacement.requiresViewerContext());
    }
}
