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

public class CustomPlaceholderConditional implements Placeholder {

    private final ExpressionTemplate condition;
    private final TextTemplate trueReplacement;
    private final TextTemplate falseReplacement;

    public CustomPlaceholderConditional(ExpressionTemplate condition, TextTemplate trueReplacement, TextTemplate falseReplacement) {
        this.condition = condition;
        this.trueReplacement = trueReplacement;
        this.falseReplacement = falseReplacement;
    }

    @Override
    public TextView instantiate() {
        return new TextViewConditional(condition, trueReplacement, falseReplacement);
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return new ConditionalToStringExpression(condition, trueReplacement, falseReplacement);
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

    private static abstract class AbstractInstance<T> extends AbstractActiveElement<T> implements ExpressionUpdateListener, TextViewUpdateListener {

        final ToBooleanExpression condition;
        final TextTemplate trueReplacement;
        final TextTemplate falseReplacement;
        TextView activeReplacement;

        private AbstractInstance(ExpressionTemplate condition, TextTemplate falseReplacement, TextTemplate trueReplacement) {
            this.falseReplacement = falseReplacement;
            this.trueReplacement = trueReplacement;
            this.condition = condition.instantiateWithBooleanResult();
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
                fireUpdateEvent();
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
            update(true); // todo use scheduled updates
        }

        @Override
        public void onTextUpdated() {
            if (hasListener()) {
                fireUpdateEvent();
            }
        }

        protected abstract void fireUpdateEvent();
    }

    private static class TextViewConditional extends AbstractInstance<TextViewUpdateListener> implements TextView {

        private TextViewConditional(ExpressionTemplate condition, TextTemplate trueReplacement, TextTemplate falseReplacement) {
            super(condition, falseReplacement, trueReplacement);
        }

        @Override
        public String getText() {
            return activeReplacement.getText();
        }

        @Override
        protected void fireUpdateEvent() {
            getListener().onTextUpdated();
        }
    }

    private static class ConditionalToStringExpression extends AbstractInstance<ExpressionUpdateListener> implements ToStringExpression {

        private ConditionalToStringExpression(ExpressionTemplate condition, TextTemplate trueReplacement, TextTemplate falseReplacement) {
            super(condition, falseReplacement, trueReplacement);
        }

        @Override
        public String evaluate() {
            return activeReplacement.getText();
        }

        @Override
        protected void fireUpdateEvent() {
            getListener().onExpressionUpdate();
        }
    }
}
