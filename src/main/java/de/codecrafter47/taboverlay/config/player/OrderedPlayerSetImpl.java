package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholder;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;

import java.text.Collator;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderedPlayerSetImpl implements OrderedPlayerSet {

    private final PlayerSet playerSet;
    private final Logger logger;
    protected final Context context;

    private final Comparator<Player> comparator;
    private final List<DataKey<?>> dependentDataKeys;

    private final HashSet<Listener> listeners = new HashSet<>();
    private final MyListener listener = new MyListener();
    private final List<Player> containedPlayers = new ArrayList<>();
    private final List<Player> pendingPlayers = new ArrayList<>();

    private Player viewer;
    private boolean active = false;

    private Future<?> updateFuture = null;

    private boolean isNotifyingListeners = false;

    public OrderedPlayerSetImpl(PlayerSet playerSet, Logger logger, Context context, PlayerOrderTemplate playerOrderTemplate) {
        this.playerSet = playerSet;
        this.logger = logger;
        this.context = context;
        this.dependentDataKeys = new ArrayList<>();

        Comparator<Player> chain = null;
        for (PlayerOrderTemplate.Entry entry : playerOrderTemplate.getEntries()) {
            Comparator<Player> comparator = null;
            PlayerPlaceholder<?, ?> placeholder = entry.getPlaceholder();
            switch (entry.getType()) {
                case TEXT:
                    comparator = Comparator.comparing(placeholder.getToStringFunction(), Collator.getInstance());
                    break;
                case NUMBER:
                    comparator = Comparator.comparingDouble(placeholder.getToDoubleFunction());
                    break;
            }
            switch (entry.getDirection()) {
                case ASCENDING:
                    break;
                case DESCENDING:
                    comparator = comparator.reversed();
                    break;
                case VIEWER_FIRST:
                    viewer = context.getViewer();
                    comparator = Comparator.comparingInt(player -> Objects.equals(placeholder.getString(player), placeholder.getString(viewer)) ? 0 : 1);
                    break;
            }
            if (chain == null) {
                chain = comparator;
            } else {
                chain = chain.thenComparing(comparator);
            }
            if (placeholder.getDataKey() != null && !dependentDataKeys.contains(placeholder.getDataKey())) {
                dependentDataKeys.add(placeholder.getDataKey());
            }
        }
        if (chain == null) {
            chain = Comparator.comparingInt(player -> 0);
        }
        this.comparator = chain;
    }

    private void registerListeners(Player player) {
        for (DataKey<?> dataKey : dependentDataKeys) {
            player.addDataChangeListener(dataKey, listener);
        }
    }

    private void unregisterListeners(Player player) {
        for (DataKey<?> dataKey : dependentDataKeys) {
            player.removeDataChangeListener(dataKey, listener);
        }
    }

    private void activate() {
        playerSet.addListener(listener);

        if (viewer != null) {
            registerListeners(viewer);
        }
        for (Player player : playerSet.getPlayers()) {
            registerListeners(player);
            containedPlayers.add(player);
        }
        containedPlayers.sort(comparator);

        active = true;
    }

    private void deactivate() {
        if (viewer != null) {
            unregisterListeners(viewer);
        }
        for (Player player : playerSet.getPlayers()) {
            unregisterListeners(player);
        }
        containedPlayers.clear();
        pendingPlayers.clear();

        playerSet.removeListener(listener);

        active = false;

        if (updateFuture != null && !updateFuture.isDone()) {
            updateFuture.cancel(false);
        }
    }

    private void scheduleUpdate(int ms) {
        if (updateFuture == null || updateFuture.isDone()) {
            updateFuture = context.getTabEventQueue().schedule(this::update, ms, TimeUnit.MILLISECONDS);
        }
    }

    private void update() {
        containedPlayers.addAll(pendingPlayers);
        containedPlayers.sort(comparator);
        notifyListenersOfUpdate(!pendingPlayers.isEmpty());
        pendingPlayers.clear();
    }

    private void notifyListenersOfRemovedPlayer(Player player) {
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

    private void notifyListenersOfUpdate(boolean newPlayers) {
        isNotifyingListeners = true;
        try {
            for (Listener listener : listeners) {
                try {
                    listener.onUpdate(newPlayers);
                } catch (Throwable th) {
                    logger.log(Level.SEVERE, "Unexpected exception while notifying listener", th);
                }
            }
        } finally {
            isNotifyingListeners = false;
        }
    }

    @Override
    public void addListener(Listener listener) {
        if (isNotifyingListeners) {
            throw new IllegalStateException("Listeners cannot be added while notifying listeners");
        }
        listeners.add(listener);
        if (!active) {
            activate();
        }
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
    public int getCount() {
        return containedPlayers.size();
    }

    @Override
    public Player getPlayer(int index) {
        return containedPlayers.get(index);
    }

    private class MyListener implements Runnable, PlayerSet.Listener {

        @Override
        public void run() {
            scheduleUpdate(1000);
        }

        @Override
        public void onPlayerAdded(Player player) {
            registerListeners(player);
            pendingPlayers.add(player);
            scheduleUpdate(500);
        }

        @Override
        public void onPlayerRemoved(Player player) {
            if (player != viewer) {
                unregisterListeners(player);
            }
            if (!pendingPlayers.remove(player)) {
                if (containedPlayers.remove(player)) {
                    notifyListenersOfRemovedPlayer(player);
                }
            }
        }
    }
}
