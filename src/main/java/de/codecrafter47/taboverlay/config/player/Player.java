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

package de.codecrafter47.taboverlay.config.player;

import de.codecrafter47.data.api.DataHolder;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * A player which may be displayed on the tab overlay.
 */
public interface Player extends DataHolder {
    /**
     * Get the username of the player.
     *
     * @return the username
     */
    @Nonnull
    String getName();

    /**
     * Get the uuid of the player.
     *
     * @return the uuid of the player
     */
    @Nonnull
    UUID getUniqueID();
}
