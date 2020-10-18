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

import de.codecrafter47.data.api.TypeToken;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.context.ContextKey;
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.expression.ExpressionUpdateListener;
import de.codecrafter47.taboverlay.config.expression.ToDoubleExpression;
import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;
import de.codecrafter47.taboverlay.config.view.AbstractActiveElement;

import javax.annotation.Nonnull;
import java.util.List;

public class BarPlaceholderResolver implements PlaceholderResolver<Context> {
    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "progress_percentage".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(() -> new BarDataProvider(ContextKeys.BAR_PERCENTAGE), TypeToken.DOUBLE, false);
        }
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "progress_value".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(() -> new BarDataProvider(ContextKeys.BAR_VALUE), TypeToken.DOUBLE, false);
        }
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "progress_min".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(() -> new BarDataProvider(ContextKeys.BAR_MIN_VALUE), TypeToken.DOUBLE, false);
        }
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "progress_max".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(() -> new BarDataProvider(ContextKeys.BAR_MAX_VALUE), TypeToken.DOUBLE, false);
        }
        throw new UnknownPlaceholderException();
    }

    private static class BarDataProvider extends AbstractActiveElement<Runnable> implements PlaceholderDataProvider<Context, Double>, ExpressionUpdateListener {

        private final ContextKey<ExpressionTemplate> template;
        private ToDoubleExpression expression;

        private BarDataProvider(ContextKey<ExpressionTemplate> template) {
            this.template = template;
        }

        @Override
        protected void onActivation() {
            expression = getContext().getCustomObject(template).instantiateWithDoubleResult();
            expression.activate(getContext(), this);
        }

        @Override
        protected void onDeactivation() {
            expression.deactivate();
        }

        @Override
        public Double getData() {
            return expression.evaluate();
        }

        @Override
        public void onExpressionUpdate() {
            if (hasListener()) {
                getListener().run();
            }
        }
    }
}
