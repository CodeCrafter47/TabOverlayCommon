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

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.view.ActiveElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PingView extends ActiveElement {

    int getPing();

    /**
     * Activates the element
     *
     * @param context  the context
     * @param listener the listener
     * @throws IllegalStateException if the element is already active
     */
    void activate(@Nonnull Context context, @Nullable PingViewUpdateListener listener);
}
