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

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractPlayerSet implements PlayerSet {
    private final PlayerProvider playerProvider;
    final Logger logger;
    private final ExpressionTemplate predicate;
    protected final Context context;
    private final HashSet<Listener> listeners = new HashSet<>();
    private final MyListener listener = new MyListener();
    private final Map<Player, PlayerEntry> playerEntryMap = new HashMap<>();
    private final Set<Player> containedPlayers = new HashSet<>();
    private boolean active = false;

    private boolean isNotifyingListeners = false;

    AbstractPlayerSet(ScheduledExecutorService eventQueue, PlayerProvider playerProvider, Logger logger, ExpressionTemplate predicate, Context context) {
        this.logger = logger;
        this.context = context;
        this.playerProvider = playerProvider;
        this.predicate = predicate;
    }

    private void activate() {
        playerProvider.registerListener(listener);

        for (Player player : playerProvider.getPlayers()) {
            playerEntryMap.put(player, new PlayerEntry(player));
        }

        active = true;
    }

    private void deactivate() {
        for (PlayerEntry playerEntry : playerEntryMap.values()) {
            playerEntry.deactivate();
        }
        playerEntryMap.clear();
        playerProvider.unregisterListener(listener);

        active = false;
    }

    @Override
    public int getCount() {
        return containedPlayers.size();
    }

    @Override
    public void addListener(Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be added while notifying listeners");
        }
        if (!active) {
            activate();
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be removed while notifying listeners");
        }
        listeners.remove(listener);
        if (listeners.isEmpty() && active) {
            deactivate();
        }
    }

    @Override
    public Collection<? extends Player> getPlayers() {
        return containedPlayers;
    }

    @Override
    public OrderedPlayerSet getOrderedPlayerSet(Context context, PlayerOrderTemplate playerOrderTemplate) {
        return new OrderedPlayerSetImpl(this, logger, context, playerOrderTemplate);
    }

    private void addPlayerAndNotifyListeners(Player player) {
        AbstractPlayerSet.this.containedPlayers.add(player);
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

    private void removePlayerAndNotifyListeners(Player player) {
        AbstractPlayerSet.this.containedPlayers.remove(player);
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

    private class PlayerEntry implements ActiveElement, ExpressionUpdateListener {
        private final Player player;
        private final ToBooleanExpression predicate;
        private boolean included;

        private PlayerEntry(Player player) {
            this.player = player;
            predicate = AbstractPlayerSet.this.predicate.instantiateWithBooleanResult();
            Context childContext = AbstractPlayerSet.this.context.clone();
            childContext.setPlayer(player);
            predicate.activate(childContext, this);
            if (this.included = predicate.evaluate()) {
                addPlayerAndNotifyListeners(player);
            }
        }

        @Override
        public void onExpressionUpdate() {
            boolean include = predicate.evaluate();
            if (include != included) {
                if (include) {
                    addPlayerAndNotifyListeners(player);

                } else {
                    removePlayerAndNotifyListeners(player);

                }
                included = include;
            }
        }

        @Override
        public void deactivate() {
            predicate.deactivate();
            if (included) {
                removePlayerAndNotifyListeners(player);
            }
        }
    }

    private class MyListener implements PlayerProvider.Listener {
        @Override
        public void onPlayerAdded(Player player) {
            playerEntryMap.put(player, new PlayerEntry(player));
        }

        @Override
        public void onPlayerRemoved(Player player) {
            PlayerEntry playerEntry = playerEntryMap.remove(player);
            if (playerEntry != null) {
                playerEntry.deactivate();
            } else {
                // this shouldn't happen
                throw new AssertionError("Tried to remove a player that is not part of the player set");
            }
        }
    }
}
