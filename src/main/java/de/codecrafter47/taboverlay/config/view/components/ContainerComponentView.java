package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.Icon;
import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.area.RectangularArea;

import java.util.UUID;

public final class ContainerComponentView extends ComponentView {

    private final ComponentView content;
    private final boolean fillSlotsVertical;
    private final int minSize;
    private final int maxSize; // -1 denotes no limit
    private final int columns;
    private final boolean forceBlock;

    public ContainerComponentView(ComponentView content, boolean fillSlotsVertical, int minSize, int maxSize, int columns, boolean forceBlock) {
        this.content = content;
        this.fillSlotsVertical = fillSlotsVertical;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.columns = columns;
        this.forceBlock = forceBlock;
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        content.activate(getContext(), this);
    }

    @Override
    protected void onAreaUpdated() {
        Area area = getArea();
        if (area != null) {
            if (fillSlotsVertical) {
                content.updateArea(new TransformedArea(area.asRectangularArea()));
            } else {
                content.updateArea(area);
            }
        } else {
            content.updateArea(null);
        }
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        if (hasListener()) {
            getListener().requestLayoutUpdate(this);
        }
    }

    @Override
    public int getMinSize() {
        int minSize = content.getMinSize();
        if (forceBlock) {
            minSize = ((minSize + columns - 1) / columns) * columns;
        }
        if (this.minSize >= 0) {
            minSize = Integer.max(minSize, this.minSize);
        }
        return minSize;
    }

    @Override
    public int getPreferredSize() {
        int preferredSize = content.getPreferredSize();
        if (forceBlock) {
            preferredSize = ((preferredSize + columns - 1) / columns) * columns;
        }
        if (fillSlotsVertical) {
            preferredSize *= columns;
        }
        if (minSize >= 0) {
            preferredSize = Integer.max(preferredSize, minSize);
        }
        if (maxSize >= 0) {
            preferredSize = Integer.min(preferredSize, maxSize);
        }
        return preferredSize;
    }

    @Override
    public int getMaxSize() {
        int maxSize = content.getMaxSize();
        if (forceBlock) {
            maxSize = ((maxSize + columns - 1) / columns) * columns;
        }
        if (fillSlotsVertical) {
            maxSize *= columns;
        }
        if (this.maxSize >= 0) {
            maxSize = Integer.min(maxSize, this.maxSize);
        }
        return maxSize;
    }

    @Override
    public boolean isBlockAligned() {
        return forceBlock || fillSlotsVertical || content.isBlockAligned();
    }

    @Override
    protected void onDeactivation() {
        content.deactivate();
        super.onDeactivation();
    }

    private static class TransformedArea implements Area {
        private final RectangularArea delegate;
        private final RectangularArea rectangularView;

        private TransformedArea(RectangularArea delegate) {
            this.delegate = delegate;
            this.rectangularView = new RectangularView();
        }

        @Override
        public void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
            delegate.setSlot(index / delegate.getRows(), index % delegate.getRows(), uuid, icon, text, ping);
        }

        @Override
        public void setUuid(int index, UUID uuid) {
            delegate.setUuid(index / delegate.getRows(), index % delegate.getRows(), uuid);
        }

        @Override
        public void setIcon(int index, Icon icon) {
            delegate.setIcon(index / delegate.getRows(), index % delegate.getRows(), icon);
        }

        @Override
        public void setText(int index, String text) {
            delegate.setText(index / delegate.getRows(), index % delegate.getRows(), text);
        }

        @Override
        public void setText(int index, String text, char alternateColorChar) {
            delegate.setText(index / delegate.getRows(), index % delegate.getRows(), text, alternateColorChar);
        }

        @Override
        public void setPing(int index, int ping) {
            delegate.setPing(index / delegate.getRows(), index % delegate.getRows(), ping);
        }

        @Override
        public void setSlot(int index, Icon icon, String text, int ping) {
            delegate.setSlot(index / delegate.getRows(), index % delegate.getRows(), icon, text, ping);
        }

        @Override
        public void setSlot(int index, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
            delegate.setSlot(index / delegate.getRows(), index % delegate.getRows(), uuid, icon, text, alternateColorChar, ping);
        }

        @Override
        public int getSize() {
            return this.delegate.getSize();
        }

        @Override
        public RectangularArea asRectangularArea() {
            return rectangularView;
        }

        private class RectangularView implements RectangularArea {// todo pretty much duplicate of the one in AreaWithOffset

            @Override
            public void setSlot(int index, UUID uuid, Icon icon, String text, int ping) {
                TransformedArea.this.setSlot(index, uuid, icon, text, ping);
            }

            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, int ping) {
                if (column == 0) {
                    TransformedArea.this.setSlot(row, uuid, icon, text, ping);
                }
            }

            @Override
            public void setSlot(int column, int row, UUID uuid, Icon icon, String text, char alternateColorChar, int ping) {
                if (column == 0) {
                    TransformedArea.this.setSlot(row, uuid, icon, text, alternateColorChar, ping);
                }

            }

            @Override
            public void setUuid(int column, int row, UUID uuid) {
                if (column == 0) {
                    TransformedArea.this.setUuid(row, uuid);
                }
            }

            @Override
            public void setIcon(int column, int row, Icon icon) {
                if (column == 0) {
                    TransformedArea.this.setIcon(row, icon);
                }
            }

            @Override
            public void setText(int column, int row, String text) {
                if (column == 0) {
                    TransformedArea.this.setText(row, text);
                }
            }

            @Override
            public void setText(int column, int row, String text, char alternateColorChar) {
                if (column == 0) {
                    TransformedArea.this.setText(row, text, alternateColorChar);
                }
            }

            @Override
            public void setPing(int column, int row, int ping) {
                if (column == 0) {
                    TransformedArea.this.setPing(row, ping);
                }
            }

            @Override
            public int getRows() {
                return TransformedArea.this.delegate.getSize();
            }

            @Override
            public int getColumns() {
                return 1;
            }

            @Override
            public int getSize() {
                return TransformedArea.this.delegate.getSize();
            }

            @Override
            public Area createChild(int firstIndex, int size) {
                return TransformedArea.this.createChild(firstIndex, size);
            }
        }
    }
}
