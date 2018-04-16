package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

import java.util.logging.Logger;

public class SlowPlayerSet extends AbstractPlayerSet implements PlayerSet {

    public SlowPlayerSet(PlayerProvider playerProvider, ExpressionTemplate template, Context context, Logger logger) {
        super(context.getTabEventQueue(), playerProvider, logger, template, context);
    }

    @Override
    public PlayerSetPartition getPartition(ExpressionTemplate partitionFunction) {
        return new PlayerSetPartition(context.getTabEventQueue(), this, logger, partitionFunction, context);
    }
}
