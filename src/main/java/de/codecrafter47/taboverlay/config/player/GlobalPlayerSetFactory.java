package de.codecrafter47.taboverlay.config.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
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
        expressionTemplateIsVisible = ExpressionTemplates.negate(new PlayerPlaceholder<>(PlayerPlaceholder.BindPoint.PLAYER,
                TypeToken.BOOLEAN,
                DATA_KEY_IS_HIDDEN,
                i -> i,
                p -> false,
                b -> Boolean.toString(b)));
        expressionTemplateCanSeeInvisible = new PlayerPlaceholder<>(PlayerPlaceholder.BindPoint.VIEWER,
                TypeToken.BOOLEAN,
                DATA_KEY_CAN_SEE_INVISIBLE,
                i -> i,
                p -> false,
                b -> Boolean.toString(b));
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
