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

package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.template.ConstantExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PlaceholderArg {

    private PlaceholderArg() {

    }

    public abstract String getText();

    public abstract ExpressionTemplate getExpression();

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Text extends PlaceholderArg {

        String value;

        @Override
        public String getText() {
            return value;
        }

        @Override
        public ExpressionTemplate getExpression() {
            return ConstantExpressionTemplate.of(value);
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Placeholder extends PlaceholderArg {

        de.codecrafter47.taboverlay.config.placeholder.Placeholder value;
        String text;

        @Override
        public ExpressionTemplate getExpression() {
            return value;
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Value
    public static class Complex extends PlaceholderArg {

        List<PlaceholderArg> value;

        @Override
        public String getText() {
            StringBuilder result = new StringBuilder();
            for (PlaceholderArg arg : value) {
                result.append(arg.getText());
            }
            return result.toString();
        }

        @Override
        public ExpressionTemplate getExpression() {
            return ExpressionTemplates.concat(value.stream().map(PlaceholderArg::getExpression).collect(Collectors.toList()));
        }
    }
}
