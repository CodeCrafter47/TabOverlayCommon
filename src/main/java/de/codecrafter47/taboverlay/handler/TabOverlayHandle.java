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

package de.codecrafter47.taboverlay.handler;

public interface TabOverlayHandle {
    /**
     * Checks whether this {@link TabOverlayHandle} is still valid, i.e. whether it is still displayed to a player.
     * <p>
     * Once invalidated a {@link TabOverlayHandle} will remain invalid. So if a call this method returns false, then all
     * subsequent calls will return false.
     *
     * @return true if this {@link TabOverlayHandle} is still valid
     */
    boolean isValid();

    /**
     * Allows multiple changes to a tab overlay to be grouped efficiently.
     * <p>
     * When a batch modification is in progress the implementation may delay updating the tab list to the client, until
     * the batch operation is completed.
     */
    interface BatchModifiable extends TabOverlayHandle {

        /**
         * Marks the begin of a batch modification.
         */
        void beginBatchModification();

        /**
         * Marks the end of a batch modifications.
         */
        void completeBatchModification();
    }
}
