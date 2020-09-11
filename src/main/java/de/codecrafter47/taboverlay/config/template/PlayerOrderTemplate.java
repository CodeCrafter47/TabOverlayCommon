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

package de.codecrafter47.taboverlay.config.template;

import de.codecrafter47.data.api.DataHolder;
import de.codecrafter47.taboverlay.config.placeholder.DataHolderPlaceholderDataProviderSupplier;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Value
public class PlayerOrderTemplate {
    @Nonnull
    @NonNull
    List<Entry> entries;

    public boolean requiresViewerContext() {
        for (Entry entry : entries) {
            if (entry.requiresViewerContext())
                return true;
        }
        return false;
    }

    @Value
    public static class Entry {
        @Nonnull
        @NonNull
        DataHolderPlaceholderDataProviderSupplier<DataHolder, ?, ?> placeholder;

        @Nonnull
        @NonNull
        Direction direction;

        @Nonnull
        @NonNull
        Type type;

        @Nullable
        List<String> customOrder;

        boolean requiresViewerContext() {
            return direction == Direction.VIEWER_FIRST;
        }
    }

    public enum Direction {
        ASCENDING, DESCENDING, VIEWER_FIRST, CUSTOM;
    }

    public enum Type {
        TEXT, NUMBER
    }
}
