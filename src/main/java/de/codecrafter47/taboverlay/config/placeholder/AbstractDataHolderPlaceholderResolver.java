package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.data.api.DataHolder;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AbstractDataHolderPlaceholderResolver<C extends DataHolder> extends AbstractPlaceholderResolver<C> {

    protected static <C extends DataHolder, T> PlaceholderResolver<C> create(DataKey<T> dataKey) {
        return (builder, args, tcc) -> builder.acquireData(new DataHolderPlaceholderDataProviderSupplier<>(dataKey.getType(), dataKey, (p, d) -> d), dataKey.getType());
    }

    protected static <C extends DataHolder, R, T> PlaceholderResolver<C> create(DataKey<R> dataKey, BiFunction<C, R, T> transformation, TypeToken<T> type) {
        return (builder, args, tcc) -> builder.acquireData(new DataHolderPlaceholderDataProviderSupplier<>(type, dataKey, transformation), type);
    }

    protected static <C extends DataHolder, R, T> PlaceholderResolver<C> create(DataKey<R> dataKey, Function<R, T> transformation, TypeToken<T> type) {
        return (builder, args, tcc) -> builder.acquireData(new DataHolderPlaceholderDataProviderSupplier<>(type, dataKey, (p, d) -> transformation.apply(d)), type);
    }
}
