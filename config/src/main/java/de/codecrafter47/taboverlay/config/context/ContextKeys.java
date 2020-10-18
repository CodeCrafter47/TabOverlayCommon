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

package de.codecrafter47.taboverlay.config.context;

import de.codecrafter47.taboverlay.config.expression.template.ExpressionTemplate;

public class ContextKeys {

    public static final ContextKey<Integer> OTHER_COUNT = new ContextKey<>("OTHER_COUNT");
    public static final ContextKey<ExpressionTemplate> BAR_VALUE = new ContextKey<>("BAR_VALUE");
    public static final ContextKey<ExpressionTemplate> BAR_PERCENTAGE = new ContextKey<>("BAR_PERCENTAGE");
    public static final ContextKey<ExpressionTemplate> BAR_MIN_VALUE = new ContextKey<>("BAR_MIN_VALUE");
    public static final ContextKey<ExpressionTemplate> BAR_MAX_VALUE = new ContextKey<>("BAR_MAX_VALUE");
}
