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
import de.codecrafter47.taboverlay.config.context.ContextKeys;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.util.List;

public class OtherCountPlaceholderResolver implements PlaceholderResolver<Context> {
    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "other_count".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            args.remove(0);
            return builder.acquireData(OtherCountDataProvider::new, TypeToken.INTEGER, false);
        }
        throw new UnknownPlaceholderException();
    }

    private static class OtherCountDataProvider implements PlaceholderDataProvider<Context, Integer> {

        private Context context;

        @Override
        public void activate(Context context, Runnable listener) {
            this.context = context;
        }

        @Override
        public void deactivate() {

        }

        @Override
        public Integer getData() {
            return context.getCustomObject(ContextKeys.OTHER_COUNT);
        }
    }
}
