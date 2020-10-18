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

package de.codecrafter47.taboverlay.config.spectator;

import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.AbstractTabOverlayProvider;
import de.codecrafter47.taboverlay.TabOverlayProviderSet;
import de.codecrafter47.taboverlay.TabView;
import de.codecrafter47.taboverlay.config.player.Player;
import de.codecrafter47.taboverlay.handler.ContentOperationMode;
import de.codecrafter47.taboverlay.handler.HeaderAndFooterOperationMode;
import de.codecrafter47.taboverlay.handler.TabOverlayHandle;
import lombok.SneakyThrows;

import java.util.concurrent.ScheduledExecutorService;

public class SpectatorPassthroughTabOverlayProvider extends AbstractTabOverlayProvider<TabOverlayHandle, TabOverlayHandle> implements Runnable {

    private final Player player;
    private final ScheduledExecutorService tabEventLoop;
    private final DataKey<Integer> DATA_KEY_GAMEMODE;
    private boolean shouldBeActive = false;
    private TabOverlayProviderSet tabOverlayProviderSet;

    public SpectatorPassthroughTabOverlayProvider(Player player, ScheduledExecutorService tabEventLoop, DataKey<Integer> data_key_gamemode) {
        super("spectator-passthrough", 10002, ContentOperationMode.PASS_TROUGH, HeaderAndFooterOperationMode.PASS_TROUGH);
        this.player = player;
        this.tabEventLoop = tabEventLoop;
        DATA_KEY_GAMEMODE = data_key_gamemode;
    }

    @Override
    protected void activate(TabView tabView, TabOverlayHandle contentHandle, TabOverlayHandle headerAndFooterHandle) {

    }

    @Override
    @SneakyThrows
    protected void attach(TabView tabView) {
        tabEventLoop.submit(() -> {
            tabOverlayProviderSet = tabView.getTabOverlayProviders();
            player.addDataChangeListener(DATA_KEY_GAMEMODE, this);
            Integer gamemode = player.get(DATA_KEY_GAMEMODE);
            shouldBeActive = gamemode != null && gamemode == 3;
        }).get();
    }

    @Override
    @SneakyThrows
    protected void detach(TabView tabView) {
        tabEventLoop.submit(() -> {
            player.removeDataChangeListener(DATA_KEY_GAMEMODE, this);
        }).get();
    }

    @Override
    protected void deactivate(TabView tabView) {

    }

    @Override
    protected boolean shouldActivate(TabView tabView) {
        return shouldBeActive;
    }

    @Override
    public void run() {
        Integer gamemode = player.get(DATA_KEY_GAMEMODE);
        if (shouldBeActive != (shouldBeActive = gamemode != null && gamemode == 3)) {
            tabOverlayProviderSet.scheduleUpdate();
        }
    }
}
