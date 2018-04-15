package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;

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
            return new SlowPlayerSet(playerProvider, template, context, logger);
        }
    }
}
