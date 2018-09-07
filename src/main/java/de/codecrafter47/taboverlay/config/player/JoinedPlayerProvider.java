package de.codecrafter47.taboverlay.config.player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JoinedPlayerProvider implements PlayerProvider {

    private final Set<Listener> listeners = new HashSet<>();
    private final Set<Player> players = new HashSet<>();

    public JoinedPlayerProvider(Collection<? extends PlayerProvider> providers) {

        Listener myListener = new Listener() {
            @Override
            public void onPlayerAdded(Player p) {
                players.add(p);
                listeners.forEach(listener -> listener.onPlayerAdded(p));
            }

            @Override
            public void onPlayerRemoved(Player p) {
                players.remove(p);
                listeners.forEach(listener -> listener.onPlayerRemoved(p));
            }
        };

        for (PlayerProvider provider : providers) {
            players.addAll(provider.getPlayers());
            provider.registerListener(myListener);
        }
    }

    @Override
    public Collection<Player> getPlayers() {
        return Collections.unmodifiableCollection(players);
    }

    @Override
    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    @Override
    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }
}
