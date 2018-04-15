package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.Conversions;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.Map;

public class CustomPlaceholderSwitch implements Placeholder {

    private final ExpressionTemplate expression;
    private final Map<String, TextTemplate> replacements;
    private final TextTemplate defaultReplacement;

    public CustomPlaceholderSwitch(ExpressionTemplate expression, Map<String, TextTemplate> replacements, TextTemplate defaultReplacement) {
        this.expression = expression;
        this.replacements = replacements;
        this.defaultReplacement = defaultReplacement;
    }

    @Override
    public TextView instantiate() {
        return new TextViewSwitch(expression, replacements, defaultReplacement);
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return new SwitchToStringExpression(expression, replacements, defaultReplacement);
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return Conversions.toDouble(instantiateWithStringResult());
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return Conversions.toBoolean(instantiateWithStringResult());
    }

    @Override
    public boolean requiresViewerContext() {
        return true; // todo too lazy to check, playing safe
    }

    private abstract static class AbstractInstance<T> extends AbstractActiveElement<T> implements ExpressionUpdateListener, TextViewUpdateListener {

        final ToStringExpression expression;
        private final Map<String, TextTemplate> replacements;
        private final TextTemplate defaultReplacement;
        TextView activeView;

        private AbstractInstance(ExpressionTemplate expression, TextTemplate defaultReplacement, Map<String, TextTemplate> replacements) {
            this.expression = expression.instantiateWithStringResult();
            this.defaultReplacement = defaultReplacement;
            this.replacements = replacements;
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
                fireUpdateEvent();
            }
        }

        protected abstract void fireUpdateEvent();

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
            update(true); // todo use scheduled update for better performance
        }

        @Override
        public void onTextUpdated() {
            if (hasListener()) {
                fireUpdateEvent();
            }
        }
    }

    private static class TextViewSwitch extends AbstractInstance<TextViewUpdateListener> implements TextView {

        private TextViewSwitch(ExpressionTemplate expression, Map<String, TextTemplate> replacements, TextTemplate defaultReplacement) {
            super(expression, defaultReplacement, replacements);
        }

        @Override
        public String getText() {
            return activeView.getText();
        }

        @Override
        protected void fireUpdateEvent() {
            getListener().onTextUpdated();
        }
    }

    private static class SwitchToStringExpression extends AbstractInstance<ExpressionUpdateListener> implements ToStringExpression {

        private SwitchToStringExpression(ExpressionTemplate expression, Map<String, TextTemplate> replacements, TextTemplate defaultReplacement) {
            super(expression, defaultReplacement, replacements);
        }

        @Override
        public String evaluate() {
            return activeView.getText();
        }

        @Override
        protected void fireUpdateEvent() {
            getListener().onExpressionUpdate();
        }
    }

}







