package de.codecrafter47.taboverlay.config.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class SharedPlayerSet extends AbstractPlayerSet {

    private final LoadingCache<ExpressionTemplate, PlayerSetPartition> cachePartition = CacheBuilder.newBuilder().weakValues().build(new CacheLoader<ExpressionTemplate, PlayerSetPartition>() {
        @Override
        public PlayerSetPartition load(@Nonnull ExpressionTemplate key) {
            return new PlayerSetPartition(context.getTabEventQueue(),
                    SharedPlayerSet.this,
                    logger, key,
                    context);
        }
    });

    private final Cache<PlayerOrderTemplate, OrderedPlayerSet> cacheOrdered = CacheBuilder.newBuilder().weakValues().build();

    public SharedPlayerSet(PlayerProvider playerProvider, ExpressionTemplate template, ScheduledExecutorService eventQueue, Logger logger) {
        super(eventQueue, playerProvider, logger, template, Context.from(null, eventQueue));
    }

    @Override
    @SneakyThrows
    public PlayerSetPartition getPartition(ExpressionTemplate partitionFunction) {
        return cachePartition.get(partitionFunction);
    }

    @Override
    @SneakyThrows
    public OrderedPlayerSet getOrderedPlayerSet(Context context, PlayerOrderTemplate playerOrderTemplate) {
        if (playerOrderTemplate.requiresViewerContext())
            return super.getOrderedPlayerSet(context, playerOrderTemplate);
        else
            return cacheOrdered.get(playerOrderTemplate, () -> {
                return new OrderedPlayerSetImpl(this, logger, SharedPlayerSet.this.context, playerOrderTemplate);
            });
    }
}
