package de.codecrafter47.taboverlay.config.expression;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import lombok.EqualsAndHashCode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ToBooleanExpression extends Expression {

    boolean evaluate();

    static ToBooleanExpression literal(boolean value) {
        return new ConstantToBooleanExpression(value);
    }

    @EqualsAndHashCode(callSuper = false)
    class ConstantToBooleanExpression implements ToBooleanExpression {

        private final boolean value;

        private ConstantToBooleanExpression(boolean value) {
            this.value = value;
        }

        @Override
        public boolean evaluate() {
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
