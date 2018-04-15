package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

// todo allow format
public class OtherCountPlaceholderResolver implements PlaceholderResolver {
    @Override
    public Placeholder resolve(String[] value, TemplateCreationContext tcc) throws UnknownPlaceholderException {
        if (value.length >= 1 && "other_count".equalsIgnoreCase(value[0])) {
            return new OtherCountPlaceholder();
        }
        throw new UnknownPlaceholderException();
    }

    public static class OtherCountPlaceholder implements Placeholder {

        @Override
        public ToStringExpression instantiateWithStringResult() {
            return new ToStringInstance();
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return new ToDoubleInstance();
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return Conversions.toBoolean(instantiateWithDoubleResult());
        }

        @Override
        public boolean requiresViewerContext() {
            return true; // todo too lazy to check, playing safe
        }

        @Override
        public TextView instantiate() {
            return new TextViewInstance();
        }

        private static class AbstractInstance<T> extends AbstractActiveElement<T> {

            @Override
            protected void onActivation() {

            }

            @Override
            protected void onDeactivation() {

            }
        }

        private static class ToDoubleInstance extends AbstractInstance<ExpressionUpdateListener> implements ToDoubleExpression {

            @Override
            public double evaluate() {
                return getContext().getCustomObject(ContextKeys.OTHER_COUNT).doubleValue();
            }
        }

        private static class ToStringInstance extends AbstractInstance<ExpressionUpdateListener> implements ToStringExpression {

            @Override
            public String evaluate() {
                return Integer.toString(getContext().getCustomObject(ContextKeys.OTHER_COUNT));
            }
        }

        private static class TextViewInstance extends AbstractInstance<TextViewUpdateListener> implements TextView {

            @Override
            public String getText() {
                return Integer.toString(getContext().getCustomObject(ContextKeys.OTHER_COUNT));
            }
        }
    }
}
