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

import com.google.common.base.Preconditions;
import de.codecrafter47.taboverlay.Icon;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * A rectangular tab overlay.
 */
public interface RectangularTabOverlay extends TabOverlayHandle.BatchModifiable, TabOverlayHandle {

    /**
     * Get the current size of the tab overlay.
     *
     * @return a {@link Dimension} object holding the size
     */
    Dimension getSize();

    /**
     * Get all supported sizes. The returned {@link Collection} is not modifiable.
     *
     * @return a {@link Collection} containing all supported sizes.
     */
    Collection<Dimension> getSupportedSizes();

    /**
     * Set the size of the tab overlay.
     *
     * @param size the new size
     * @throws NullPointerException     if {@code size} is null
     * @throws IllegalArgumentException if {@code size} is not a supported size.
     */
    void setSize(@Nonnull Dimension size);

    /**
     * Set the content of a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param uuid   the uuid to use for the slot. For player slots use the players uuid, otherwise use {@code null}
     * @param icon   the icon to display
     * @param text   the text to display
     * @param ping   the ping to display
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     * @throws NullPointerException      if {@code icon} or {@code text} are {@code null}
     */
    void setSlot(int column, int row, @Nullable UUID uuid, @Nonnull Icon icon, @Nonnull String text, int ping);

    /**
     * Set the content of a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param icon   the icon to display
     * @param text   the text to display
     * @param ping   the ping to display
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     * @throws NullPointerException      if {@code icon} or {@code text} are {@code null}
     */
    default void setSlot(int column, int row, @Nonnull Icon icon, @Nonnull String text, int ping) {
        setSlot(column, row, null, icon, text, ping);
    }

    /**
     * Set the uuid to use for a slot. For player slots use the players uuid, otherwise use {@code null}.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param uuid   the uuid
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     */
    void setUuid(int column, int row, UUID uuid);

    /**
     * Set the icon to use for a slot.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param icon   the icon
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     * @throws NullPointerException      if {@code icon} is {@code null}
     */
    void setIcon(int column, int row, @Nonnull Icon icon);

    /**
     * Set the text to display on a slot.
     * <p>
     * The text is provided as plain text and may contain legacy <a url=http://minecraft.gamepedia.com/Formatting_codes>formatting codes</a>.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param text   the text
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     * @throws NullPointerException      if {@code text} is {@code null}
     */
    void setText(int column, int row, @Nonnull String text);

    /**
     * Set the ping for a slot.
     *
     * @param column the column identifying the slot
     * @param row    the row identifying the slot
     * @param ping   the ping to display
     * @throws IndexOutOfBoundsException if the position specified by {@code column} and {@code row} is not inside the
     *                                   tab list
     */
    void setPing(int column, int row, int ping);

    /**
     * Represents the size of a rectangular tab overlay.
     */
    @Value
    class Dimension {
        int columns;
        int rows;
        int size;

        public Dimension(int columns, int rows) {
            Preconditions.checkArgument(columns > 0, "columns must be positive (is {0})", columns);
            Preconditions.checkArgument(rows >= 0, "rows must not be negative (is {0})", rows);
            this.columns = columns;
            this.rows = rows;
            this.size = columns * rows;
        }
    }
}
