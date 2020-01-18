package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

public class CustomPlaceholderConditional extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener, TextViewUpdateListener {

    private final ToBooleanExpression condition;
    private final TextTemplate trueReplacement;
    private final TextTemplate falseReplacement;
    private TextView activeReplacement;

    public CustomPlaceholderConditional(ExpressionTemplate condition, TextTemplate trueReplacement, TextTemplate falseReplacement) {
        this.condition = condition.instantiateWithBooleanResult();
        this.trueReplacement = trueReplacement;
        this.falseReplacement = falseReplacement;
    }

    @Override
    protected void onActivation() {
        this.condition.activate(getContext(), this);
        update(false);
    }

    private void update(boolean fireEvent) {
        if (activeReplacement != null) {
            activeReplacement.deactivate();
        }
        boolean result = condition.evaluate();
        if (result) {
            activeReplacement = trueReplacement.instantiate();
        } else {
            activeReplacement = falseReplacement.instantiate();
        }
        activeReplacement.activate(getContext(), this);

        if (fireEvent && hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onDeactivation() {
        if (activeReplacement != null) {
            activeReplacement.deactivate();
        }
        condition.deactivate();
    }

    @Override
    public void onExpressionUpdate() {
        update(true);
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }

    @Override
    public String getData() {
        return activeReplacement.getText();
    }
}
