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

public class RectangularAreaWithOffset implements RectangularArea {

    private final RectangularArea delegate;
    private final int offsetColumns;
    private final int offsetRows;
    private final int sizeColumns;
    private final int sizeRows;
    private final int size;

    public RectangularAreaWithOffset(RectangularArea delegate, int offsetColumns, int offsetRows, int sizeColumns, int sizeRows) {
        this.delegate = delegate;
        this.offsetColumns = offsetColumns;
        this.offsetRows = offsetRows;
        this.sizeColumns = sizeColumns;
        this.sizeRows = sizeRows;
        this.size = sizeColumns * sizeRows;
    }


    @Override
    public void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setSlot(column + offsetColumns, row + offsetRows, uuid, icon, text, ping);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setSlot(column + offsetColumns, row + offsetRows, uuid, icon, text, alternateColorChar, ping);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setUuid(int column, int row, UUID uuid) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setUuid(column + offsetColumns, row + offsetRows, uuid);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setIcon(int column, int row, Icon icon) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setIcon(column + offsetColumns, row + offsetRows, icon);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setText(int column, int row, String text) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setText(column + offsetColumns, row + offsetRows, text);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setText(int column, int row, String text, char alternateColorChar) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setText(column + offsetColumns, row + offsetRows, text, alternateColorChar);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public void setPing(int column, int row, int ping) {
        if (column < sizeColumns && row < sizeRows) {
            delegate.setPing(column + offsetColumns, row + offsetRows, ping);
        } else {
            throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=" + getColumns() + ", rows=" + getRows() + ").");
        }
    }

    @Override
    public int getRows() {
        return sizeRows;
    }

    @Override
    public int getColumns() {
        return sizeColumns;
    }

    @Override
    public RectangularArea createRectangularChild(int column, int row, int columns, int rows) {
        Preconditions.checkArgument(column < sizeColumns, "column < sizeColumns");
        Preconditions.checkArgument(row < sizeRows, "row < sizeRows");
        Preconditions.checkArgument(column + columns <= sizeColumns, "column + columns <= sizeColumns");
        Preconditions.checkArgument(row + rows <= sizeRows, "row + rows <= sizeRows");
        return new RectangularAreaWithOffset(delegate, this.offsetColumns + column, this.offsetRows + row, columns, rows);
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getSlotWidth() {
        return delegate.getSlotWidth();
    }
}
