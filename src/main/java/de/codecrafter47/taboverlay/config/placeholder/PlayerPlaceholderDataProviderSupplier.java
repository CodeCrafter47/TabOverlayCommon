package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.player.Player;

import java.util.function.BiFunction;

/**
 * Stateless player based placeholder
 *
 * @param <T>
 */
public class PlayerPlaceholderDataProviderSupplier<R, T> extends DataHolderPlaceholderDataProviderSupplier<Player, R, T> {

    public PlayerPlaceholderDataProviderSupplier(TypeToken<T> type, DataKey<R> dataKey, BiFunction<Player, R, T> transformation) {
        super(type, dataKey, transformation);
    }
}
