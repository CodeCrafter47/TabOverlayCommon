package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import javax.annotation.Nullable;
import java.util.concurrent.Future;

/**
 * Base class for all ComponentViews, implements some common functionality.
 */
public abstract class ComponentView extends AbstractActiveElement<ComponentView> {

    @Nullable
    private Area area = null;

    @Override
    protected void onActivation() {

    }

    protected abstract void onAreaUpdated();

    protected abstract void requestLayoutUpdate(ComponentView source);

    public abstract int getMinSize();

    public abstract int getPreferredSize();

    public abstract int getMaxSize();

    public abstract boolean isBlockAligned();

    @Override
    protected void onDeactivation() {
        this.area = null;
    }

    public final void updateArea(Area area) {
        this.area = area;
        onAreaUpdated();
    }

    @Nullable
    protected final Area getArea() {
        return this.area;
    }
}
