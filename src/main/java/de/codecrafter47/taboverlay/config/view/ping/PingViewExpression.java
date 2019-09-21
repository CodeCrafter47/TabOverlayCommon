package de.codecrafter47.taboverlay.config.view.ping;

import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

public class PingViewExpression extends AbstractActiveElement<PingViewUpdateListener> implements PingView, ExpressionUpdateListener {

    private final ToDoubleExpression expression;

    public PingViewExpression(ToDoubleExpression expression) {
        this.expression = expression;
    }

    @Override
    public int getPing() {
        return (int) expression.evaluate();
    }

    @Override
    protected void onActivation() {
        expression.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        expression.deactivate();
    }

    @Override
    public void onExpressionUpdate() {
        if (hasListener()) {
            getListener().onPingUpdated();
        }
    }
}
