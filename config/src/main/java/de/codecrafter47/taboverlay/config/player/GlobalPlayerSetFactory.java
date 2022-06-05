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

package de.codecrafter47.taboverlay.config.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.placeholder.PlaceholderBuilder;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderDataProviderSupplier;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class GlobalPlayerSetFactory {

    private final PlayerProvider playerProvider;
    private final ScheduledExecutorService eventQueue;
    private final Logger logger;
    final ExpressionTemplate expressionTemplateIsVisible;
    final ExpressionTemplate expressionTemplateCanSeeInvisible;

    private final LoadingCache<PlayerSetTemplate, SharedPlayerSet> cache = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<PlayerSetTemplate, SharedPlayerSet>() {
        @Override
        public SharedPlayerSet load(@Nonnull PlayerSetTemplate key) {
            switch (key.getHiddenPlayersVisibility()) {
                case VISIBLE:
                    return new SharedPlayerSet(GlobalPlayerSetFactory.this.playerProvider,
                            key.getPredicate(),
                            GlobalPlayerSetFactory.this.eventQueue,
                            logger);
                case VISIBLE_TO_ADMINS:
                    throw new IllegalArgumentException("PlayerSet with VISIBLE_TO_ADMINS can't be shared");
                case INVISIBLE:
                    return new SharedPlayerSet(GlobalPlayerSetFactory.this.playerProvider,
                            ExpressionTemplates.and(Arrays.asList(key.getPredicate(), expressionTemplateIsVisible)),
                            GlobalPlayerSetFactory.this.eventQueue,
                            logger);
                default:
                    throw new AssertionError("Unknown player visibility " + key.getHiddenPlayersVisibility());
            }
        }
    });

    public GlobalPlayerSetFactory(PlayerProvider playerProvider, ScheduledExecutorService eventQueue, Logger logger, DataKey<Boolean> DATA_KEY_IS_HIDDEN, DataKey<Boolean> DATA_KEY_CAN_SEE_INVISIBLE) {
        this.playerProvider = playerProvider;
        this.eventQueue = eventQueue;
        this.logger = logger;
        expressionTemplateIsVisible = ExpressionTemplates.negate(PlaceholderBuilder.create()
                .transformContext(Context::getPlayer)
                .acquireData(new PlayerPlaceholderDataProviderSupplier<>(TypeToken.BOOLEAN,
                                DATA_KEY_IS_HIDDEN,
                                (p, i) -> i == null ? false : i),
                        TypeToken.BOOLEAN)
                .requireViewerContext(false)
                .build());
        expressionTemplateCanSeeInvisible = PlaceholderBuilder.create()
                .transformContext(Context::getViewer)
                .acquireData(new PlayerPlaceholderDataProviderSupplier<>(TypeToken.BOOLEAN,
                                DATA_KEY_CAN_SEE_INVISIBLE,
                                (p, i) -> i == null ? false : i),
                        TypeToken.BOOLEAN)
                .requireViewerContext(true)
                .build();
    }

    /**
     * Get a shared (fast) instance of the player set
     *
     * @param template the configuration
     * @return the player set or null if not possible
     */
    @Nullable
    @SneakyThrows
    public PlayerSet getSharedInstance(PlayerSetTemplate template) {
        if (!template.isRequiresViewerContext()) {
            return cache.get(template);
        }
        return null;
    }
}
