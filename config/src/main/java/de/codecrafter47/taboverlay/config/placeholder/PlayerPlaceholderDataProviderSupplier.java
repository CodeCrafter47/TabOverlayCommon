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
