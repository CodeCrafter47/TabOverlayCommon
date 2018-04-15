package de.codecrafter47.taboverlay.config.platform;

import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.player.Player;

public interface EventListener {

    void onTabViewAdded(TabView tabView, Player viewer);

    void onTabViewRemoved(TabView tabView);
}
