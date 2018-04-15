package de.codecrafter47.taboverlay.config.expression;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractBinaryToBooleanExpression<T extends Expression> extends ExpressionBase implements ToBooleanExpression {

    protected final T a;
    protected final T b;

    @Override
    protected void onActivation() {
        a.activate(getContext(), getListener());
        b.activate(getContext(), getListener());
    }

    @Override
    protected void onDeactivation() {
        a.deactivate();
        b.deactivate();
    }
}
