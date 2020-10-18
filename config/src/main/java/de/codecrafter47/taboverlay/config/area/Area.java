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

package de.codecrafter47.taboverlay.config.area;

import com.google.common.base.Preconditions;
import de.codecrafter47.taboverlay.Icon;

import java.util.UUID;

public interface Area {

    default void setSlot(int index, Icon icon, String text, int ping) {
        setSlot(index, null, icon, text, ping);
    }

    void setSlot(int index, UUID uuid, Icon icon, String text, int ping);

    void setUuid(int index, UUID uuid);

    void setIcon(int index, Icon icon);

    void setText(int index, String text);

    void setPing(int index, int ping);

    int getSize();

    int getSlotWidth();

    default Area createChild(int firstIndex, int size) {
        Preconditions.checkArgument(firstIndex < getSize(), "firstIndex < getSize()");
        Preconditions.checkArgument(firstIndex + size <= getSize(), "firstIndex + size <= getSize()");
        return new AreaWithOffset(this, firstIndex, size);
    }

    RectangularArea asRectangularArea();
}
