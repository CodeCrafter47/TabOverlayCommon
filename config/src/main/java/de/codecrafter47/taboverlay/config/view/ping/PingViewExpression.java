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

package de.codecrafter47.taboverlay.config.view.ping;

import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

public class PingViewExpression extends AbstractActiveElement<PingViewUpdateListener> implements PingView, ExpressionUpdateListener {

    private final ToDoubleExpression expression;

    public PingViewExpression(ToDoubleExpression expression) {
        this.expression = expression;
    }

    @Override
    public int getPing() {
        return (int) expression.evaluate();
    }

    @Override
    protected void onActivation() {
        expression.activate(getContext(), this);
    }

    @Override
    protected void onDeactivation() {
        expression.deactivate();
    }

    @Override
    public void onExpressionUpdate() {
        if (hasListener()) {
            getListener().onPingUpdated();
        }
    }
}
