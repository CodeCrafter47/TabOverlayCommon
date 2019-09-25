package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericPlaceholder<C, D> implements Placeholder {

    private Function<Context, C> contextTransformation;
    private TypeToken<D> typeToken;
    private Supplier<PlaceholderDataProvider<C, D>> dataProviderFactory;
    private boolean requiresViewerContext;

    GenericPlaceholder(Function<Context, C> contextTransformation, Supplier<PlaceholderDataProvider<C, D>> dataProviderFactory, TypeToken<D> typeToken, boolean requiresViewerContext) {
        this.contextTransformation = contextTransformation;
        this.dataProviderFactory = dataProviderFactory;
        this.typeToken = typeToken;
        this.requiresViewerContext = requiresViewerContext;
    }

    @Nonnull
    @Override
    public TextView instantiate() {
        return new GenericPlaceholderTextView();
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return new GenericPlaceholderToStringExpression();
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        // todo may use set instead (in data-api?)
        if (typeToken == TypeToken.FLOAT || typeToken == TypeToken.DOUBLE || typeToken == TypeToken.INTEGER) {
            return new GenericPlaceholderToDoubleExpression();
        } else {
            return Conversions.toDouble(instantiateWithStringResult());
        }
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        if (typeToken == TypeToken.BOOLEAN) {
            return new GenericPlaceholderToBooleanExpression();
        } else {
            return Conversions.toBoolean(instantiateWithStringResult());
        }
    }

    @Override
    public boolean requiresViewerContext() {
        return requiresViewerContext;
    }

    private C transformContext(Context context) {
        return contextTransformation.apply(context);
    }

    private class GenericPlaceholderTextView extends AbstractActiveElement<TextViewUpdateListener> implements TextView, Runnable {

        private final PlaceholderDataProvider<C, D> delegate = dataProviderFactory.get();

        @Override
        public String getText() {
            D value = delegate.getData();
            if (value instanceof Float || value instanceof Double) {
                double val = ((Number) value).doubleValue();
                if ((val % 1) == 0) {
                    return Integer.toString((int) val);
                }
            }
            return value == null ? "" : Objects.toString(value);
        }

        @Override
        protected void onActivation() {
            delegate.activate(transformContext(getContext()), this);
        }

        @Override
        protected void onDeactivation() {
            delegate.deactivate();
        }

        @Override
        public void run() {
            if (hasListener()) {
                getListener().onTextUpdated();
            }
        }
    }

    private class GenericPlaceholderToStringExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToStringExpression, Runnable {

        private final PlaceholderDataProvider<C, D> delegate = dataProviderFactory.get();

        @Override
        public String evaluate() {
            D value = delegate.getData();
            if (value instanceof Float || value instanceof Double) {
                double val = ((Number) value).doubleValue();
                if ((val % 1) == 0) {
                    return Integer.toString((int) val);
                }
            }
            return value == null ? "" : Objects.toString(value);
        }

        @Override
        protected void onActivation() {
            delegate.activate(transformContext(getContext()), this);
        }

        @Override
        protected void onDeactivation() {
            delegate.deactivate();
        }

        @Override
        public void run() {
            getListener().onExpressionUpdate();
        }
    }

    private class GenericPlaceholderToDoubleExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToDoubleExpression, Runnable {

        private final PlaceholderDataProvider<C, D> delegate = dataProviderFactory.get();

        @Override
        public double evaluate() {
            Number value = (Number) delegate.getData();
            return value == null ? 0 : value.doubleValue();
        }

        @Override
        protected void onActivation() {
            delegate.activate(transformContext(getContext()), this);
        }

        @Override
        protected void onDeactivation() {
            delegate.deactivate();
        }

        @Override
        public void run() {
            getListener().onExpressionUpdate();
        }
    }

    private class GenericPlaceholderToBooleanExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToBooleanExpression, Runnable {

        private final PlaceholderDataProvider<C, D> delegate = dataProviderFactory.get();

        @Override
        public boolean evaluate() {
            Boolean value = (Boolean) delegate.getData();
            return value == Boolean.TRUE;
        }

        @Override
        protected void onActivation() {
            delegate.activate(transformContext(getContext()), this);
        }

        @Override
        protected void onDeactivation() {
            delegate.deactivate();
        }

        @Override
        public void run() {
            getListener().onExpressionUpdate();
        }
    }
}
