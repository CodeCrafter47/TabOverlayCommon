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

import de.codecrafter47.taboverlay.Icon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public interface SimpleTabOverlay extends TabOverlayHandle.BatchModifiable, TabOverlayHandle {

    /**
     * Get the current size of the tab overlay.
     *
     * @return the size
     */
    int getSize();

    /**
     * Get the maximum supported size.
     *
     * @return the maximum supported size
     */
    int getMaxSize();

    /**
     * Set the size of the tab overlay.
     *
     * @param size the new size
     * @throws IllegalArgumentException if {@code size} is not a supported size.
     */
    void setSize(int size);

    /**
     * Set the content of a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param index index of the slot
     * @param uuid  the uuid to use for the slot. For player slots use the players uuid, otherwise use {@code null}
     * @param icon  the icon to display
     * @param text  the text to display
     * @param ping  the ping to display
     * @throws IndexOutOfBoundsException if the give position is invalid
     * @throws NullPointerException      if {@code icon} or {@code text} are {@code null}
     */
    void setSlot(int index, @Nullable UUID uuid, @Nonnull Icon icon, @Nonnull String text, int ping);

    /**
     * Set the content of a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param index index of the slot
     * @param icon  the icon to display
     * @param text  the text to display
     * @param ping  the ping to display
     * @throws IndexOutOfBoundsException if the give position is invalid
     * @throws NullPointerException      if {@code icon} or {@code text} are {@code null}
     */
    default void setSlot(int index, @Nonnull Icon icon, @Nonnull String text, int ping) {
        setSlot(index, null, icon, text, ping);
    }

    /**
     * Set the uuid to use for a slot. For player slots use the players uuid, otherwise use {@code null}.
     *
     * @param index index of the slot
     * @param uuid  the uuid
     * @throws IndexOutOfBoundsException if the give position is invalid
     */
    void setUuid(int index, @Nullable UUID uuid);

    /**
     * Set the icon to use for a slot.
     *
     * @param index index of the slot
     * @param icon  the icon
     * @throws IndexOutOfBoundsException if the give position is invalid
     * @throws NullPointerException      if {@code icon} is {@code null}
     */
    void setIcon(int index, @Nonnull Icon icon);

    /**
     * Set the text to display on a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a href=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param index index of the slot
     * @param text  the text
     * @throws IndexOutOfBoundsException if the give position is invalid
     * @throws NullPointerException      if {@code text} is {@code null}
     */
    void setText(int index, @Nonnull String text);

    /**
     * Set the ping for a slot.
     *
     * @param index index of the slot
     * @param ping  the ping to display
     * @throws IndexOutOfBoundsException if the give position is invalid
     */
    void setPing(int index, int ping);
}
