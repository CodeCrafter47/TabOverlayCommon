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
import de.codecrafter47.taboverlay.config.ConfigTabOverlayManager;
import de.codecrafter47.taboverlay.config.context.Context;
import de.codecrafter47.taboverlay.config.template.TemplateCreationContext;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimePlaceholderResolver implements PlaceholderResolver<Context> {

    private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("HH:mm:ss");
    // todo replace with type token from data api once available
    private static final TypeToken<Long> TYPE_TOKEN_LONG = TypeToken.create();
    private final ConfigTabOverlayManager configTabOverlayManager;

    public TimePlaceholderResolver(ConfigTabOverlayManager configTabOverlayManager) {
        this.configTabOverlayManager = configTabOverlayManager;
    }

    @Nonnull
    @Override
    public PlaceholderBuilder<?, ?> resolve(PlaceholderBuilder<Context, ?> builder, List<PlaceholderArg> args, TemplateCreationContext tcc) throws UnknownPlaceholderException, PlaceholderException {
        if (args.size() >= 1 && args.get(0) instanceof PlaceholderArg.Text && "time".equalsIgnoreCase(((PlaceholderArg.Text) args.get(0)).getValue())) {
            SimpleDateFormat format = DEFAULT_FORMAT;
            if (args.size() > 1) {
                StringBuilder formatString = new StringBuilder();
                for (int i = 1; i < args.size(); i++) {
                    if (args.get(i) instanceof PlaceholderArg.Text) {
                        String s = ((PlaceholderArg.Text) args.get(i)).getValue();
                        if (i != 1) {
                            formatString.append(' ');
                        }
                        formatString.append(s);
                    } else {
                        throw new PlaceholderException("Use of placeholder in time format is not allowed");
                    }
                }
                try {
                    TimeZone timeZone = configTabOverlayManager.getTimeZone();
                    format = new SimpleDateFormat(formatString.toString());
                    if (timeZone != null) {
                        format.setTimeZone(timeZone);
                    }
                } catch (IllegalArgumentException ex) {
                    throw new PlaceholderException("Invalid time format", ex);
                }
            }
            args.clear();
            return builder.acquireData(TimeProvider::new, TYPE_TOKEN_LONG, false)
                    .transformData(format::format, TypeToken.STRING);
        }
        throw new UnknownPlaceholderException();
    }

    private static class TimeProvider implements PlaceholderDataProvider<Context, Long> {

        private ScheduledFuture<?> future;

        @Override
        public void activate(Context context, Runnable listener) {
            // todo can do better if seconds not used?
            future = context.getTabEventQueue().scheduleWithFixedDelay(listener, 1, 1, TimeUnit.SECONDS);
        }

        @Override
        public void deactivate() {
            future.cancel(false);
        }

        @Override
        public Long getData() {
            return System.currentTimeMillis();
        }
    }
}
