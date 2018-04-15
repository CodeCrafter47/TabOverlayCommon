package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public final class AnimatedComponentView extends ComponentView {

    private final List<ComponentView> components;
    private final long intervalMS;
    private final int size;

    private ComponentView activeView;
    private Future<?> task;
    private int nextElementIndex;

    public AnimatedComponentView(List<ComponentView> components, float interval, int size) {
        this.components = components;
        this.intervalMS = (long) (interval * 1000);
        this.size = size;
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        task = getContext().getTabEventQueue().scheduleAtFixedRate(this::switchActiveElement, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
        activeView = components.get(0);
        nextElementIndex = 1;
    }

    private void switchActiveElement() {
        activeView.deactivate();
        if (nextElementIndex >= components.size()) {
            nextElementIndex = 0;
        }
        activeView = components.get(nextElementIndex++);
        activeView.activate(getContext(), this);
        Area area = getArea();
        if (area != null) {
            activeView.updateArea(area);
        }
    }

    @Override
    protected void onAreaUpdated() {
        activeView.updateArea(getArea());
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        // todo do we allow components of variable layout to be child of this one???
    }

    @Override
    public int getMinSize() {
        return size;
    }

    @Override
    public int getPreferredSize() {
        return size;
    }

    @Override
    public int getMaxSize() {
        return size;
    }

    @Override
    public boolean isBlockAligned() {
        return activeView.isBlockAligned();
    }

    @Override
    protected void onDeactivation() {
        super.onDeactivation();
        task.cancel(false);
        activeView.deactivate();
    }
}
