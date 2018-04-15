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
