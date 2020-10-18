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

public class AreaWithOffset implements Area {

    private final Area delegate;
    private final int offset;
    private final int size;

    private RectangularArea rectangularArea;

    public AreaWithOffset(Area delegate, int offset, int size) {
        this.delegate = delegate;
        this.offset = offset;
        this.size = size;
    }

    @Override
    public void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
        if (index < size) {
            delegate.setSlot(index + offset, uuid, icon, text, alternateColorChar, ping);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
        if (index < size) {
            delegate.setSlot(index + offset, uuid, icon, text, ping);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setUuid(int index, UUID uuid) {
        if (index < size) {
            delegate.setUuid(index + offset, uuid);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setIcon(int index, Icon icon) {
        if (index < size) {
            delegate.setIcon(index + offset, icon);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setText(int index, String text) {
        if (index < size) {
            delegate.setText(index + offset, text);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setText(int index, String text, char alternateColorChar) {
        if (index < size) {
            delegate.setText(index + offset, text, alternateColorChar);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public void setPing(int index, int ping) {
        if (index < size) {
            delegate.setPing(index + offset, ping);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds(size=" + size + ").");
        }
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public int getSlotWidth() {
        return delegate.getSlotWidth();
    }

    @Override
    public Area createChild(int firstIndex, int size) {
        Preconditions.checkArgument(firstIndex < this.size, "firstIndex < this.size");
        Preconditions.checkArgument(firstIndex + size <= this.size, "firstIndex + size <= this.size");
        return new AreaWithOffset(delegate, offset + firstIndex, size);
    }

    @Override
    public RectangularArea asRectangularArea() {
        if (rectangularArea == null) {
            rectangularArea = new RectangularView();
        }
        return rectangularArea;
    }

    private class RectangularView implements RectangularArea {

        @Override
        public void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
            AreaWithOffset.this.setSlot(index, uuid, icon, text, ping);
        }

        @Override
        public void setSlot(int index, Icon icon, String text, char alternateColorChar, int ping) {
            AreaWithOffset.this.setSlot(index, icon, text, alternateColorChar, ping);
        }

        @Override
        public void setSlot(int index, Icon icon, String text, int ping) {
            AreaWithOffset.this.setSlot(index, icon, text, ping);
        }

        @Override
        public void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
            AreaWithOffset.this.setSlot(index, uuid, icon, text, alternateColorChar, ping);
        }

        @Override
        public void setUuid(int index, UUID uuid) {
            AreaWithOffset.this.setUuid(index, uuid);
        }

        @Override
        public void setIcon(int index, Icon icon) {
            AreaWithOffset.this.setIcon(index, icon);
        }

        @Override
        public void setText(int index, String text) {
            AreaWithOffset.this.setText(index, text);
        }

        @Override
        public void setText(int index, String text, char alternateColorChar) {
            AreaWithOffset.this.setText(index, text, alternateColorChar);
        }

        @Override
        public void setPing(int index, int ping) {
            AreaWithOffset.this.setPing(index, ping);
        }

        @Override
        public void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping) {
            if (column == 0) {
                AreaWithOffset.this.setSlot(row, uuid, icon, text, ping);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
            if (column == 0) {
                AreaWithOffset.this.setSlot(row, uuid, icon, text, alternateColorChar, ping);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setUuid(int column, int row, UUID uuid) {
            if (column == 0) {
                AreaWithOffset.this.setUuid(row, uuid);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setIcon(int column, int row, Icon icon) {
            if (column == 0) {
                AreaWithOffset.this.setIcon(row, icon);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setText(int column, int row, String text) {
            if (column == 0) {
                AreaWithOffset.this.setText(row, text);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setText(int column, int row, String text, char alternateColorChar) {
            if (column == 0) {
                AreaWithOffset.this.setText(row, text, alternateColorChar);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public void setPing(int column, int row, int ping) {
            if (column == 0) {
                AreaWithOffset.this.setPing(row, ping);
            } else {
                throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
            }
        }

        @Override
        public int getRows() {
            return AreaWithOffset.this.size;
        }

        @Override
        public int getColumns() {
            return 1;
        }

        @Override
        public int getSize() {
            return AreaWithOffset.this.size;
        }

        @Override
        public int getSlotWidth() {
            return AreaWithOffset.this.getSlotWidth();
        }

        @Override
        public Area createChild(int firstIndex, int size) {
            return AreaWithOffset.this.createChild(firstIndex, size);
        }
    }
}
