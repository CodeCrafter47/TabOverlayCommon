package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.*;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import de.codecrafter47.taboverlay.util.Unchecked;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Stateless player based placeholder
 *
 * @param <T>
 */
@Value
public class PlayerPlaceholder<R, T> implements Placeholder {

    @Nonnull
    @NonNull
    private final BindPoint bindPoint;
    @Nonnull
    @NonNull
    private final TypeToken<T> type;
    @Nullable
    private final DataKey<R> dataKey;
    @Nullable
    private final Function<R, T> transformation;
    @Nullable
    private final Function<Player, T> defaultValueFunction;
    @Nonnull
    @NonNull
    private final Function<T, String> representationFunction;

    @Override
    public TextView instantiate() {
        return new PlayerPlaceholderTextView();
    }

    @Override
    public ToStringExpression instantiateWithStringResult() {
        return new PlayerPlaceholderToStringExpression();
    }

    @Override
    public ToDoubleExpression instantiateWithDoubleResult() {
        // todo may use set instead (in data-api?)
        if (type == TypeToken.FLOAT || type == TypeToken.DOUBLE || type == TypeToken.INTEGER) {
            return new PlayerPlaceholderToDoubleExpression();
        } else {
            return Conversions.toDouble(instantiateWithStringResult());
        }
    }

    @Override
    public ToBooleanExpression instantiateWithBooleanResult() {
        if (type == TypeToken.BOOLEAN) {
            return new PlayerPlaceholderToBooleanExpression();
        } else {
            return Conversions.toBoolean(instantiateWithStringResult());
        }
    }

    @Override
    public boolean requiresViewerContext() {
        return bindPoint == BindPoint.VIEWER;
    }

    private T get(Context context) {
        Player player = getPlayer(context);
        T value = null;
        if (dataKey != null) {
            R original = player.get(dataKey);
            if (transformation != null && original != null) {
                value = transformation.apply(original);
            } else {
                value = Unchecked.cast(original);
            }
        }
        if (value == null && defaultValueFunction != null) {
            value = defaultValueFunction.apply(player);
        }
        return value;
    }

    private Player getPlayer(Context context) {
        switch (bindPoint) {
            case VIEWER:
                return context.getViewer();
            case PLAYER:
                return context.getPlayer();
            default:
                throw new AssertionError();
        }
    }

    private final class PlayerPlaceholderActiveElement extends AbstractActiveElement<Runnable> {

        @Override
        protected void onActivation() {
            if (dataKey != null) {
                getPlayer(getContext()).addDataChangeListener(dataKey, getListener());
            }
        }

        @Override
        protected void onDeactivation() {
            // todo ensure this doesn't throw
            if (dataKey != null) {
                getPlayer(getContext()).removeDataChangeListener(dataKey, getListener());
            }
        }
    }

    private class PlayerPlaceholderTextView extends AbstractActiveElement<TextViewUpdateListener> implements TextView, Runnable {

        private final PlayerPlaceholderActiveElement delegate = new PlayerPlaceholderActiveElement();

        @Override
        public String getText() {
            T value = get(getContext());
            return representationFunction.apply(value);
        }

        @Override
        protected void onActivation() {
            delegate.activate(getContext(), this);
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

    private class PlayerPlaceholderToStringExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToStringExpression, Runnable {

        private final PlayerPlaceholderActiveElement delegate = new PlayerPlaceholderActiveElement();

        @Override
        public String evaluate() {
            T value = get(getContext());
            return representationFunction.apply(value);
        }

        @Override
        protected void onActivation() {
            delegate.activate(getContext(), this);
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

    private class PlayerPlaceholderToDoubleExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToDoubleExpression, Runnable {

        private final PlayerPlaceholderActiveElement delegate = new PlayerPlaceholderActiveElement();

        @Override
        public double evaluate() {
            Number value = (Number) get(getContext());
            return value == null ? 0 : value.doubleValue();
        }

        @Override
        protected void onActivation() {
            delegate.activate(getContext(), this);
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

    private class PlayerPlaceholderToBooleanExpression extends AbstractActiveElement<ExpressionUpdateListener> implements ToBooleanExpression, Runnable {

        private final PlayerPlaceholderActiveElement delegate = new PlayerPlaceholderActiveElement();

        @Override
        public boolean evaluate() {
            Boolean value = (Boolean) get(getContext());
            return value == Boolean.TRUE;
        }

        @Override
        protected void onActivation() {
            delegate.activate(getContext(), this);
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

    public enum BindPoint {
        VIEWER, PLAYER
    }
}
