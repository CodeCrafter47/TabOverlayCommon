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

package de.codecrafter47.taboverlay.config.expression.template;

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ConstantExpressionTemplate implements ExpressionTemplate {

    private final ToStringExpression stringExpression;
    private final ToDoubleExpression doubleExpression;
    private final ToBooleanExpression booleanExpression;

    private ConstantExpressionTemplate(String stringValue, double doubleValue, boolean booleanValue) {
        stringExpression = ToStringExpression.literal(stringValue);
        doubleExpression = ToDoubleExpression.literal(doubleValue);
        booleanExpression = ToBooleanExpression.literal(booleanValue);
    }

    public static ConstantExpressionTemplate of(String stringValue) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(stringValue);
        } catch (NumberFormatException ex) {
            doubleValue = stringValue.length();
        }
        return new ConstantExpressionTemplate(stringValue, doubleValue, Boolean.parseBoolean(stringValue));
    }

    public static ConstantExpressionTemplate of(double doubleValue) {
        return new ConstantExpressionTemplate(((int) doubleValue) == doubleValue ? Integer.toString((int) doubleValue) : Double.toString(doubleValue), doubleValue, doubleValue != 0);
    }

    public static ConstantExpressionTemplate of(boolean booleanValue) {
        return new ConstantExpressionTemplate(Boolean.toString(booleanValue), booleanValue ? 1 : 0, booleanValue);
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return stringExpression;
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return doubleExpression;
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return booleanExpression;
    }

    @Override
    public boolean requiresViewerContext() {
        return false;
    }
}
