package de.codecrafter47.taboverlay.config.expression;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Collection;
import java.util.Objects;

@UtilityClass
public class Expressions {

    public ToBooleanExpression negate(ToBooleanExpression expression) {
        return new AbstractUnaryToBooleanExpression<ToBooleanExpression>(expression) {
            @Override
            public boolean evaluate() {
                return !delegate.evaluate();
            }
        };
    }

    public static ToBooleanExpression and(Collection<ToBooleanExpression> operands) {
        return new AbstractToBooleanExpression<ToBooleanExpression>(operands) {
            @Override
            public boolean evaluate() {
                for (val operand : operands) {
                    if (!operand.evaluate()) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static ToBooleanExpression or(Collection<ToBooleanExpression> operands) {
        return new AbstractToBooleanExpression<ToBooleanExpression>(operands) {
            @Override
            public boolean evaluate() {
                for (val operand : operands) {
                    if (operand.evaluate()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static ToStringExpression concat(Collection<ToStringExpression> operands) {
        return new AbstractToStringExpression<ToStringExpression>(operands) {

            @Override
            public String evaluate() {
                StringBuilder result = new StringBuilder();
                for (val operand : operands) {
                    result.append(operand.evaluate());
                }
                return result.toString();
            }
        };
    }

    public static ToBooleanExpression equal(ToStringExpression a, ToStringExpression b) {
        return new AbstractBinaryToBooleanExpression<ToStringExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return Objects.equals(a.evaluate(), b.evaluate());
            }
        };
    }

    public static ToBooleanExpression notEqual(ToStringExpression a, ToStringExpression b) {
        return new AbstractBinaryToBooleanExpression<ToStringExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return !Objects.equals(a.evaluate(), b.evaluate());
            }
        };
    }

    public static ToBooleanExpression greaterThan(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToBooleanExpression<ToDoubleExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return a.evaluate() > b.evaluate();
            }
        };
    }

    public static ToBooleanExpression greaterOrEqualThan(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToBooleanExpression<ToDoubleExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return a.evaluate() >= b.evaluate();
            }
        };
    }

    public static ToBooleanExpression lesserThan(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToBooleanExpression<ToDoubleExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return a.evaluate() < b.evaluate();
            }
        };
    }

    public static ToBooleanExpression lesserOrEqualThan(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToBooleanExpression<ToDoubleExpression>(a, b) {
            @Override
            public boolean evaluate() {
                return a.evaluate() <= b.evaluate();
            }
        };
    }
}
