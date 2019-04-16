package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.area.RectangularArea;
import de.codecrafter47.taboverlay.config.view.icon.IconView;
import de.codecrafter47.taboverlay.config.view.icon.IconViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.ping.PingView;
import de.codecrafter47.taboverlay.config.view.ping.PingViewUpdateListener;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.List;
import java.util.concurrent.Future;

public class ListComponentView extends ComponentView implements TextViewUpdateListener, PingViewUpdateListener, IconViewUpdateListener {

    protected final List<ComponentView> components;
    protected final int columns;
    private final TextView defaultTextView;
    private final PingView defaultPingView;
    private final IconView defaultIconView;
    private int minSize, preferredSize, maxSize;
    private boolean blockAligned;
    private Future<?> updateFuture = null;
    private int[] sectionSize;

    public ListComponentView(List<ComponentView> components, int columns, TextView defaultTextView, PingView defaultPingView, IconView defaultIconView) {
        this.components = components;
        this.columns = columns;
        this.defaultTextView = defaultTextView;
        this.defaultPingView = defaultPingView;
        this.defaultIconView = defaultIconView;
        this.sectionSize = new int[this.components.size()];
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        defaultIconView.activate(getContext(), this);
        defaultTextView.activate(getContext(), this);
        defaultPingView.activate(getContext(), this);

        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            component.activate(getContext(), this);
        }

        updateLayoutRequirements(false);
    }

    private void update() {
        updateLayoutRequirements(true);
        if (getArea() != null) {
            updateLayout();
        }
    }

    @Override
    protected void onAreaUpdated() {
        if (getArea() != null) {
            updateLayout();
        }
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
        return blockAligned;
    }

    @Override
    protected void onDeactivation() {
        defaultIconView.deactivate();
        defaultTextView.deactivate();
        defaultPingView.deactivate();

        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            component.deactivate();
        }
        if (updateFuture != null) {
            updateFuture.cancel(false);
        }
        super.onDeactivation();
    }

    protected void updateLayoutRequirements(boolean notify) {
        int minSize = 0;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                minSize = ((minSize + columns - 1) / columns) * columns;
            }
            minSize += component.getMinSize();
        }
        int preferredSize = 0;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                preferredSize = ((preferredSize + columns - 1) / columns) * columns;
            }
            preferredSize += component.getPreferredSize();
        }
        int maxSize = 0;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                maxSize = ((maxSize + columns - 1) / columns) * columns;
            }
            maxSize += component.getMaxSize();
        }
        boolean blockAligned = false;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                blockAligned = true;
            }
        }

        if (minSize != this.minSize
                || preferredSize != this.preferredSize
                || maxSize != this.maxSize
                || blockAligned != this.blockAligned) {
            this.minSize = minSize;
            this.preferredSize = preferredSize;
            this.maxSize = maxSize;
            this.blockAligned = blockAligned;
            if (notify && hasListener()) {
                getListener().requestLayoutUpdate(this);
            }
        }
    }

    private void updateLayout() {
        this.sectionSize = new int[this.components.size()];
        for (int i = 0; i < components.size(); i++) {
            sectionSize[i] = components.get(i).getMinSize();
        }

        RectangularArea area = getArea().asRectangularArea();

        int sizeNeeded = 0;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                sizeNeeded = ((sizeNeeded + columns - 1) / columns) * columns;
            }
            sizeNeeded += sectionSize[i];
        }

        boolean repeat;
        boolean max = false;
        do {
            repeat = false;

            for (int i = 0; i < components.size(); i++) {
                ComponentView component = components.get(i);
                int oldSectionSize = sectionSize[i];
                if (oldSectionSize >= (max ? component.getMaxSize() : component.getPreferredSize())) {
                    continue;
                }
                sectionSize[i] += component.isBlockAligned() ? columns : 1;

                sizeNeeded = 0;
                for (int j = 0; j < components.size(); j++) {
                    ComponentView component1 = components.get(j);
                    if (component1.isBlockAligned()) {
                        sizeNeeded = ((sizeNeeded + columns - 1) / columns) * columns;
                    }
                    sizeNeeded += sectionSize[j];
                }

                if (sizeNeeded <= area.getSize()) {
                    repeat = true;
                } else {
                    sectionSize[i] = oldSectionSize;
                }
            }

            if (!repeat && !max) {
                max = true;
                repeat = true;
            }
        } while (repeat);

        int pos = 0;
        for (int i = 0; i < components.size(); i++) {
            ComponentView component = components.get(i);
            if (component.isBlockAligned()) {
                int posNext = ((pos + columns - 1) / columns) * columns;
                setSlotsToDefault(pos, posNext);
                pos = posNext;
                if (pos < area.getSize()) {
                    component.updateArea(area.createRectangularChild(0, pos / columns, area.getColumns(), sectionSize[i] / columns));
                } else {
                    component.updateArea(null);
                }
            } else {
                if (pos < area.getSize()) {
                    component.updateArea(area.createChild(pos, sectionSize[i]));
                } else {
                    component.updateArea(null);
                }
            }
            pos += sectionSize[i];
        }
        setSlotsToDefault(pos, area.getSize());
    }

    private void updateDefaultSlots() {
        Area area = getArea();
        if (area != null) {
            int pos = 0;
            for (int i = 0; i < components.size(); i++) {
                ComponentView component = components.get(i);
                if (component.isBlockAligned()) {
                    int posNext = ((pos + columns - 1) / columns) * columns;
                    setSlotsToDefault(pos, posNext);
                    pos = posNext;
                }
                pos += sectionSize[i];
            }
            setSlotsToDefault(pos, area.getSize());
        }
    }

    private void setSlotsToDefault(int start, int end) {
        for (int i = start; i < end; i++) {
            getArea().setSlot(i, defaultIconView.getIcon(), defaultTextView.getText(), defaultPingView.getPing());
        }
    }

    @Override
    public void onIconUpdated() {
        updateDefaultSlots();
    }

    @Override
    public void onPingUpdated() {
        updateDefaultSlots();
    }

    @Override
    public void onTextUpdated() {
        updateDefaultSlots();
    }
}
