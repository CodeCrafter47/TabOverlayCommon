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
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
public abstract class Operator {
    @Getter
    private final int priority;

    public static  Operator of(int priority, BiFunction<ExpressionTemplate, ExpressionTemplate, ExpressionTemplate> function) {
        return new Operator(priority) {
            @Override
            public ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b) {
                return function.apply(a, b);
            }
        };
    }

    public abstract ExpressionTemplate createTemplate(ExpressionTemplate a, ExpressionTemplate b);
}
