package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import lombok.EqualsAndHashCode;

import java.text.SimpleDateFormat;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimePlaceholderResolver implements PlaceholderResolver {
    @Override
    public Placeholder resolve(String[] value, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (value.length >= 1 && "time".equals(value[0])) {
            StringBuilder formatString = new StringBuilder(value.length >= 2 ? value[1] : "");
            for (int i = 2; i < value.length; i++) {
                String s = value[i];
                formatString.append(' ');
                formatString.append(s);
            }
            SimpleDateFormat format;
            try {
                format = new SimpleDateFormat(formatString.toString()); // todo apply locale
            } catch (IllegalArgumentException ex) {
                throw new PlaceholderException("Invalid time format", ex);
            }

            return new TimePlaceholder(format);
        }
        throw new UnknownPlaceholderException();
    }

    @EqualsAndHashCode
    private static class TimePlaceholder implements Placeholder {

        private final SimpleDateFormat format;

        public TimePlaceholder(SimpleDateFormat format) {
            this.format = format;
        }

        @Override
        public ToStringExpression instantiateWithStringResult() {
            return null;
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
            return false;
        }

        @Override
        public TextView instantiate() {
            return new TextViewInstance(format);
        }

        private static abstract class AbstractInstance<T> extends AbstractActiveElement<T> implements Runnable {

            protected final SimpleDateFormat format;
            private ScheduledFuture<?> future;

            protected AbstractInstance(SimpleDateFormat format) {
                this.format = format;
            }

            @Override
            protected void onActivation() {
                // todo can do better if seconds not used?
                future = getContext().getTabEventQueue().scheduleWithFixedDelay(this, 1, 1, TimeUnit.SECONDS);
            }

            @Override
            protected void onDeactivation() {
                future.cancel(false);
            }

            @Override
            public void run() {
                notifyListener();
            }

            protected abstract void notifyListener();
        }

        private static class ToStringInstance extends AbstractInstance<ExpressionUpdateListener> implements ToStringExpression {

            protected ToStringInstance(SimpleDateFormat format) {
                super(format);
            }

            @Override
            public String evaluate() {
                return format.format(System.currentTimeMillis());
            }

            @Override
            protected void notifyListener() {
                if (hasListener()) {
                    getListener().onExpressionUpdate();
                }
            }
        }

        private static class TextViewInstance extends AbstractInstance<TextViewUpdateListener> implements TextView {

            protected TextViewInstance(SimpleDateFormat format) {
                super(format);
            }

            @Override
            protected void notifyListener() {
                if (hasListener()) {
                    getListener().onTextUpdated();
                }
            }

            @Override
            public String getText() {
                return format.format(System.currentTimeMillis());
            }
        }
    }
}
