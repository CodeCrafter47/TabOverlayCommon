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

package de.codecrafter47.taboverlay.config.expression.operators;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class ListOperator extends Operator {
    public ListOperator(int priority) {
        super(priority);
    }

    public static  ListOperator of(int priority, Function<Collection<ExpressionTemplate>, ExpressionTemplate> function) {
        return new ListOperator(priority) {
            @Override
            public ExpressionTemplate createTemplate(List<ExpressionTemplate> operands) {
                return function.apply(operands);
            }
        };
    }

    @Override
    public ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b) {
        return createTemplate(Arrays.<ExpressionTemplate>asList(a, b));
    }

    public abstract ExpressionTemplate createTemplate(List<ExpressionTemplate> operands);
}
