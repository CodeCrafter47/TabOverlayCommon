package de.codecrafter47.taboverlay.config.player;

import java.util.Collection;

/**
 * Provides access to players.
 *
 * All methods are expected to be called from the main thread.
 */
public interface PlayerProvider {

    Collection<? extends Player> getPlayers();

    void registerListener(Listener listener);

    void unregisterListener(Listener listener);

    interface Listener {

        void onPlayerAdded(Player player);

        void onPlayerRemoved(Player player);
    }
}
