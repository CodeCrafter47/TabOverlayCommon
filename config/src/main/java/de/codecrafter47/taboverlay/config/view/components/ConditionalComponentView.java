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

package de.codecrafter47.taboverlay.config.view.components;

import de.codecrafter47.taboverlay.config.area.Area;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.template.component.ComponentTemplate;

import java.util.concurrent.Future;

public final class ConditionalComponentView extends ComponentView implements ExpressionUpdateListener {

    private final ToBooleanExpression condition;
    private final ComponentTemplate trueReplacement;
    private final ComponentTemplate falseReplacement;
    private ComponentView activeReplacement = null;
    private boolean previousResult = false;
    private int minSize, preferredSize, maxSize;
    private boolean blockAligned;
    private Future<?> updateFuture = null;

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
        if (updateFuture == null || updateFuture.isDone()) {
            updateFuture = getContext().getTabEventQueue().submit(this::update);
        }
    }

    private void update() {
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
        if (updateFuture != null) {
            updateFuture.cancel(false);
        }
        super.onDeactivation();
    }
}
