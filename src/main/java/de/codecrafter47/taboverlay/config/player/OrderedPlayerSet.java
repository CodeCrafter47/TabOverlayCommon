package de.codecrafter47.taboverlay.config.player;

public interface OrderedPlayerSet {

    int getCount();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    Player getPlayer(int index);

    interface Listener {

        void onPlayerRemoved(Player player);

        void onUpdate(boolean newPlayers);
    }
}
