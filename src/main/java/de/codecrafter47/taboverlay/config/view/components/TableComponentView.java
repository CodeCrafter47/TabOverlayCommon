package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;
import lombok.Value;

import java.util.List;
import java.util.concurrent.Future;

public final class TableComponentView extends ComponentView implements DefaultSlotHandler.Listener {

    private final List<Column> columns;
    /* a value of -1 denotes dynamic size */
    private final int size;
    private final int columnCount;
    private final DefaultSlotHandler defaultSlotHandler;
    private Future<?> updateFuture = null;
    private int minSize, preferredSize, maxSize;

    public TableComponentView(List<Column> columns, int size, int columnCount, TextView defaultTextView, PingView defaultPingView, IconView defaultIconView) {
        this.columns = columns;
        this.size = size;
        this.columnCount = columnCount;
        this.defaultSlotHandler = new DefaultSlotHandler(defaultTextView, defaultPingView, defaultIconView);
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        defaultSlotHandler.activate(getContext(), this);

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            column.component.activate(getContext(), this);
        }
        updateLayoutRequirements(false);
    }

    private void update() {
        updateLayoutRequirements(true);
        updateLayout();
    }

    @Override
    protected void onAreaUpdated() {
        updateLayout();
    }

    private void updateLayout() {
        Area area = getArea();
        if (area != null) {
            RectangularArea rArea = area.asRectangularArea();
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                column.component.updateArea(rArea.createRectangularChild(column.columnIndex, 0, column.width, rArea.getRows()));
            }
        }
        updateDefaultSlots();
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        if (updateFuture == null || updateFuture.isDone()) {
            updateFuture = getContext().getTabEventQueue().submit(this::update);
        }
    }

    @Override
    public int getMinSize() {
        return minSize;
    }

    @Override
    public int getPreferredSize() {
        return preferredSize;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean isBlockAligned() {
        return true;
    }

    @Override
    protected void onDeactivation() {
        defaultSlotHandler.deactivate();

        for (Column column : columns) {
            column.component.deactivate();
        }
        super.onDeactivation();
    }

    private void updateLayoutRequirements(boolean notify) {
        if (size != -1) {
            this.minSize = size;
            this.preferredSize = size;
            this.maxSize = size;
        } else {
            int minSize = 0;
            int preferredSize = 0;
            int maxSize = 0;
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                minSize = Integer.max(minSize, (column.component.getMinSize() + column.width - 1) / column.width);
                preferredSize = Integer.max(preferredSize, (column.component.getPreferredSize() + column.width - 1) / column.width);
                maxSize = Integer.max(maxSize, (column.component.getMaxSize() + column.width - 1) / column.width);
            }
            minSize *= columnCount;
            preferredSize *= columnCount;
            maxSize *= columnCount;
            if (minSize != this.minSize
                    || preferredSize != this.preferredSize
                    || maxSize != this.maxSize) {
                this.minSize = minSize;
                this.preferredSize = preferredSize;
                this.maxSize = maxSize;
                if (notify && hasListener()) {
                    getListener().requestLayoutUpdate(this);
                }
            }
        }
    }

    private void updateDefaultSlots() {
        updateDefaultSlots((area, column, row, defaultSlot) -> area.setSlot(column, row, defaultSlot.getIcon(), defaultSlot.getText(), '&', defaultSlot.getPing()));
    }

    private void updateDefaultSlots(DefaultSlotUpdateFunction updateFunction) {
        Area area = getArea();
        if (area != null) {
            RectangularArea rArea = area.asRectangularArea();
            int i = 0;
            for (int column = 0; column < rArea.getColumns(); column++) {
                int rowsUsed = 0;
                while (i < columns.size() && column >= columns.get(i).columnIndex + columns.get(i).width) {
                    i++;
                }
                if (i < columns.size() && column >= columns.get(i).columnIndex) {
                    rowsUsed = columns.get(i).component.getArea().asRectangularArea().getRows();
                }
                for (int row = rowsUsed; row < rArea.getRows(); row++) {
                    updateFunction.apply(rArea, column, row, defaultSlotHandler);
                }
            }
        }
    }

    @Override
    public void onDefaultSlotTextUpdated() {
        updateDefaultSlots((area, column, row, defaultSlot) -> area.setText(column, row, defaultSlot.getText(), '&'));
    }

    @Override
    public void onDefaultSlotPingUpdated() {
        updateDefaultSlots((area, column, row, defaultSlot) -> area.setPing(column, row, defaultSlot.getPing()));
    }

    @Override
    public void onDefaultSlotIconUpdated() {
        updateDefaultSlots((area, column, row, defaultSlot) -> area.setIcon(column, row, defaultSlot.getIcon()));
    }

    @Value
    public static class Column {
        private int columnIndex;
        private int width;
        private ComponentView component;
    }

    interface DefaultSlotUpdateFunction {
        void apply(RectangularArea area, int column, int row, DefaultSlotHandler defaultSlotHandler);
    }
}
