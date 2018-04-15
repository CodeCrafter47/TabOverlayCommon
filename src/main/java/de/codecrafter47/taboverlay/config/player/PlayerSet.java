package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

import java.util.Collection;

public interface PlayerSet {

    int getCount();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    Collection<? extends Player> getPlayers();

    PlayerSetPartition getPartition(ExpressionTemplate partitionFunction);

    interface Listener {

        void onPlayerAdded(Player player);

        void onPlayerRemoved(Player player);
    }
}
