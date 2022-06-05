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

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public final class PlaceholderResolverChain implements PlaceholderResolver<Context>, Cloneable {
    private ArrayList<PlaceholderResolver<Context>> resolvers = new ArrayList<>();

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        for (PlaceholderResolver<Context> resolver : resolvers) {
            try {
                return resolver.resolve(builder, args, tcc);
            } catch (UnknownPlaceholderException ignored) {
            }
        }

        throw new UnknownPlaceholderException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PlaceholderResolverChain clone() {
        PlaceholderResolverChain clone;
        try {
            clone = (PlaceholderResolverChain) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
        clone.resolvers = (ArrayList<PlaceholderResolver<Context>>) resolvers.clone();
        return clone;
    }

    public void addResolver(PlaceholderResolver<Context> resolver) {
        resolvers.add(resolver);
    }
}
