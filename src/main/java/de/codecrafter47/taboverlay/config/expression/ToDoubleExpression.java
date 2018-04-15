package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.context.Context;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ToDoubleExpression extends Expression {

    double evaluate();

    static ToDoubleExpression literal(double value) {
        return new ConstantToDoubleExpression(value);
    }

    @EqualsAndHashCode(callSuper = false)
    class ConstantToDoubleExpression implements ToDoubleExpression {
        private final double value;

        private ConstantToDoubleExpression(double value) {
            this.value = value;
        }

        @Override
        public double evaluate() {
            return value;
        }

        @Override
        public void activate(@Nonnull Context context, @Nullable ExpressionUpdateListener listener) {
            // nothing to do
        }

        @Override
        public void deactivate() {
            // nothing to do
        }
    }
}
