package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataHolder;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

@RequiredArgsConstructor
public class DataHolderPlaceholderDataProviderSupplier<C extends DataHolder, R, T> implements Supplier<PlaceholderDataProvider<C, T>> {
    @Nonnull
    @NonNull
    @Getter
    private final TypeToken<T> type;
    @Nullable
    @Getter
    private final DataKey<R> dataKey;
    @Nonnull
    private final BiFunction<C, R, T> transformation;

    private T get(C context) {
        R value = null;
        if (dataKey != null) {
            value = context.get(dataKey);
        }
        return transformation.apply(context, value);
    }

    public Function<C, String> getToStringFunction() {
        return context -> String.valueOf(get(context));
    }

    public ToDoubleFunction<C> getToDoubleFunction() {
        return context -> {
            Number value = (Number) get(context);
            return value == null ? 0 : value.doubleValue();
        };
    }

    @Override
    public PlaceholderDataProvider<C, T> get() {
        return new PlayerPlaceholderDataProvider();
    }

    private class PlayerPlaceholderDataProvider implements PlaceholderDataProvider<C, T> {
        private C context;
        private Runnable listener;

        @Override
        public void activate(C player, Runnable listener) {
            this.context = player;
            this.listener = listener;
            if (dataKey != null) {
                player.addDataChangeListener(dataKey, listener);
            }
        }

        @Override
        public void deactivate() {
            if (dataKey != null) {
                context.removeDataChangeListener(dataKey, listener);
            }
        }

        @Override
        public T getData() {
            return get(context);
        }
    }
}
