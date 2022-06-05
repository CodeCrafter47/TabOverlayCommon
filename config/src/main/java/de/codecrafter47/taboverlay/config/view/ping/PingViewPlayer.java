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

package de.codecrafter47.taboverlay.config.view.ping;

import de.codecrafter47.data.api.DataHolder;
import de.codecrafter47.data.api.DataKey;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.placeholder.PlayerPlaceholderResolver;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

public class PingViewPlayer extends AbstractActiveElement<PingViewUpdateListener> implements PingView, Runnable {

    private DataHolder dataHolder;
    private final PlayerPlaceholderResolver.BindPoint bindPoint;
    private final DataKey<Integer> dataKey;

    public PingViewPlayer(PlayerPlaceholderResolver.BindPoint bindPoint, DataKey<Integer> dataKey) {
        this.bindPoint = bindPoint;
        this.dataKey = dataKey;
    }

    @Override
    public int getPing() {
        Integer ping = dataHolder.get(dataKey);
        return ping != null ? ping : 0;
    }

    @Override
    protected void onActivation() {
        this.dataHolder = getDataHolder(getContext(), bindPoint);
        dataHolder.addDataChangeListener(dataKey, this);
    }

    @Override
    protected void onDeactivation() {
        dataHolder.removeDataChangeListener(dataKey, this);
        this.dataHolder = null;
    }

    @Override
    public void run() {
        if (hasListener()) {
            getListener().onPingUpdated();
        }
    }

    private static DataHolder getDataHolder(Context context, PlayerPlaceholderResolver.BindPoint bindPoint) {
        DataHolder dataHolder;
        if (bindPoint == PlayerPlaceholderResolver.BindPoint.PLAYER) {
            dataHolder = context.getPlayer();
        } else if (bindPoint == PlayerPlaceholderResolver.BindPoint.VIEWER) {
            dataHolder = context.getViewer();
        } else {
            throw new AssertionError();
        }
        if (dataHolder == null) {
            throw new AssertionError(bindPoint.toString() + " not available");
        }
        return dataHolder;
    }
}
