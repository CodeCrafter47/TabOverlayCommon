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
import de.codecrafter47.taboverlay.handler.RectangularTabOverlay;
import de.codecrafter47.taboverlay.handler.SimpleTabOverlay;

import java.util.UUID;

public interface RectangularArea extends Area {

    @Override
    default void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
        setSlot(index % getColumns(), index / getColumns(), uuid, icon, text, ping);
    }

    @Override
    default void setSlot(int index, Icon icon, String text, char alternateColorChar, int ping) {
        setSlot(index % getColumns(), index / getColumns(), icon, text, alternateColorChar, ping);
    }

    @Override
    default void setSlot(int index, Icon icon, String text, int ping) {
        setSlot(index % getColumns(), index / getColumns(), icon, text, ping);
    }

    @Override
    default void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
        setSlot(index % getColumns(), index / getColumns(), uuid, icon, text, alternateColorChar, ping);
    }

    @Override
    default void setUuid(int index, UUID uuid) {
        setUuid(index % getColumns(), index / getColumns(), uuid);
    }

    @Override
    default void setIcon(int index, Icon icon) {
        setIcon(index % getColumns(), index / getColumns(), icon);
    }

    @Override
    default void setText(int index, String text) {
        setText(index % getColumns(), index / getColumns(), text);
    }

    @Override
    default void setText(int index, String text, char alternateColorChar) {
        setText(index % getColumns(), index / getColumns(), text, alternateColorChar);
    }

    @Override
    default void setPing(int index, int ping) {
        setPing(index % getColumns(), index / getColumns(), ping);
    }

    default void setSlot(int column, int row, Icon icon, String text, int ping) {
        setSlot(column, row, null, icon, text, ping);
    }

    default void setSlot(int column, int row, Icon icon, String text, char alternateColorChar, int ping) {
        setSlot(column, row, null, icon, text, alternateColorChar, ping);
    }

    void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping);

    void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping);

    void setUuid(int column, int row, UUID uuid);

    void setIcon(int column, int row, Icon icon);

    void setText(int column, int row, String text);

    void setText(int column, int row, String text, char alternateColorChar);

    void setPing(int column, int row, int ping);

    int getRows();

    int getColumns();

    default RectangularArea createRectangularChild(int column, int row, int columns, int rows) {
        Preconditions.checkArgument(column < getColumns(), "column < getColumns()");
        Preconditions.checkArgument(row < getRows(), "row < getRows()");
        Preconditions.checkArgument(column + columns <= getColumns(), "column + columns <= getColumns()");
        Preconditions.checkArgument(row + rows <= getRows(), "row + rows <= getRows()");
        return new RectangularAreaWithOffset(this, column, row, columns, rows);
    }

    @Override
    default RectangularArea asRectangularArea() {
        return this;
    }

    static RectangularArea of(RectangularTabOverlay tabOverlay) {
        int columns = tabOverlay.getSize().getColumns();
        int rows = tabOverlay.getSize().getRows();
        int size = columns * rows;
        int slotWidth = 80;
        switch (columns) {
            case 1:
                slotWidth = 360;
                break;
            case 2:
                slotWidth = 180;
                break;
            case 3:
                slotWidth = 110;
                break;
        }
        int finalSlotWidth = slotWidth;
        return new RectangularArea() {
            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping) {
                tabOverlay.setSlot(column, row, uuid, icon, text, ping);
            }

            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
                tabOverlay.setSlot(column, row, uuid, icon, text, alternateColorChar, ping);
            }

            @Override
            public void setUuid(int column, int row, UUID uuid) {
                tabOverlay.setUuid(column, row, uuid);
            }

            @Override
            public void setIcon(int column, int row, Icon icon) {
                tabOverlay.setIcon(column, row, icon);
            }

            @Override
            public void setText(int column, int row, String text) {
                tabOverlay.setText(column, row, text);
            }

            @Override
            public void setText(int column, int row, String text, char alternateColorChar) {
                tabOverlay.setText(column, row, text, alternateColorChar);
            }

            @Override
            public void setPing(int column, int row, int ping) {
                tabOverlay.setPing(column, row, ping);
            }

            @Override
            public int getRows() {
                return rows;
            }

            @Override
            public int getColumns() {
                return columns;
            }

            @Override
            public int getSize() {
                return size;
            }

            @Override
            public int getSlotWidth() {
                return finalSlotWidth;
            }
        };
    }

    static Area of(SimpleTabOverlay tabOverlay) {
        int size = tabOverlay.getSize();
        int slotWidth = 80;
        if (size <= 20) {
            slotWidth = 360;
        } else if (size <= 40) {
            slotWidth = 180;
        } else if (size <= 60) {
            slotWidth = 110;
        }
        int finalSlotWidth = slotWidth;
        return new RectangularArea() {
            @Override
            public void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
                tabOverlay.setSlot(index, uuid, icon, text, alternateColorChar, ping);
            }

            @Override
            public void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
                tabOverlay.setSlot(index, uuid, icon, text, ping);
            }

            @Override
            public void setSlot(int index, Icon icon, String text, char alternateColorChar, int ping) {
                tabOverlay.setSlot(index, icon, text, alternateColorChar, ping);
            }

            @Override
            public void setSlot(int index, Icon icon, String text, int ping) {
                tabOverlay.setSlot(index, icon, text, ping);
            }

            @Override
            public void setUuid(int index, UUID uuid) {
                tabOverlay.setUuid(index, uuid);
            }

            @Override
            public void setIcon(int index, Icon icon) {
                tabOverlay.setIcon(index, icon);
            }

            @Override
            public void setText(int index, String text) {
                tabOverlay.setText(index, text);
            }

            @Override
            public void setText(int index, String text, char alternateColorChar) {
                tabOverlay.setText(index, text, alternateColorChar);
            }

            @Override
            public void setPing(int index, int ping) {
                tabOverlay.setPing(index, ping);
            }

            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping) {
                if (column == 0) {
                    tabOverlay.setSlot(row, uuid, icon, text, ping);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setSlot(int column, int row, Icon icon, String text, int ping) {
                if (column == 0) {
                    tabOverlay.setSlot(row, icon, text, ping);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setSlot(int column, int row, Icon icon, String text, char alternateColorChar, int ping) {
                if (column == 0) {
                    tabOverlay.setSlot(row, icon, text, alternateColorChar, ping);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
                if (column == 0) {
                    tabOverlay.setSlot(row, uuid, icon, text, alternateColorChar, ping);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setUuid(int column, int row, UUID uuid) {
                if (column == 0) {
                    tabOverlay.setUuid(row, uuid);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setIcon(int column, int row, Icon icon) {
                if (column == 0) {
                    tabOverlay.setIcon(row, icon);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setText(int column, int row, String text) {
                if (column == 0) {
                    tabOverlay.setText(row, text);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setText(int column, int row, String text, char alternateColorChar) {
                if (column == 0) {
                    tabOverlay.setText(row, text, alternateColorChar);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public void setPing(int column, int row, int ping) {
                if (column == 0) {
                    tabOverlay.setPing(row, ping);
                } else {
                    throw new IndexOutOfBoundsException("Index c=" + column + "r=" + row + " out of bounds(columns=1, rows=" + getRows() + ").");
                }
            }

            @Override
            public int getRows() {
                return tabOverlay.getSize();
            }

            @Override
            public int getColumns() {
                return 1;
            }

            @Override
            public int getSize() {
                return tabOverlay.getSize();
            }

            @Override
            public int getSlotWidth() {
                return finalSlotWidth;
            }

            @Override
            public RectangularArea asRectangularArea() {
                return this;
            }
        };
    }
}
