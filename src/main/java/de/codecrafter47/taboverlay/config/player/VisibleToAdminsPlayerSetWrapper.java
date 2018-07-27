package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerSetTemplate;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisibleToAdminsPlayerSetWrapper implements PlayerSet {
    private final PlayerSetFactory playerSetFactory;
    protected final Context context;
    private final Logger logger;
    private final PlayerSetTemplate templateVisible;
    private final PlayerSetTemplate templateInvisible;
    private final ToBooleanExpression expressionCanSeeHiddenPlayers;

    private final MyListener listener;
    private final HashSet<Listener> listeners = new HashSet<>();
    @Nullable
    private PlayerSet playerSet;
    private boolean canSeeHiddenPlayers;
    private boolean isNotifyingListeners = false;

    public VisibleToAdminsPlayerSetWrapper(PlayerSetFactory playerSetFactory, Context context, Logger logger, PlayerSetTemplate templateVisible, PlayerSetTemplate templateInvisible, ExpressionTemplate canSeeHiddenPlayersTemplate) {
        this.playerSetFactory = playerSetFactory;
        this.context = context;
        this.logger = logger;
        this.templateVisible = templateVisible;
        this.templateInvisible = templateInvisible;
        this.listener = new MyListener();
        this.expressionCanSeeHiddenPlayers = canSeeHiddenPlayersTemplate.instantiateWithBooleanResult();
    }

    private void activate() {
        expressionCanSeeHiddenPlayers.activate(context, listener);
        canSeeHiddenPlayers = expressionCanSeeHiddenPlayers.evaluate();
        playerSet = playerSetFactory.getInstance(canSeeHiddenPlayers ? templateVisible : templateInvisible);
        playerSet.addListener(listener);
    }

    private void deactivate() {
        expressionCanSeeHiddenPlayers.deactivate();
        playerSet.removeListener(listener);
        playerSet = null;
    }

    @Override
    public int getCount() {
        if (playerSet == null) {
            throw new IllegalStateException("Calling PlayerSet.getCount() before registering a listener");
        }
        return playerSet.getCount();
    }

    @Override
    public void addListener(Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be added while notifying listeners");
        }
        listeners.add(listener);
        if (playerSet == null) {
            activate();
        }
    }

    @Override
    public void removeListener(Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be removed while notifying listeners");
        }
        listeners.remove(listener);
        if (listeners.isEmpty() && playerSet != null) {
            deactivate();
        }
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        if (playerSet == null) {
            throw new IllegalStateException("Calling PlayerSet.getPlayers() before registering a listener");
        }
        return playerSet.getPlayers();
    }

    @Override
    public OrderedPlayerSet getOrderedPlayerSet(Context context, PlayerOrderTemplate playerOrderTemplate) {
        // todo make this be shared!
        return new OrderedPlayerSetImpl(this, logger, context, playerOrderTemplate);
    }

    @Override
    public PlayerSetPartition getPartition(ExpressionTemplate partitionFunction) {
        // todo make this be shared!
        return new PlayerSetPartition(context.getTabEventQueue(), this, logger, partitionFunction, context);
    }

    private class MyListener implements Listener, ExpressionUpdateListener {

        @Override
        public void onPlayerAdded(Player player) {
            isNotifyingListeners = true;
            try {
                for (Listener listener : listeners) {
                    try {
                        listener.onPlayerAdded(player);
                    } catch (Throwable th) {
                        logger.log(Level.SEVERE, "Unexpected exception while notifying listener", th);
                    }
                }
            } finally {
                isNotifyingListeners = false;
            }
        }

        @Override
        public void onPlayerRemoved(Player player) {
            isNotifyingListeners = true;
            try {
                for (Listener listener : listeners) {
                    try {
                        listener.onPlayerRemoved(player);
                    } catch (Throwable th) {
                        logger.log(Level.SEVERE, "Unexpected exception while notifying listener", th);
                    }
                }
            } finally {
                isNotifyingListeners = false;
            }
        }

        @Override
        public void onExpressionUpdate() {
            // todo think of something more efficient
            boolean canSeeHiddenPlayers = expressionCanSeeHiddenPlayers.evaluate();
            if (VisibleToAdminsPlayerSetWrapper.this.canSeeHiddenPlayers != canSeeHiddenPlayers) {
                VisibleToAdminsPlayerSetWrapper.this.canSeeHiddenPlayers = canSeeHiddenPlayers;
                playerSet.getPlayers().forEach(this::onPlayerRemoved);
                playerSet.removeListener(listener);
                playerSet = playerSetFactory.getInstance(canSeeHiddenPlayers ? templateVisible : templateInvisible);
                playerSet.addListener(listener);
                playerSet.getPlayers().forEach(this::onPlayerAdded);
            }
        }
    }
}
