/*
 *     Copyright (C) 2020 Florian Stober
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
