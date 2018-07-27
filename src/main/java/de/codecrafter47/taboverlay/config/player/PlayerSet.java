package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.PlayerOrderTemplate;

import java.util.Collection;

public interface PlayerSet {

    int getCount();

    void addListener(Listener listener);

    void removeListener(Listener listener);

    Collection<? extends Player> getPlayers();

    OrderedPlayerSet getOrderedPlayerSet(Context context, PlayerOrderTemplate playerOrderTemplate);

    PlayerSetPartition getPartition(ExpressionTemplate partitionFunction);

    interface Listener {

        void onPlayerAdded(Player player);

        void onPlayerRemoved(Player player);
    }
}
