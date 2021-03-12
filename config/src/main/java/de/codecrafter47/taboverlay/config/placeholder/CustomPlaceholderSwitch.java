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
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;
import de.codecrafter47.taboverlay.config.view.text.TextView;
import de.codecrafter47.taboverlay.config.view.text.TextViewUpdateListener;

import java.util.Map;
import java.util.concurrent.Future;

public class CustomPlaceholderSwitch extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, String>, ExpressionUpdateListener, TextViewUpdateListener {

    private final ToStringExpression expression;
    private final Map<String, TextTemplate> replacements;
    private final TextTemplate defaultReplacement;
    private TextView activeView;
    private Future<?> updateFuture = null;

    public CustomPlaceholderSwitch(ExpressionTemplate expression, Map<String, TextTemplate> replacements, TextTemplate defaultReplacement) {
        this.expression = expression.instantiateWithStringResult();
        this.replacements = replacements;
        this.defaultReplacement = defaultReplacement;
    }


    private void update(boolean fireEvent) {
        if (!isActive()) {
            return;
        }
        if (this.activeView != null) {
            this.activeView.deactivate();
        }
        String result = expression.evaluate();
        TextTemplate template = replacements.getOrDefault(result, defaultReplacement);
        activeView = template.instantiate();
        activeView.activate(getContext(), this);

        if (fireEvent && hasListener()) {
            getListener().run();
        }
    }

    @Override
    protected void onActivation() {
        this.expression.activate(getContext(), this);
        update(false);
    }

    @Override
    protected void onDeactivation() {
        if (activeView != null) {
            activeView.deactivate();
        }
        expression.deactivate();
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
        return activeView.getText();
    }

}







