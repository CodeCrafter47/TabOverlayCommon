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
