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

package de.codecrafter47.taboverlay.spectator;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.TabOverlayProvider;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.platform.EventListener;
import de.codecrafter47.taboverlay.config.platform.Platform;
import de.codecrafter47.taboverlay.config.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class SpectatorPassthroughTabOverlayManager {

    private final Platform platform;
    private final ScheduledExecutorService tabEventLoop;
    private final DataKey<Integer> dataKeyGamemode;
    private final HashMap<TabView, Player> tabViews = new HashMap<>();
    private boolean enabled = false;

    public SpectatorPassthroughTabOverlayManager(Platform platform, ScheduledExecutorService tabEventLoop, DataKey<Integer> dataKeyGamemode) {
        this.platform = platform;
        this.tabEventLoop = tabEventLoop;
        this.dataKeyGamemode = dataKeyGamemode;
        platform.addEventListener(new Listener());
    }

    public synchronized void enable() {
        if (!enabled) {
            enabled = true;
            for (Map.Entry<TabView, Player> entry : tabViews.entrySet()) {
                TabView tabView = entry.getKey();
                Player player = entry.getValue();
                TabOverlayProvider provider = new SpectatorPassthroughTabOverlayProvider(player, tabEventLoop, dataKeyGamemode);
                tabView.getTabOverlayProviders().addProvider(provider);
            }
        }
    }

    public synchronized void disable() {
        if (enabled) {
            enabled = false;
            for (Map.Entry<TabView, Player> entry : tabViews.entrySet()) {
                entry.getKey().getTabOverlayProviders().removeProviders(SpectatorPassthroughTabOverlayProvider.class);
            }
        }
    }

    private class Listener implements EventListener {

        @Override
        public void onTabViewAdded(TabView tabView, Player viewer) {
            synchronized (SpectatorPassthroughTabOverlayManager.this) {
                tabViews.put(tabView, viewer);
                if (enabled) {
                    TabOverlayProvider provider = new SpectatorPassthroughTabOverlayProvider(viewer, tabEventLoop, dataKeyGamemode);
                    tabView.getTabOverlayProviders().addProvider(provider);
                }
            }
        }

        @Override
        public void onTabViewRemoved(TabView tabView) {
            synchronized (SpectatorPassthroughTabOverlayManager.this) {
                tabViews.remove(tabView);
                tabView.getTabOverlayProviders().removeProviders(SpectatorPassthroughTabOverlayProvider.class);
            }
        }
    }
}
