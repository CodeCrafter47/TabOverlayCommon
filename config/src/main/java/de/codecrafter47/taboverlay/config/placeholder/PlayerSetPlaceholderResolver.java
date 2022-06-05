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
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.config.player.PlayerSet;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import javax.annotation.Nonnull;
import java.util.List;

public class PlayerSetPlaceholderResolver implements PlaceholderResolver<Context> {

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && args.get(0).getText().startsWith("playerset:")) {
            String playerSetId = args.get(0).getText().substring(10);
            PlayerSetTemplate playerSetTemplate = tcc.getPlayerSets().get(playerSetId);
            if (playerSetTemplate == null) {
                throw new PlaceholderException("Unknown player set " + playerSetId);
            }
            args.remove(0);
            if (!args.isEmpty() && "size".equals(args.get(0).getText())) {
                args.remove(0);
            }
            return builder.acquireData(() -> new PlayerCountDataProvider(playerSetTemplate), TypeToken.INTEGER, playerSetTemplate.isRequiresViewerContext());
        }
        throw new UnknownPlaceholderException();
    }

    private static class PlayerCountDataProvider extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, Integer>, PlayerSet.Listener {

        private final PlayerSetTemplate playerSetTemplate;
        private PlayerSet playerSet;

        private PlayerCountDataProvider(PlayerSetTemplate playerSetTemplate) {
            this.playerSetTemplate = playerSetTemplate;
        }

        @Override
        protected void onActivation() {
            playerSet = getContext().getPlayerSetFactory().getInstance(playerSetTemplate);
            playerSet.addListener(this);
        }

        @Override
        protected void onDeactivation() {
            playerSet.removeListener(this);
        }

        public Integer getData() {
            return playerSet.getCount();
        }

        @Override
        public void onPlayerAdded(Player player) {
            if (hasListener()) {
                getListener().run();
            }
        }

        @Override
        public void onPlayerRemoved(Player player) {
            if (hasListener()) {
                getListener().run();
            }
        }
    }
}
