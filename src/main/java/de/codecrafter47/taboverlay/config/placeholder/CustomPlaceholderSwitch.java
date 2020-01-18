package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.Map;

public class CustomPlaceholderSwitch extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener, TextViewUpdateListener {

    private final ToStringExpression expression;
    private final Map<String, TextTemplate> replacements;
    private final TextTemplate defaultReplacement;
    private TextView activeView;

    public CustomPlaceholderSwitch(ExpressionTemplate expression, Map<String, TextTemplate> replacements, TextTemplate defaultReplacement) {
        this.expression = expression.instantiateWithStringResult();
        this.replacements = replacements;
        this.defaultReplacement = defaultReplacement;
    }


    private void update(boolean fireEvent) {
        if (this.activeView != null) {
            this.activeView.deactivate();
        }
        String result = expression.evaluate();
        TextTemplate template = replacements.getOrDefault(result, defaultReplacement);
        activeView = template.instantiate();
        activeView.activate(getContext(), this);

        if (fireEvent && hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onActivation() {
        this.expression.activate(getContext(), this);
        update(false);
    }

    @Override
    protected void onDeactivation() {
        if (activeView != null) {
            activeView.deactivate();
        }
        expression.deactivate();
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
        return activeView.getText();
    }

}







