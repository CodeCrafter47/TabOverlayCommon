package de.codecrafter47.taboverlay.config.player;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class SharedPlayerSet extends AbstractPlayerSet {

    private final LoadingCache<ExpressionTemplate, PlayerSetPartition> cache = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<ExpressionTemplate, PlayerSetPartition>() {
        @Override
        public PlayerSetPartition load(@Nonnull ExpressionTemplate key) {
            return new PlayerSetPartition(context.getTabEventQueue(),
                    SharedPlayerSet.this,
                    logger, key,
                    context);
        }
    });

    public SharedPlayerSet(PlayerProvider playerProvider, ExpressionTemplate template, ScheduledExecutorService eventQueue, Logger logger) {
        super(eventQueue, playerProvider, logger, template, Context.from(null, eventQueue));
    }

    @Override
    @SneakyThrows
    public PlayerSetPartition getPartition(ExpressionTemplate partitionFunction) {
        return cache.get(partitionFunction);
    }
}
