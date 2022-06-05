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

package de.codecrafter47.taboverlay.config.placeholder;

import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.concurrent.Future;

public class CustomPlaceholderConditional extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener, TextViewUpdateListener {

    private final ToBooleanExpression condition;
    private final TextTemplate trueReplacement;
    private final TextTemplate falseReplacement;
    private TextView activeReplacement;
    private Future<?> updateFuture = null;

    public CustomPlaceholderConditional(ExpressionTemplate condition, TextTemplate trueReplacement, TextTemplate falseReplacement) {
        this.condition = condition.instantiateWithBooleanResult();
        this.trueReplacement = trueReplacement;
        this.falseReplacement = falseReplacement;
    }

    @Override
    protected void onActivation() {
        this.condition.activate(getContext(), this);
        update(false);
    }

    private void update(boolean fireEvent) {
        if (!isActive()) {
            return;
        }
        if (activeReplacement != null) {
            activeReplacement.deactivate();
        }
        boolean result = condition.evaluate();
        if (result) {
            activeReplacement = trueReplacement.instantiate();
        } else {
            activeReplacement = falseReplacement.instantiate();
        }
        activeReplacement.activate(getContext(), this);

        if (fireEvent && hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onDeactivation() {
        if (activeReplacement != null) {
            activeReplacement.deactivate();
        }
        condition.deactivate();
    }

    @Override
    public void onExpressionUpdate() {
        if (updateFuture == null || updateFuture.isDone()) {
            updateFuture = getContext().getTabEventQueue().submit(() -> update(true));
        }
    }

    @Override
    public void onTextUpdated() {
        if (hasListener()) {
            getListener().run();
        }
    }

    @Override
    public String getData() {
        return activeReplacement.getText();
    }
}
