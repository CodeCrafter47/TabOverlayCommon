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
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class PlayerPlaceholderResolver implements PlaceholderResolver<Context> {

    private final PlaceholderResolver<Player> delegate;
    private final BindPoint bindPoint;
    private final String prefix;

    public PlayerPlaceholderResolver(PlaceholderResolver<Player> delegate, BindPoint bindPoint) {
        this.delegate = delegate;
        this.bindPoint = bindPoint;
        this.prefix = bindPoint == BindPoint.PLAYER ? "player" : "viewer";
    }

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {

        if (args.size() < 1) {
            throw new UnknownPlaceholderException();
        }
        if (!prefix.equals(args.get(0).getText())) {
            throw new UnknownPlaceholderException();
        }

        if (bindPoint == BindPoint.VIEWER && !tcc.isViewerAvailable()) {
            throw new PlaceholderException("viewer bound placeholders not available in the current context");
        } else if (bindPoint == BindPoint.PLAYER && !tcc.isPlayerAvailable()) {
            StringBuilder suggest = new StringBuilder("viewer");
            for (int i = 1; i < args.size(); i++) {
                String token = args.get(i).getText();
                suggest.append(' ');
                suggest.append(token);
            }
            throw new PlaceholderException("player bound placeholders not available in the current context\n"
                    + "Note: You might want to use ${" + suggest + "} instead");
        }

        args.remove(0);
        try {
            return delegate.resolve(builder.transformContext(bindPoint.contextTransformation), args, tcc).requireViewerContext(bindPoint == BindPoint.VIEWER);
        } catch (UnknownPlaceholderException ignored) {
            throw new PlaceholderException("Unknown placeholder");
        }
    }

    @AllArgsConstructor
    public enum BindPoint {
        VIEWER(Context::getViewer), PLAYER(Context::getPlayer);

        private Function<Context, Player> contextTransformation;
    }
}
