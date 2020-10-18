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

package de.codecrafter47.taboverlay.config.expression;

import lombok.RequiredArgsConstructor;

import java.util.Collection;

@RequiredArgsConstructor
public abstract class AbstractToBooleanExpression<T extends Expression> extends ExpressionBase implements ToBooleanExpression {

    protected final Collection<T> operands;

    @Override
    protected void onActivation() {
        for (T operand : operands) {
            operand.activate(getContext(), getListener());
        }
    }

    @Override
    protected void onDeactivation() {
        for (T operand : operands) {
            operand.deactivate();
        }
    }
}
