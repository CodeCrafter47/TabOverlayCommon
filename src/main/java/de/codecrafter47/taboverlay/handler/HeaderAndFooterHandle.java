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

import javax.annotation.Nullable;

/**
 * Allows modifying the header and footer of the tab overlay.
 */
public interface HeaderAndFooterHandle extends TabOverlayHandle, TabOverlayHandle.BatchModifiable {

    /**
     * Set the header and footer of the tab list.
     * <p>
     * The header and footer are provided as text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     * <p>
     * A value of null removes the header or footer.
     *
     * @param header the header
     * @param footer the footer
     */
    void setHeaderFooter(@Nullable String header, @Nullable String footer);

    /**
     * Set the header of the tab list.
     * <p>
     * The header is provided as text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     * <p>
     * A value of null removes the header.
     *
     * @param header the header
     */
    void setHeader(@Nullable String header);

    /**
     * Set the footer of the tab list.
     * <p>
     * The footer is provided as text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     * <p>
     * A value of null removes the footer.
     *
     * @param footer the footer
     */
    void setFooter(@Nullable String footer);
}
