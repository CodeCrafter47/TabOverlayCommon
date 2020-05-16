package de.codecrafter47.taboverlay.config.expression;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

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

    public static ToDoubleExpression sum(Collection<ToDoubleExpression> operands) {
        return new AbstractToDoubleExpression<ToDoubleExpression>(operands) {

            @Override
            public double evaluate() {
                double result = 0;
                for (val operand : operands) {
                    result += operand.evaluate();
                }
                return result;
            }
        };
    }

    public static ToDoubleExpression product(Collection<ToDoubleExpression> operands) {
        return new AbstractToDoubleExpression<ToDoubleExpression>(operands) {

            @Override
            public double evaluate() {
                double result = 1;
                for (val operand : operands) {
                    result *= operand.evaluate();
                }
                return result;
            }
        };
    }

    public static ToDoubleExpression sub(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToDoubleExpression<ToDoubleExpression>(a, b) {
            @Override
            public double evaluate() {
                return a.evaluate() - b.evaluate();
            }
        };
    }

    public static ToDoubleExpression div(ToDoubleExpression a, ToDoubleExpression b) {
        return new AbstractBinaryToDoubleExpression<ToDoubleExpression>(a, b) {
            @Override
            public double evaluate() {
                return a.evaluate() / b.evaluate();
            }
        };
    }

    public ToDoubleExpression negateNumber(ToDoubleExpression a) {
        return new AbstractUnaryToDoubleExpression<ToDoubleExpression>(a) {
            @Override
            public double evaluate() {
                return -delegate.evaluate();
            }
        };
    }

    public ToStringExpression applyToStringFunction(ToStringExpression expression, Function<String, String> function) {
        return new AbstractUnaryToStringExpression<ToStringExpression>(expression) {
            @Override
            public String evaluate() {
                return function.apply(delegate.evaluate());
            }
        };
    }
}
