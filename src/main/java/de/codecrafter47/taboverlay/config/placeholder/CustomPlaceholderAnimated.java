package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewAnimated;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CustomPlaceholderAnimated implements Placeholder {

    private final List<TextTemplate> parts;
    private final float interval;

    public CustomPlaceholderAnimated(List<TextTemplate> parts, float interval) {
        this.parts = parts;
        this.interval = interval;
    }

    @Override
    @Nonnull
    public TextView instantiate() {
        return new TextViewAnimated(interval, parts);
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return new ToStringExpressionInstance(parts, interval);
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

    private static class ToStringExpressionInstance extends AbstractActiveElement<ExpressionUpdateListener> implements TextViewUpdateListener, ToStringExpression {

        private Future<?> task;
        private final List<TextTemplate> elements; // todo using text views instead of templates here might improve performance
        private TextView activeElement;
        private int nextElementIndex;
        private final long intervalMS;

        private ToStringExpressionInstance(List<TextTemplate> elements, float interval) {
            this.elements = elements;
            this.intervalMS = (long) (interval * 1000);
        }

        @Override
        public String evaluate() {
            return activeElement.getText();
        }

        private void switchActiveElement() {
            activeElement.deactivate();
            if (nextElementIndex >= elements.size()) {
                nextElementIndex = 0;
            }
            activeElement = elements.get(nextElementIndex++).instantiate();
            activeElement.activate(getContext(), this);
            if (hasListener()) {
                getListener().onExpressionUpdate();
            }
        }

        @Override
        protected void onActivation() {
            task = getContext().getTabEventQueue().scheduleAtFixedRate(this::switchActiveElement, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
            activeElement = elements.get(0).instantiate();
            activeElement.activate(getContext(), this);
            nextElementIndex = 1;
        }

        @Override
        protected void onDeactivation() {
            task.cancel(false);
            activeElement.deactivate();
        }

        @Override
        public void onTextUpdated() {
            if (hasListener()) {
                getListener().onExpressionUpdate();
            }
        }
    }
}
