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

import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.Setter;
import lombok.val;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractPlaceholderResolver<C> implements PlaceholderResolver<C> {
    private Map<String, PlaceholderResolver<C>> placeholders = new HashMap<>();
    @Setter
    private Function<PlaceholderBuilder<C, ?>, PlaceholderBuilder<C, ?>> defaultPlaceholder;

    public AbstractPlaceholderResolver() {
    }

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<C, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (args.size() == 0) {
            if (defaultPlaceholder != null) {
                return defaultPlaceholder.apply(builder);
            }
            throw new UnknownPlaceholderException();
        } else {
            String token = args.get(0).getText();

            val placeholderResolver = placeholders.get(token);
            if (placeholderResolver == null) {
                throw new UnknownPlaceholderException();
            }

            if (!args.isEmpty()) {
                args.remove(0);
            }

            return placeholderResolver.resolve(builder, args, tcc);
        }
    }

    protected final <R, T> void addPlaceholder(String name, PlaceholderResolver<C> resolver) {
        placeholders.put(name, resolver);
    }

}
