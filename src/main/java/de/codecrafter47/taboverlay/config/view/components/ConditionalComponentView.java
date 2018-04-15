package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;

public final class ConditionalComponentView extends ComponentView implements ExpressionUpdateListener {

    private final ToBooleanExpression condition;
    private final ComponentTemplate trueReplacement;
    private final ComponentTemplate falseReplacement;
    private ComponentView activeReplacement = null;
    private boolean previousResult = false;
    private int minSize, preferredSize, maxSize;
    private boolean blockAligned;

    public ConditionalComponentView(ToBooleanExpression condition, ComponentTemplate trueReplacement, ComponentTemplate falseReplacement) {
        this.condition = condition;
        this.trueReplacement = trueReplacement;
        this.falseReplacement = falseReplacement;
    }

    @Override
    protected void onActivation() {
        super.onActivation();

        condition.activate(getContext(), this);
        boolean result = condition.evaluate();
        if (activeReplacement != null) {
            activeReplacement.deactivate();
        }
        if (result) {
            activeReplacement = trueReplacement.instantiate();
        } else {
            activeReplacement = falseReplacement.instantiate();
        }
        activeReplacement.activate(getContext(), this);
        previousResult = result;

        minSize = activeReplacement.getMinSize();
        preferredSize = activeReplacement.getPreferredSize();
        maxSize = activeReplacement.getMaxSize();
        blockAligned = activeReplacement.isBlockAligned();
    }

    @Override
    public void onExpressionUpdate() {
        boolean result = condition.evaluate();
        if (previousResult != result) {
            if (activeReplacement != null) {
                activeReplacement.deactivate();
            }
            if (result) {
                activeReplacement = trueReplacement.instantiate();
            } else {
                activeReplacement = falseReplacement.instantiate();
            }
            activeReplacement.activate(getContext(), this);
        }
        previousResult = result;

        updateLayout();
    }

    private void updateLayout() {
        int minSize = activeReplacement.getMinSize();
        int preferredSize = activeReplacement.getPreferredSize();
        int maxSize = activeReplacement.getMaxSize();
        boolean blockAligned = activeReplacement.isBlockAligned();

        if (minSize != this.minSize
                || preferredSize != this.preferredSize
                || maxSize != this.maxSize
                || blockAligned != this.blockAligned) {
            this.minSize = minSize;
            this.preferredSize = preferredSize;
            this.maxSize = maxSize;
            this.blockAligned = blockAligned;
            if (hasListener()) {
                getListener().requestLayoutUpdate(this);
            }
        } else {
            Area area = getArea();
            if (area != null) {
                activeReplacement.updateArea(area);
            }
        }
    }

    @Override
    protected void onAreaUpdated() {
        activeReplacement.updateArea(getArea());
    }

    @Override
    protected void requestLayoutUpdate(ComponentView source) {
        updateLayout();
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
        condition.deactivate();
        activeReplacement.deactivate();
        super.onDeactivation();
    }
}
