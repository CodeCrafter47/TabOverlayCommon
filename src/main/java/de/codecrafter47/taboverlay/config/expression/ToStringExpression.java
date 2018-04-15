package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.context.Context;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ToStringExpression extends Expression {

    String evaluate();

    static ToStringExpression literal(String value) {
        return new ConstantToStringExpression(value);
    }

    @EqualsAndHashCode(callSuper = false)
    class ConstantToStringExpression implements ToStringExpression {

        private final String value;

        private ConstantToStringExpression(String value) {
            this.value = value;
        }

        @Override
        public String evaluate() {
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
