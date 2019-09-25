package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

public class CustomPlaceholderCompute extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, Double>, ExpressionUpdateListener {

    private final ToDoubleExpression expression;

    public CustomPlaceholderCompute(ExpressionTemplate expression) {
        this.expression = expression.instantiateWithDoubleResult();
    }

    @Override
    public Double getData() {
        return expression.evaluate();
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
            getListener().run();
        }
    }
}
