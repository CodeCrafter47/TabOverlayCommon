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
import de.codecrafter47.taboverlay.config.placeholder.CustomPlaceholderCompute;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderArg;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;

@NoArgsConstructor
public class CustomPlaceholderComputeConfiguration extends CustomPlaceholderConfiguration {

    @Getter
    @Setter
    private MarkedStringProperty expression;

    private transient boolean needToFixMark;

    public CustomPlaceholderComputeConfiguration(String text) {
        if (text != null) {
            this.expression = new MarkedStringProperty(text);
        }
        this.needToFixMark = true;
    }

    @Override
    public void setStartMark(Mark startMark) {
        super.setStartMark(startMark);
        if (needToFixMark) {
            this.expression.setStartMark(startMark);
        }
    }

    @Override
    public PlaceholderBuilder<?, ?> bindArgs(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) {
        ExpressionTemplate compiledExpression = ConstantExpressionTemplate.of(""); // dummy value, to continue processing in case of config errors, to find more errors
        if (ConfigValidationUtil.checkNotNull(tcc, "custom placeholder !compute", "expression", expression, getStartMark())) {
            try {
                compiledExpression = tcc.getExpressionEngine().compile(tcc, replaceParameters(expression.getValue(), args), expression.getStartMark());
            } catch (Exception e) {
                tcc.getErrorHandler().addError("Failed to compile condition for custom placeholder. " + e.getMessage(), expression.getStartMark());
            }
        }
        ExpressionTemplate finalCompiledExpression = compiledExpression;
        return builder.acquireData(() -> new CustomPlaceholderCompute(finalCompiledExpression), TypeToken.DOUBLE, finalCompiledExpression.requiresViewerContext());
    }
}
