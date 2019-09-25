package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.player.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

/**
 * Stateless player based placeholder
 *
 * @param <T>
 */
@RequiredArgsConstructor
public class PlayerPlaceholderDataProviderSupplier<R, T> implements Supplier<PlaceholderDataProvider<Player, T>> {

    @Nonnull
    @NonNull
    @Getter
    private final TypeToken<T> type;
    @Nullable
    @Getter
    private final DataKey<R> dataKey;
    @Nonnull
    private final BiFunction<Player, R, T> transformation;

    private T get(Player player) {
        R value = null;
        if (dataKey != null) {
            value = player.get(dataKey);
        }
        return transformation.apply(player, value);
    }

    public Function<Player, String> getToStringFunction() {
        return player -> String.valueOf(get(player));
    }

    public ToDoubleFunction<Player> getToDoubleFunction() {
        return player -> {
            Number value = (Number) get(player);
            return value == null ? 0 : value.doubleValue();
        };
    }

    @Override
    public PlaceholderDataProvider<Player, T> get() {
        return new PlayerPlaceholderDataProvider();
    }

    private class PlayerPlaceholderDataProvider implements PlaceholderDataProvider<Player, T> {
        private Player player;
        private Runnable listener;

        @Override
        public void activate(Player player, Runnable listener) {
            this.player = player;
            this.listener = listener;
            if (dataKey != null) {
                player.addDataChangeListener(dataKey, listener);
            }
        }

        @Override
        public void deactivate() {
            if (dataKey != null) {
                player.removeDataChangeListener(dataKey, listener);
            }
        }

        @Override
        public T getData() {
            return get(player);
        }
    }
}
