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

package de.codecrafter47.taboverlay.config.expression;

import lombok.experimental.UtilityClass;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

@UtilityClass
public class Conversions {

    private final static NumberFormat NUMBER_FORMAT;

    static {
        NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ROOT);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    public ToBooleanExpression toBoolean(ToDoubleExpression expression) {
        return new AbstractUnaryToBooleanExpression<ToDoubleExpression>(expression) {
            @Override
            public boolean evaluate() {
                return delegate.evaluate() != 0;
            }
        };
    }

    public ToBooleanExpression toBoolean(ToStringExpression expression) {
        return new AbstractUnaryToBooleanExpression<ToStringExpression>(expression) {
            @Override
            public boolean evaluate() {
                return Boolean.parseBoolean(delegate.evaluate());
            }
        };
    }

    public ToDoubleExpression toDouble(ToBooleanExpression expression) {
        return new AbstractUnaryToDoubleExpression<ToBooleanExpression>(expression) {
            @Override
            public double evaluate() {
                return delegate.evaluate() ? 1 : 0;
            }
        };
    }

    public ToDoubleExpression toDouble(ToStringExpression expression) {
        return new AbstractUnaryToDoubleExpression<ToStringExpression>(expression) {

            @Override
            public double evaluate() {
                String result = delegate.evaluate();
                try {
                    return NUMBER_FORMAT.parse(result).doubleValue();
                } catch (ParseException | NumberFormatException ignored) {
                    return 0;
                }
            }
        };
    }

    public ToStringExpression toString(ToBooleanExpression expression) {
        return new AbstractUnaryToStringExpression<ToBooleanExpression>(expression) {
            @Override
            public String evaluate() {
                return Boolean.toString(delegate.evaluate());
            }
        };
    }

    public ToStringExpression toString(ToDoubleExpression expression) {
        return new AbstractUnaryToStringExpression<ToDoubleExpression>(expression) {
            @Override
            public String evaluate() {
                double result = delegate.evaluate();
                return result == (int) result ? Integer.toString((int) result) : Double.toString(result);
            }
        };
    }
}
