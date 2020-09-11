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

public interface TabOverlayHandler {

    /**
     * Change the operation mode of the tab list content.
     *
     * @param operationMode new operation mode
     * @param <R> type representing the tab list for the new operation mode
     * @return representation of the tab list
     * @throws UnsupportedOperationException if the operationMode is not supported.
     */
    <R> R enterContentOperationMode(ContentOperationMode<R> operationMode);

    /**
     * Change the operation mode of the tab list header and footer.
     *
     * @param operationMode new operation mode
     * @param <R> type representing the tab list for the new operation mode
     * @return representation of the tab list
     * @throws UnsupportedOperationException if the operationMode is not supported.
     */
    <R> R enterHeaderAndFooterOperationMode(HeaderAndFooterOperationMode<R> operationMode);
}
