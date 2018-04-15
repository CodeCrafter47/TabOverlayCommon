package de.codecrafter47.taboverlay.config.expression;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractUnaryToDoubleExpression<T extends Expression> extends ExpressionBase implements ToDoubleExpression {

    protected final T delegate;

    @Override
    protected void onActivation() {
        delegate.activate(getContext(), getListener());
    }

    @Override
    protected void onDeactivation() {
        delegate.deactivate();
    }
}
