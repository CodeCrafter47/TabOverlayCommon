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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;

@RequiredArgsConstructor
public class DataHolderPlaceholderDataProviderSupplier<C extends DataHolder, R, T> implements Supplier<PlaceholderDataProvider<C, T>> {

    private final static NumberFormat NUMBER_FORMAT;

    static {
        NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.ROOT);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

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
        if (type == TypeToken.FLOAT || type == TypeToken.DOUBLE || type == TypeToken.INTEGER) {
            return context -> {
                Number value = (Number) get(context);
                return value == null ? 0 : value.doubleValue();
            };
        } else if (type == TypeToken.BOOLEAN) {
            return context -> {
                Boolean bool = (Boolean) get(context);
                return bool == Boolean.TRUE ? 1 : 0;
            };
        } else {
            return context -> {
                String result = String.valueOf(get(context));
                try {
                    return NUMBER_FORMAT.parse(result).doubleValue();
                } catch (ParseException | NumberFormatException ignored) {
                    return 0;
                }
            };
        }
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
