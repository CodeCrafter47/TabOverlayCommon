package de.codecrafter47.taboverlay.config.expression;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class AbstractToDoubleExpression<T extends Expression> extends ExpressionBase implements ToDoubleExpression {

    protected final Collection<T> operands;

    @Override
    protected void onActivation() {
        for (T operand : operands) {
            operand.activate(getContext(), getListener());
        }
    }

    @Override
    protected void onDeactivation() {
        for (T operand : operands) {
            operand.deactivate();
        }
    }
}
