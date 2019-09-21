package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import javax.annotation.Nonnull;

public class CustomPlaceholderCompute implements Placeholder {

    private final ExpressionTemplate expression;

    public CustomPlaceholderCompute(ExpressionTemplate expression) {
        this.expression = expression;
    }


    @Override
    @Nonnull
    public TextView instantiate() {
        return new TextViewInstance(expression.instantiateWithStringResult());
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return expression.instantiateWithStringResult();
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        return expression.instantiateWithDoubleResult();
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        return expression.instantiateWithBooleanResult();
    }

    @Override
    public boolean requiresViewerContext() {
        return true; // todo too lazy to check, playing safe
    }

    private static class TextViewInstance extends AbstractActiveElement<TextViewUpdateListener> implements ExpressionUpdateListener, TextView {

        private final ToStringExpression expression;

        private TextViewInstance(ToStringExpression expression) {
            this.expression = expression;
        }

        @Override
        protected void onActivation() {
            this.expression.activate(getContext(), this);
        }

        @Override
        protected void onDeactivation() {
            expression.deactivate();
        }

        @Override
        public void onExpressionUpdate() {
            if (hasListener()) {
                getListener().onTextUpdated();
            }
        }

        @Override
        public String getText() {
            return expression.evaluate();
        }
    }
}
