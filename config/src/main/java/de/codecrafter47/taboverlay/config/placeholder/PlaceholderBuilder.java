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

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

public class PlaceholderBuilder<C, D> {

    @Getter
    Function<Context, C> contextTransformation;
    @Getter
    private Supplier<PlaceholderDataProvider<C, D>> dataProviderFactory;
    private TypeToken<D> typeToken;
    @Getter
    private boolean requiresViewerContext;

    private PlaceholderBuilder(Function<Context, C> contextTransformation) {
        this.contextTransformation = contextTransformation;
    }

    private PlaceholderBuilder(Function<Context, C> contextTransformation, Supplier<PlaceholderDataProvider<C, D>> dataProviderFactory, TypeToken<D> typeToken, boolean requiresViewerContext) {
        this.contextTransformation = contextTransformation;
        this.dataProviderFactory = dataProviderFactory;
        this.typeToken = typeToken;
        this.requiresViewerContext = requiresViewerContext;
    }

    public static PlaceholderBuilder<Context, Void> create() {
        return new PlaceholderBuilder<>(Function.identity());
    }

    public <C2> PlaceholderBuilder<C2, Void> transformContext(Function<C, C2> transformation) {
        return new PlaceholderBuilder<>(contextTransformation.andThen(transformation));
    }

    public <D2> PlaceholderBuilder<C, D2> acquireData(Supplier<PlaceholderDataProvider<C, D2>> dataProviderFactory, TypeToken<D2> typeToken) {
        return new PlaceholderBuilder<>(contextTransformation, dataProviderFactory, typeToken, requiresViewerContext);
    }

    public <D2> PlaceholderBuilder<C, D2> acquireData(Supplier<PlaceholderDataProvider<C, D2>> dataProviderFactory, TypeToken<D2> typeToken, boolean requiresViewerContext) {
        return new PlaceholderBuilder<>(contextTransformation, dataProviderFactory, typeToken, requiresViewerContext);
    }

    public PlaceholderBuilder<C, D> requireViewerContext(boolean requiresViewerContext) {
        return new PlaceholderBuilder<>(contextTransformation, dataProviderFactory, typeToken, requiresViewerContext);
    }

    public <D2> PlaceholderBuilder<C, D2> transformData(Function<D, D2> dataTransformation, TypeToken<D2> typeToken) {
        return new PlaceholderBuilder<>(contextTransformation, () -> dataProviderFactory.get().transformData(dataTransformation), typeToken, requiresViewerContext);
    }

    public TypeToken<D> getType() {
        return typeToken;
    }

    public Placeholder build() {
        return new GenericPlaceholder<C, D>(contextTransformation, dataProviderFactory, typeToken, requiresViewerContext);
    }
}
