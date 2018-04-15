package de.codecrafter47.taboverlay.config.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class GlobalPlayerSetFactory {

    private final PlayerProvider playerProvider;
    private final ScheduledExecutorService eventQueue;
    private final Logger logger;

    private final LoadingCache<PlayerSetTemplate, SharedPlayerSet> cache = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<PlayerSetTemplate, SharedPlayerSet>() {
        @Override
        public SharedPlayerSet load(@Nonnull PlayerSetTemplate key) {
            return new SharedPlayerSet(GlobalPlayerSetFactory.this.playerProvider,
                    key,
                    GlobalPlayerSetFactory.this.eventQueue,
                    logger);
        }
    });

    public GlobalPlayerSetFactory(PlayerProvider playerProvider, ScheduledExecutorService eventQueue, Logger logger) {
        this.playerProvider = playerProvider;
        this.eventQueue = eventQueue;
        this.logger = logger;
    }

    /**
     * Get a shared (fast) instance of the player set
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
