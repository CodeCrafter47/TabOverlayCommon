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

import de.codecrafter47.taboverlay.config.expression.ToBooleanExpression;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.ToStringExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.text.TextTemplate;
import de.codecrafter47.taboverlay.config.view.text.TextView;

import javax.annotation.Nonnull;

public interface Placeholder extends TextTemplate, ExpressionTemplate {

    Placeholder DUMMY = new Placeholder() {
        @Override
        public ToStringExpression instantiateWithStringResult() {
            return ToStringExpression.literal("");
        }

        @Override
        public ToDoubleExpression instantiateWithDoubleResult() {
            return ToDoubleExpression.literal(0.0);
        }

        @Override
        public ToBooleanExpression instantiateWithBooleanResult() {
            return ToBooleanExpression.literal(false);
        }

        @Override
        public boolean requiresViewerContext() {
            return false;
        }

        @Nonnull
        @Override
        public TextView instantiate() {
            return TextView.EMPTY;
        }
    };

}
