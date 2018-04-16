package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.dsl.PlayerSetConfiguration;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplates;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PlayerSetFactory {

    private final PlayerProvider playerProvider;
    private final GlobalPlayerSetFactory globalPlayerSetFactory;
    private final Logger logger;
    private final Context context;
    private final Map<PlayerSetTemplate, PlayerSet> cache = new HashMap<>();

    public PlayerSetFactory(PlayerProvider playerProvider, GlobalPlayerSetFactory globalPlayerSetFactory, Logger logger, Context context) {
        this.playerProvider = playerProvider;
        this.globalPlayerSetFactory = globalPlayerSetFactory;
        this.logger = logger;
        this.context = context;
    }

    public PlayerSet getInstance(PlayerSetTemplate template) {
        return cache.computeIfAbsent(template, this::createPlayerSet);
    }

    private PlayerSet createPlayerSet(PlayerSetTemplate template) {
        PlayerSet playerSet = globalPlayerSetFactory.getSharedInstance(template);
        if (playerSet != null) {
            return playerSet;
        } else {
            switch (template.getHiddenPlayersVisibility()) {
                case VISIBLE:
                    return new SlowPlayerSet(playerProvider, template.getPredicate(), context, logger);
                case VISIBLE_TO_ADMINS:
                    return new VisibleToAdminsPlayerSetWrapper(this,
                            context,
                            logger,
                            PlayerSetTemplate.builder().predicate(template.getPredicate()).hiddenPlayersVisibility(PlayerSetConfiguration.Visibility.VISIBLE).build(),
                            PlayerSetTemplate.builder().predicate(template.getPredicate()).hiddenPlayersVisibility(PlayerSetConfiguration.Visibility.INVISIBLE).build(),
                            globalPlayerSetFactory.expressionTemplateCanSeeInvisible);
                case INVISIBLE:
                    return new SlowPlayerSet(playerProvider, ExpressionTemplates.and(Arrays.asList(template.getPredicate(), globalPlayerSetFactory.expressionTemplateIsVisible)), context, logger);
                default:
                    throw new AssertionError("Unknown player set visibility " + template.getHiddenPlayersVisibility());
            }
        }
    }
}
