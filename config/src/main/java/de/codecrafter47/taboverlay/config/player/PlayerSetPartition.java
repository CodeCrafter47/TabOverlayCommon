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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;
import de.codecrafter47.taboverlay.config.view.ActiveElement;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class PlayerSetPartition {

    private final PlayerSet playerSet;
    private final Logger logger;
    protected final ExpressionTemplate partitionFunction;
    protected final Context context;
    private final HashSet<Listener> listeners = new HashSet<>();
    private final MyListener listener = new MyListener();
    private final Map<Player, PlayerEntry> playerEntryMap = new HashMap<>();
    private final Map<String, PlayerSetSubset> partitions = new HashMap<>();
    private boolean active = false;

    private boolean isNotifyingListeners = false;

    public PlayerSetPartition(ScheduledExecutorService eventQueue, PlayerSet playerSet, Logger logger, ExpressionTemplate partitionFunction, Context context) {
        this.logger = logger;
        this.context = context;
        this.playerSet = playerSet;
        this.partitionFunction = partitionFunction;
    }

    private void activate(boolean notify) {
        playerSet.addListener(listener);

        for (Player player : playerSet.getPlayers()) {
            playerEntryMap.put(player, new PlayerEntry(player, notify));
        }

        active = true;
    }

    private void deactivate() {
        for (PlayerEntry playerEntry : playerEntryMap.values()) {
            playerEntry.deactivate();
        }
        playerEntryMap.clear();
        playerSet.removeListener(listener);

        active = false;
    }

    public Collection<? extends Map.Entry<String, ? extends PlayerSet>> getPartitions() {
        return partitions.entrySet();
    }

    public PlayerSet getPartition(String name) {
        return partitions.get(name);
    }

    public void addListener(PlayerSetPartition.Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be added while notifying listeners");
        }
        listeners.add(listener);
        if (!active) {
            activate(false);
        }
    }

    public void removeListener(PlayerSetPartition.Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be removed while notifying listeners");
        }
        listeners.remove(listener);
        if (listeners.isEmpty() && active) {
            deactivate();
        }
    }

    private class PlayerEntry implements ActiveElement, ExpressionUpdateListener {
        private final Player player;
        private final ToStringExpression function;
        private String partition;

        private PlayerEntry(Player player, boolean notify) {
            this.player = player;
            this.function = PlayerSetPartition.this.partitionFunction.instantiateWithStringResult();
            Context childContext = PlayerSetPartition.this.context.clone();
            childContext.setPlayer(player);
            function.activate(childContext, this);
            this.partition = function.evaluate();
            addToPartition(partition, notify);
        }

        void addToPartition(String p, boolean notify) {
            if (!partitions.containsKey(p)) {
                PlayerSetSubset subset = new PlayerSetSubset(context, logger);
                subset.add(player);
                partitions.put(p, subset);
                if (notify) {
                    isNotifyingListeners = true;
                    try {
                        for (Listener listener1 : listeners) {
                            try {
                                listener1.onPartitionAdded(p, subset);
                            } catch (Throwable th) {
                                logger.log(Level.SEVERE, "Unexpected exception while notifying listener", th);
                            }
                        }
                    } finally {
                        isNotifyingListeners = false;
                    }
                }
            } else {
                partitions.get(p).add(player);
            }
        }

        void removeFromPartition(String p) {
            PlayerSetSubset subset = partitions.get(p);
            subset.remove(player);
            if (subset.getCount() == 0) {
                partitions.remove(p);
                isNotifyingListeners = true;
                try {
                    for (Listener listener1 : listeners) {
                        try {
                            listener1.onPartitionRemoved(p);
                        } catch (Throwable th) {
                            logger.log(Level.SEVERE, "Unexpected exception while notifying listener", th);
                        }
                    }
                } finally {
                    isNotifyingListeners = false;
                }

            }
        }

        @Override
        public void onExpressionUpdate() {
            String partition = function.evaluate();
            if (!Objects.equals(partition, this.partition)) {
                removeFromPartition(this.partition);
                this.partition = partition;
                addToPartition(this.partition, true);
            }
        }

        @Override
        public void deactivate() {
            function.deactivate();
            removeFromPartition(this.partition);
        }
    }

    private class MyListener implements PlayerSet.Listener {
        @Override
        public void onPlayerAdded(Player player) {
            playerEntryMap.put(player, new PlayerSetPartition.PlayerEntry(player, true));
        }

        @Override
        public void onPlayerRemoved(Player player) {
            PlayerSetPartition.PlayerEntry playerEntry = playerEntryMap.remove(player);
            if (playerEntry != null) {
                playerEntry.deactivate();
            } else {
                // this shouldn't happen
                throw new AssertionError("Tried to remove a player that is not part of the player set");
            }
        }
    }

    private static class PlayerSetSubset implements PlayerSet {

        private final Context context;
        private final Logger logger;
        private final HashSet<Listener> listeners = new HashSet<>();
        private final Set<Player> containedPlayers = new HashSet<>();
        private final Cache<PlayerOrderTemplate, OrderedPlayerSet> cacheOrdered = CacheBuilder.newBuilder().weakValues().build();

        private boolean isNotifyingListeners = false;

        private PlayerSetSubset(Context context, Logger logger) {
            this.context = context;
            this.logger = logger;
        }

        void add(Player player) {
            containedPlayers.add(player);
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

        void remove(Player player) {
            containedPlayers.remove(player);
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
        public int getCount() {
            return containedPlayers.size();
        }

        @Override
        public void addListener(Listener listener) {
            if (isNotifyingListeners) {
                throw new IllegalStateException("Listeners cannot be added while notifying listeners");
            }
            listeners.add(listener);
        }

        @Override
        public void removeListener(Listener listener) {
            if (isNotifyingListeners) {
                throw new IllegalStateException("Listeners cannot be removed while notifying listeners");
            }
            listeners.remove(listener);
        }

        @Override
        public Collection<? extends Player> getPlayers() {
            return containedPlayers;
        }

        @Override
        public PlayerSetPartition getPartition(ExpressionTemplate idFunction) {
            throw new UnsupportedOperationException("Partition inside partition is not supported");
        }

        @Override
        @SneakyThrows
        public OrderedPlayerSet getOrderedPlayerSet(Context context, PlayerOrderTemplate playerOrderTemplate) {
            if (playerOrderTemplate.requiresViewerContext())
                return new OrderedPlayerSetImpl(this, logger, context, playerOrderTemplate);
            else
                return cacheOrdered.get(playerOrderTemplate, () -> {
                    return new OrderedPlayerSetImpl(this, logger, PlayerSetSubset.this.context, playerOrderTemplate);
                });
        }
    }

    public interface Listener {

        void onPartitionAdded(String id, PlayerSet playerSet);

        void onPartitionRemoved(String id);
    }
}
