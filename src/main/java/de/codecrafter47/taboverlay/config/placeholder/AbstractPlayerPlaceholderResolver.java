package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.player.Player;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractPlayerPlaceholderResolver extends AbstractPlaceholderResolver<Player> {

    public AbstractPlayerPlaceholderResolver() {
        addPlaceholder("name", create(null, (player, ignored) -> player.getName(), TypeToken.STRING));
        addPlaceholder("uuid", create(null, (player, ignored) -> player.getUniqueID().toString(), TypeToken.STRING));
    }

    protected static <T> PlaceholderResolver<Player> create(DataKey<T> dataKey) {
        return (builder, args, tcc) -> builder.acquireData(new PlayerPlaceholderDataProviderSupplier<T, T>(dataKey.getType(), dataKey, (p, d) -> d), dataKey.getType());
    }

    protected static <R, T> PlaceholderResolver<Player> create(DataKey<R> dataKey, BiFunction<Player, R, T> transformation, TypeToken<T> type) {
        return (builder, args, tcc) -> builder.acquireData(new PlayerPlaceholderDataProviderSupplier<R, T>(type, dataKey, transformation), type);
    }

    protected static <R, T> PlaceholderResolver<Player> create(DataKey<R> dataKey, Function<R, T> transformation, TypeToken<T> type) {
        return (builder, args, tcc) -> builder.acquireData(new PlayerPlaceholderDataProviderSupplier<R, T>(type, dataKey, (p, d) -> transformation.apply(d)), type);
    }
}
